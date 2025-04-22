package com.threads.postservice.service.implementation;

import com.threads.PinPostEvent;
import com.threads.events.CreatePostEvent;
import com.threads.events.PostStatusEvent;
import com.threads.events.UpdatePostEvent;
import com.threads.postservice.dto.PostDto;
import com.threads.postservice.dto.PostUpdateDto;
import com.threads.postservice.dto.ReplyUpdateDto;
import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.PostMedia;
import com.threads.postservice.entity.Repost;
import com.threads.postservice.enums.MediaType;
import com.threads.postservice.enums.ReplyPermission;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.exception.OwnershipException;
import com.threads.postservice.feign.SecurityFeignClient;
import com.threads.postservice.kafka.KafkaPostProducer;
import com.threads.postservice.mapper.PostMapper;
import com.threads.postservice.payload.PostRequest;
import com.threads.postservice.payload.QuotePostRequest;
import com.threads.postservice.payload.ReplyRequest;
import com.threads.postservice.repository.*;
import com.threads.postservice.response.PostResponse;
import com.threads.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final SecurityFeignClient securityFeignClient;
    private final RepostRepository repostRepository;
    private final PostAccessGuard accessGuard;
    private final KafkaPostProducer producer;
    private final LikeRepository likeRepository;
    private final SaveRepository saveRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostMediaService postMediaService;
    private final PostShareRepository shareRepository;
    private final ReportRepository reportRepository;

    @Override
    public PostDto createPost(PostRequest request, List<MultipartFile> media, String authorizationHeader) {
        Post post = mapper.toEntity(request);
        post.setAuthorId(getUserId(authorizationHeader));
        post.setIsPost(true);
        post.setCreatedAt(LocalDateTime.now());
        post.setIsReply(false);
        post.setHidden(false);
        List<String> mediaUrls = new ArrayList<>();
        if (media != null && !media.isEmpty()) {
            List<PostMedia> mediaList;
            mediaList = saveMediaFiles(media, post.getId());
            postMediaRepository.saveAll(mediaList);
            post.setMedia(mediaList);
            mediaUrls.addAll(mediaList.stream().map(PostMedia::getMediaUrl).toList());
        }
        postRepository.save(post);

        CreatePostEvent createPostEvent = createPostEvent(post, mediaUrls);
        producer.sendCreatePostEvent(createPostEvent);

        return mapper.toDto(post);
    }

    @Override
    public PostDto replyToPost(ReplyRequest request, Long postId, List<MultipartFile> media, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(postId, currentUserId);
        accessGuard.checkPostVisible(post);
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        if (post.getReplyPermission().equals(ReplyPermission.FOLLOWERS_ONLY)) {
            accessGuard.checkFollower(currentUserId, post.getAuthorId());
        }
        post.setReplyCount(post.getReplyCount() + 1);
        Post reply = mapper.toEntity(request);
        reply.setAuthorId(getUserId(authorizationHeader));
        reply.setIsReply(true);
        reply.setIsPost(false);
        reply.setOriginalPostId(postId);
        reply.setReplyToPostId(postId);
        reply.setHidden(false);
        reply.setReplyPermission(ReplyPermission.EVERYONE);
        reply.setCreatedAt(LocalDateTime.now());
        if (post.getReplyToPostId() != null) {
            reply.setOriginalPostId(post.getOriginalPostId());
            reply.setReplyToPostId(post.getId());
        }
        if (media != null && !media.isEmpty()) {
            List<PostMedia> mediaList = saveMediaFiles(media, reply.getId());
            postMediaRepository.saveAll(mediaList);
            reply.setMedia(mediaList);
        }
        postRepository.save(reply);
        postRepository.save(post);
        PostStatusEvent postStatusEvent = new PostStatusEvent(post.getId(), post.getLikeCount(),
                post.getRepostCount(), post.getReplyCount(), post.getSendCount());
        producer.sendPostStatusEvent(postStatusEvent);
        return mapper.toDtoReply(reply);
    }

    @Override
    public PostDto updatePost(PostUpdateDto postDto, Long id, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(id, currentUserId);
        accessGuard.checkOwnership(post.getAuthorId(), currentUserId);
        if (!post.getIsPost()) {
            throw new IllegalArgumentException("Please provide a post!");
        }
        mapper.updatePost(postDto, post);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        UpdatePostEvent updatePostEvent = new UpdatePostEvent(post.getId(), post.getContent(), postDto.getTopic());
        producer.sendUpdatePostEvent(updatePostEvent);
        return mapper.toDto(post);
    }

    @Override
    public PostDto updateReply(ReplyUpdateDto dto, Long id, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post reply = findPostOrThrow(id, currentUserId);
        accessGuard.checkOwnership(reply.getAuthorId(), currentUserId);
        accessGuard.checkPostVisible(reply);
        if (!reply.getIsReply()) {
            throw new IllegalArgumentException("Please provide a reply!");
        }
        mapper.updateReply(dto, reply);
        reply.setUpdatedAt(LocalDateTime.now());
        postRepository.save(reply);
        return mapper.toDto(reply);
    }

    @Override
    public PostResponse getPost(Long id, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(id, currentUserId);
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        return mapper.toResponse(post);
    }

    @Override
    public List<PostResponse> getRepliesOfPost(Long postId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(postId, currentUserId);
        if (post.getAuthorId().equals(currentUserId)) {
            return postRepository.findByOriginalPostIdOrderByLikeCountDesc(postId)
                    .stream().
                    map(mapper::toResponse).toList();
        }
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        return postRepository.findByOriginalPostIdAndHiddenFalseOrderByLikeCountDesc(postId)
                .stream()
                .map(mapper::toResponse).toList();
    }

    @Override
    public PostResponse quotePost(Long id, QuotePostRequest quotePostRequest, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post originalPost = findPostOrThrow(id, currentUserId);
        accessGuard.checkUserAccessToPost(currentUserId, originalPost.getAuthorId());
        accessGuard.checkPostVisible(originalPost);
        Post quote = new Post();
        quote.setAuthorId(currentUserId);
        quote.setContent(quotePostRequest.getContent());
        quote.setTopic(quotePostRequest.getTopic());
        quote.setIsPost(true);
        quote.setIsReply(false);
        quote.setOriginalPostId(originalPost.getId());
        quote.setHidden(false);
        quote.setReplyPermission(ReplyPermission.EVERYONE);
        quote.setCreatedAt(LocalDateTime.now());
        postRepository.save(quote);

        CreatePostEvent createPostEvent = createPostEvent(quote, null);
        createPostEvent.setOriginalPostId(originalPost.getId());
        producer.sendCreatePostEvent(createPostEvent);

        return mapper.toResponse(quote);
    }

    @Override
    public List<PostResponse> getUserPosts(Long authorId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkUserAccessToPost(currentUserId, authorId);
        return postRepository.findByAuthorIdAndIsPostTrueAndHiddenFalseOrderByCreatedAtDesc(authorId)
                .stream()
                .map(mapper::toResponse).toList();
    }

    @Override
    public List<PostResponse> getUserReplies(Long authorId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkUserAccessToPost(currentUserId, authorId);
        return postRepository.findByAuthorIdAndIsReplyTrueAndHiddenFalseOrderByCreatedAtDesc(authorId)
                .stream()
                .map(mapper::toResponse).toList();
    }

    @Override
    public List<PostResponse> getMyPosts(String authorizationHeader) {
        return postRepository.findByAuthorIdAndIsPostTrueAndHiddenFalseOrderByCreatedAtDesc(getUserId(authorizationHeader))
                .stream()
                .map(mapper::toResponse).toList();
    }

    @Override
    public List<PostResponse> getMyReplies(String authorizationHeader) {
        return postRepository.findByAuthorIdAndIsReplyTrueAndHiddenFalseOrderByCreatedAtDesc(getUserId(authorizationHeader))
                .stream()
                .map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void deletePost(Long id, String authorizationHeader) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post " + id + " not found!"));
        Long currentUserId = getUserId(authorizationHeader);

        if (post.getIsReply()) {
            deleteReply(post, currentUserId);
        } else {
            deleteOriginalPost(post, currentUserId);
        }
    }

    protected void deleteOriginalPost(Post post, Long currentUserId) {
        accessGuard.checkOwnership(post.getAuthorId(), currentUserId);
        repostRepository.deleteByPostId(post.getId());
        likeRepository.deleteByPostId(post.getId());
        saveRepository.deleteByPostId(post.getId());
        postMediaRepository.deleteByPostId(post.getId());
        shareRepository.deleteByPostId(post.getId());
        reportRepository.deleteByTargetId(post.getId());
        postRepository.delete(post);
        producer.sendPostDeleteEvent(post.getId());
    }

    protected void deleteReply(Post reply, Long currentUserId) {
        Optional<Post> originalPostOpt = postRepository.findById(reply.getOriginalPostId());
        if (originalPostOpt.isPresent()) {
            Post originalPost = originalPostOpt.get();
            if (accessGuard.checkOwnershipToDelete(originalPost, currentUserId) && accessGuard.checkOwnershipToDelete(reply, currentUserId)) {
                throw new OwnershipException("You don't have access!");
            }
            if (!reply.getHidden()) {
                originalPost.setReplyCount(originalPost.getReplyCount() - 1);
            }
            if (reply.getId().equals(originalPost.getPinnedReplyId())) {
                originalPost.setPinnedReplyId(null);
            }
            postRepository.save(originalPost);
            PostStatusEvent postStatusEvent = new PostStatusEvent(originalPost.getId(), originalPost.getLikeCount(),
                    originalPost.getRepostCount(), originalPost.getReplyCount(), originalPost.getSendCount());
            producer.sendPostStatusEvent(postStatusEvent);
        }
        else {
            if (accessGuard.checkOwnershipToDelete(reply, currentUserId)) {
                throw new OwnershipException("You don't have access!");
            }
        }
        repostRepository.deleteByPostId(reply.getId());
        likeRepository.deleteByPostId(reply.getId());
        saveRepository.deleteByPostId(reply.getId());
        postMediaRepository.deleteByPostId(reply.getId());
        shareRepository.deleteByPostId(reply.getId());
        reportRepository.deleteByTargetId(reply.getId());
        postRepository.delete(reply);
    }

    @Override
    public void archivePost(Long id, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(id, currentUserId);

        accessGuard.checkPostVisible(post);
        if (post.getIsPost()) {
            archiveOriginalPost(post, currentUserId);
        } else {
            archiveReply(post, currentUserId);
        }
        List<Repost> reposts = repostRepository.findByPostId(post.getId())
        .stream()
                .peek(repost -> repost.setHidden(true)).toList();
        repostRepository.saveAll(reposts);
    }

    private void archiveOriginalPost(Post post, Long currentUserId) {
        accessGuard.checkOwnership(post.getAuthorId(), currentUserId);
        List<Post> replies = postRepository.findByOriginalPostId(post.getId());
        replies.forEach(reply -> reply.setHidden(true));
        postRepository.saveAll(replies);
        post.setHidden(true);
        postRepository.save(post);
        producer.sendPostArchiveEvent(post.getId());
    }

    private void archiveReply(Post reply, Long currentUserId) {
        Post originalPost = findPostOrThrow(reply.getOriginalPostId(), currentUserId);
        accessGuard.checkOwnership(originalPost.getAuthorId(), currentUserId);
        accessGuard.checkPostVisible(originalPost);
        reply.setHidden(true);
        originalPost.setReplyCount(originalPost.getReplyCount() - 1);
        if (!(reply.getPinnedReplyId() == null)) {
            reply.setPinnedReplyId(null);
        }
        if (reply.getId().equals(originalPost.getPinnedReplyId())) {
            originalPost.setPinnedReplyId(null);
        }
        postRepository.save(reply);
        postRepository.save(originalPost);
        PostStatusEvent postStatusEvent = new PostStatusEvent(originalPost.getId(), originalPost.getLikeCount(),
                originalPost.getRepostCount(), originalPost.getReplyCount(), originalPost.getSendCount());
        producer.sendPostStatusEvent(postStatusEvent);
    }

    @Override
    public List<PostResponse> getArchivedPosts(String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        return postRepository.findByAuthorIdAndIsPostTrueAndHiddenTrue(currentUserId)
                .stream()
                .map(mapper::toResponse).toList();
    }

    @Override
    public void unarchivePost(Long id, String authorizationHeader) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post " + id + " not found!"));
        Long currentUserId = getUserId(authorizationHeader);
        if (post.getIsPost()) {
            unarchiveOriginalPost(post, currentUserId);
        } else {
            unarchiveReply(post, currentUserId);
        }
        postRepository.save(post);
        List<Repost> reposts = repostRepository.findByPostId(post.getId())
                .stream()
                .peek(repost -> repost.setHidden(false)).toList();
        repostRepository.saveAll(reposts);
    }

    private void unarchiveOriginalPost(Post post, Long currentUserId) {
        accessGuard.checkOwnership(post.getAuthorId(), currentUserId);
        if (!post.getHidden()) {
            return;
        }
        post.setHidden(false);
        List<Post> replies = postRepository.findByOriginalPostId(post.getId());
        replies.forEach(reply -> reply.setHidden(false));
        postRepository.saveAll(replies);
        producer.sendPostUnarchiveEvent(post.getId());
    }

    private void unarchiveReply(Post reply, Long currentUserId) {
        Post originalPost = postRepository.findById(reply.getOriginalPostId())
                .orElseThrow(() -> new NotFoundException("Post " + reply.getOriginalPostId() + " not found!"));
        accessGuard.checkOwnership(originalPost.getAuthorId(), currentUserId);
        accessGuard.checkPostVisible(originalPost);
        if (!reply.getHidden()) {
            return;
        }
        reply.setHidden(false);
        originalPost.setReplyCount(originalPost.getReplyCount() + 1);
        PostStatusEvent postStatusEvent = new PostStatusEvent(originalPost.getId(), originalPost.getLikeCount(),
                originalPost.getRepostCount(), originalPost.getReplyCount(), originalPost.getSendCount());
        producer.sendPostStatusEvent(postStatusEvent);
    }

    @Override
    public void pinReply(Long replyId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post reply = findPostOrThrow(replyId, currentUserId);
        if (!reply.getIsReply()) {
            throw new IllegalArgumentException("Please provide a reply!");
        }
        Post originalPost = postRepository.findById(reply.getOriginalPostId())
                .orElseThrow(() -> new NotFoundException("Post " + reply.getOriginalPostId() + " not found!"));
        accessGuard.checkOwnership(originalPost.getAuthorId(), currentUserId);
        accessGuard.checkPostVisible(originalPost);
        if (reply.getId().equals(originalPost.getPinnedReplyId())) {
            return;
        }
        originalPost.setPinnedReplyId(replyId);
        postRepository.save(originalPost);
    }

    @Override
    public void unpinReply(Long replyId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post reply = findPostOrThrow(replyId, currentUserId);
        if (!reply.getIsReply()) {
            throw new IllegalArgumentException("Please provide a reply!");
        }
        Post originalPost = postRepository.findById(reply.getOriginalPostId())
                .orElseThrow(() -> new NotFoundException("Post " + reply.getOriginalPostId() + " not found!"));
        accessGuard.checkOwnership(originalPost.getAuthorId(), currentUserId);
        accessGuard.checkPostVisible(originalPost);
        if (!reply.getId().equals(originalPost.getPinnedReplyId())) {
            return;
        }
        originalPost.setPinnedReplyId(null);
        postRepository.save(originalPost);
    }

    @Override
    public void pinPost(Long postId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(postId, currentUserId);
        if (!post.getIsPost()) {
            throw new IllegalArgumentException("Please provide a post!");
        }
        accessGuard.checkOwnership(post.getAuthorId(), currentUserId);
        accessGuard.checkPostVisible(post);
        PinPostEvent pinPostEvent = new PinPostEvent(currentUserId, postId);
        producer.sendPinPostEvent(pinPostEvent);
    }

    @Override
    public void unpinPost(Long postId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(postId, currentUserId);
        if (!post.getIsPost()) {
            throw new IllegalArgumentException("Please provide a post!");
        }
        accessGuard.checkOwnership(post.getAuthorId(), currentUserId);
        PinPostEvent pinPostEvent = new PinPostEvent(currentUserId, null);
        producer.sendPinPostEvent(pinPostEvent);
    }

    protected Post findPostOrThrow(Long id, Long currentUserId) {
        return postRepository.findById(id)
                .filter(post -> !post.getHidden() || post.getAuthorId().equals(currentUserId))
                .orElseThrow(() -> new NotFoundException("Post " + id + " not found!"));
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }

    private List<PostMedia> saveMediaFiles(List<MultipartFile> media, Long postId) {
        List<PostMedia> mediaList = new ArrayList<>();
        for (MultipartFile file : media) {
            String fileName = postMediaService.saveFileLocally(file);
            MediaType type = postMediaService.detectMediaType(file);

            PostMedia postMedia = new PostMedia();
            postMedia.setPostId(postId);
            postMedia.setMediaUrl("/media/" + fileName);
            postMedia.setType(type);
            mediaList.add(postMedia);
        }
        return mediaList;
    }

    private CreatePostEvent createPostEvent(Post post, List<String> mediaUrls) {
        CreatePostEvent createPostEvent = new CreatePostEvent();
        createPostEvent.setPostId(post.getId());
        createPostEvent.setAuthorId(post.getAuthorId());
        createPostEvent.setContent(post.getContent());
        createPostEvent.setTopic(post.getTopic());
        createPostEvent.setCreatedAt(post.getCreatedAt());
        createPostEvent.setIsRepost(false);
        createPostEvent.setMediaUrls(mediaUrls);
        createPostEvent.setLikeCount(post.getLikeCount());
        createPostEvent.setRepostCount(post.getRepostCount());
        createPostEvent.setReplyCount(post.getReplyCount());
        createPostEvent.setSendCount(post.getSendCount());
        producer.sendCreatePostEvent(createPostEvent);
        return createPostEvent;
    }
}
