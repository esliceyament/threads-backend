package com.threads.postservice.service.implementation;

import com.threads.PinPostEvent;
import com.threads.postservice.dto.PostDto;
import com.threads.postservice.dto.PostUpdateDto;
import com.threads.postservice.dto.ReplyUpdateDto;
import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.Repost;
import com.threads.postservice.enums.ReplyPermission;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.exception.NotPinnedReplyException;
import com.threads.postservice.exception.OwnershipException;
import com.threads.postservice.feign.SecurityFeignClient;
import com.threads.postservice.kafka.PinnedPostProducer;
import com.threads.postservice.mapper.PostMapper;
import com.threads.postservice.payload.PostRequest;
import com.threads.postservice.payload.QuotePostRequest;
import com.threads.postservice.payload.ReplyRequest;
import com.threads.postservice.repository.PostRepository;
import com.threads.postservice.repository.RepostRepository;
import com.threads.postservice.response.PostResponse;
import com.threads.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final SecurityFeignClient securityFeignClient;
    private final RepostRepository repostRepository;
    private final PostAccessGuard accessGuard;
    private final PinnedPostProducer producer;
///////////////////////////////media

    public PostDto createPost(PostRequest request, String authorizationHeader) {
        Post post = mapper.toEntity(request);
        post.setAuthorId(getUserId(authorizationHeader));
        post.setIsPost(true);
        post.setCreatedAt(LocalDateTime.now());
        post.setIsReply(false);
        post.setHidden(false);
        postRepository.save(post);
        return mapper.toDto(post);
    }

    public PostDto replyToPost(ReplyRequest request, Long postId, String authorizationHeader) {
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
        if (post.getReplyToPostId() != null) {
            reply.setOriginalPostId(post.getOriginalPostId());
            reply.setReplyToPostId(post.getId());
        }
        reply.setCreatedAt(LocalDateTime.now());
        postRepository.save(reply);
        postRepository.save(post);
        return mapper.toDtoReply(reply);
    }

    public PostDto updatePost(PostUpdateDto postDto, Long id, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(id, currentUserId);
        accessGuard.checkOwnership(post, currentUserId);
        if (!post.getIsPost()) {
            throw new IllegalArgumentException("Please provide a post!");
        }
        mapper.updatePost(postDto, post);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        return mapper.toDto(post);
    }

    public PostDto updateReply(ReplyUpdateDto dto, Long id, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post reply = findPostOrThrow(id, currentUserId);
        accessGuard.checkOwnership(reply, currentUserId);
        accessGuard.checkPostVisible(reply);
        if (!reply.getIsReply()) {
            throw new IllegalArgumentException("Please provide a reply!");
        }
        mapper.updateReply(dto, reply);
        reply.setUpdatedAt(LocalDateTime.now());
        postRepository.save(reply);
        return mapper.toDto(reply);
    }

    public PostResponse getPost(Long id, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(id, currentUserId);
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        return mapper.toResponse(post);
    }

    public List<PostResponse> getRepliesOfPost(Long postId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(postId, currentUserId);
        if (post.getAuthorId().equals(currentUserId)) {
            return postRepository.findByOriginalPostId(postId)
                    .stream().
                    map(mapper::toResponse).toList();
        }
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        return postRepository.findByOriginalPostIdAndHiddenFalse(postId)
                .stream()
                .map(mapper::toResponse).toList();
    }

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
        return mapper.toResponse(quote);
    }

    public List<PostResponse> getUserPosts(Long authorId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkUserAccessToPost(currentUserId, authorId);
        return postRepository.findByAuthorIdAndIsPostTrueAndHiddenFalse(authorId)
                .stream()
                .map(mapper::toResponse).toList();
    }

    public List<PostResponse> getUserReplies(Long authorId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkUserAccessToPost(currentUserId, authorId);
        return postRepository.findByAuthorIdAndIsReplyTrueAndHiddenFalse(authorId)
                .stream()
                .map(mapper::toResponse).toList();
    }

    public List<PostResponse> getMyPosts(String authorizationHeader) {
        return postRepository.findByAuthorIdAndIsPostTrueAndHiddenFalse(getUserId(authorizationHeader))
                .stream()
                .map(mapper::toResponse).toList();
    }
    public List<PostResponse> getMyReplies(String authorizationHeader) {
        return postRepository.findByAuthorIdAndIsReplyTrueAndHiddenFalse(getUserId(authorizationHeader))
                .stream()
                .map(mapper::toResponse).toList();
    }

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
        repostRepository.deleteByPostId(post.getId());

        postRepository.delete(post);
    }

    private void deleteOriginalPost(Post post, Long currentUserId) {
        accessGuard.checkOwnership(post, currentUserId);
        List<Post> replies = postRepository.findByOriginalPostId(post.getId());
        postRepository.deleteAll(replies);
    }

    private void deleteReply(Post reply, Long currentUserId) {
        Post originalPost = postRepository.findById(reply.getOriginalPostId())
                .orElseThrow(() -> new NotFoundException("Post " + reply.getOriginalPostId() + " not found!"));

        if (!accessGuard.checkOwnershipToDelete(originalPost, currentUserId) && !accessGuard.checkOwnershipToDelete(reply, currentUserId)) {
            throw new OwnershipException("You don't have access!");
        }
        if (!reply.getHidden()) {
            originalPost.setReplyCount(originalPost.getReplyCount() - 1);
        }
        if (reply.getId().equals(originalPost.getPinnedReplyId())) {
            originalPost.setPinnedReplyId(null);
        }
        postRepository.save(originalPost);
    }

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
        accessGuard.checkOwnership(post, currentUserId);
        List<Post> replies = postRepository.findByOriginalPostId(post.getId());
        replies.forEach(reply -> reply.setHidden(true));
        postRepository.saveAll(replies);
        post.setHidden(true);
        postRepository.save(post);
    }

    private void archiveReply(Post reply, Long currentUserId) {
        Post originalPost = findPostOrThrow(reply.getOriginalPostId(), currentUserId);
        accessGuard.checkOwnership(originalPost, currentUserId);
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
    }

    public List<PostResponse> getArchivedPosts(String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        return postRepository.findByAuthorIdAndIsPostTrueAndHiddenTrue(currentUserId)
                .stream()
                .map(mapper::toResponse).toList();
    }

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
    }

    private void unarchiveOriginalPost(Post post, Long currentUserId) {
        accessGuard.checkOwnership(post, currentUserId);
        if (!post.getHidden()) {
            throw new IllegalStateException("Post " + post.getId() +" is not archived!");
        }
        post.setHidden(false);
        List<Post> replies = postRepository.findByOriginalPostId(post.getId());
        replies.forEach(reply -> reply.setHidden(false));
        postRepository.saveAll(replies);
    }

    private void unarchiveReply(Post reply, Long currentUserId) {
        Post originalPost = postRepository.findById(reply.getOriginalPostId()).get();
        accessGuard.checkOwnership(originalPost, currentUserId);
        accessGuard.checkPostVisible(originalPost);
        if (!reply.getHidden()) {
            throw new IllegalStateException("Reply " + reply.getId() +" is not archived!");
        }
        reply.setHidden(false);
    }

    public void pinReply(Long replyId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post reply = findPostOrThrow(replyId, currentUserId);
        if (!reply.getIsReply()) {
            throw new IllegalArgumentException("Please provide a reply!");
        }
        Post originalPost = postRepository.findById(reply.getOriginalPostId()).get();
        accessGuard.checkOwnership(originalPost, currentUserId);
        accessGuard.checkPostVisible(originalPost);
        if (reply.getId().equals(originalPost.getPinnedReplyId())) {
            return;
        }
        originalPost.setPinnedReplyId(replyId);
        postRepository.save(originalPost);
    }

    public void unpinReply(Long replyId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post reply = findPostOrThrow(replyId, currentUserId);
        if (!reply.getIsReply()) {
            throw new IllegalArgumentException("Please provide a reply!");
        }
        Post originalPost = postRepository.findById(reply.getOriginalPostId())
                .orElseThrow(() -> new NotFoundException("Post " + reply.getOriginalPostId() + " not found!"));
        accessGuard.checkOwnership(originalPost, currentUserId);
        accessGuard.checkPostVisible(originalPost);
        if (!reply.getId().equals(originalPost.getPinnedReplyId())) {
            throw new NotPinnedReplyException("This reply cannot be unpinned!");
        }
        originalPost.setPinnedReplyId(null);
        postRepository.save(originalPost);
    }

    public void pinPost(Long postId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(postId, currentUserId);
        if (!post.getIsPost()) {
            throw new IllegalArgumentException("Please provide a post!");
        }
        accessGuard.checkOwnership(post, currentUserId);
        accessGuard.checkPostVisible(post);
        PinPostEvent pinPostEvent = new PinPostEvent(currentUserId, postId);
        producer.sendPinPostEvent(pinPostEvent);
    }

    public void unpinPost(Long postId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        Post post = findPostOrThrow(postId, currentUserId);
        if (!post.getIsPost()) {
            throw new IllegalArgumentException("Please provide a post!");
        }
        accessGuard.checkOwnership(post, currentUserId);
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
}
