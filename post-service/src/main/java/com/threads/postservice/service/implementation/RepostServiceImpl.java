package com.threads.postservice.service.implementation;

import com.threads.events.CreatePostEvent;
import com.threads.events.PostStatusEvent;
import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.Repost;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.feign.SecurityFeignClient;
import com.threads.postservice.kafka.KafkaPostProducer;
import com.threads.postservice.mapper.PostMapper;
import com.threads.postservice.repository.PostRepository;
import com.threads.postservice.repository.RepostRepository;
import com.threads.postservice.response.RepostResponse;
import com.threads.postservice.service.RepostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepostServiceImpl implements RepostService {
    private final RepostRepository repostRepository;
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final SecurityFeignClient securityFeignClient;
    private final PostAccessGuard accessGuard;
    private final KafkaPostProducer producer;

    @Override
    public void repostPost(Long postId, String authorizationHeader) {
        Post originalPost = postRepository.findByIdAndHiddenFalse(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found!"));
        Long currentUser = getUserId(authorizationHeader);

        accessGuard.checkUserAccessToPost(currentUser, originalPost.getAuthorId());
        if (repostRepository.existsByUserIdAndPostId(currentUser, postId)) {
            return;
        }
        originalPost.setRepostCount(originalPost.getRepostCount() + 1);

        Repost repost = new Repost();
        repost.setUserId(currentUser);
        repost.setPostId(postId);
        postRepository.save(originalPost);
        repostRepository.save(repost);

        CreatePostEvent createPostEvent = new CreatePostEvent();
        createPostEvent.setPostId(repost.getId());
        createPostEvent.setAuthorId(originalPost.getAuthorId());
        createPostEvent.setContent(originalPost.getContent());
        createPostEvent.setTopic(originalPost.getTopic());
        createPostEvent.setCreatedAt(originalPost.getCreatedAt());
        createPostEvent.setIsRepost(true);
        createPostEvent.setOriginalPostId(originalPost.getId());
        createPostEvent.setRepostedByUserId(repost.getUserId());
        createPostEvent.setLikeCount(originalPost.getLikeCount());
        createPostEvent.setRepostCount(originalPost.getRepostCount());
        createPostEvent.setReplyCount(originalPost.getReplyCount());
        createPostEvent.setSendCount(originalPost.getSendCount());
        producer.sendCreatePostEvent(createPostEvent);
        PostStatusEvent postStatusEvent = new PostStatusEvent(originalPost.getId(), originalPost.getLikeCount(),
                originalPost.getRepostCount(), originalPost.getReplyCount(), originalPost.getSendCount());
        producer.sendPostStatusEvent(postStatusEvent);
    }

    @Override
    @Transactional
    public void removeRepost(Long repostId, String authorizationHeader) {
        Repost repost = repostRepository.findByIdAndHiddenFalse(repostId)
                .orElseThrow(() -> new NotFoundException("Repost " + repostId + " not found!"));
        Post originalPost = postRepository.findById(repost.getPostId())
                .orElseThrow(() -> new NotFoundException("Post " + repost.getPostId() + " not found!"));
        Long currentUser = getUserId(authorizationHeader);
        accessGuard.checkOwnership(repost.getUserId(), currentUser);
        originalPost.setRepostCount(originalPost.getRepostCount() - 1);
        postRepository.save(originalPost);
        repostRepository.delete(repost);
        PostStatusEvent postStatusEvent = new PostStatusEvent(originalPost.getId(), originalPost.getLikeCount(),
                originalPost.getRepostCount(), originalPost.getReplyCount(), originalPost.getSendCount());
        producer.sendPostStatusEvent(postStatusEvent);
        producer.sendPostDeleteEvent(repostId);
    }

    @Override
    public List<RepostResponse> getUserReposts(Long authorId, String authorizationHeader) {
        Long currentUser = getUserId(authorizationHeader);
        accessGuard.checkUserAccessToPost(currentUser, authorId);
        List<Repost> reposts = repostRepository.findByUserIdAndHiddenFalse(authorId);
        List<RepostResponse> responseList = new ArrayList<>();
        for (Repost repost : reposts) {
            postRepository.findByIdAndHiddenFalse(repost.getPostId())
                    .ifPresent(post -> {
                        RepostResponse response = new RepostResponse();
                        response.setUserId(authorId);
                        response.setPost(mapper.toResponse(post));
                        responseList.add(response);
                    });
        }
        return responseList;
    }

    @Override
    public List<RepostResponse> getMyReposts(String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        return getRepostResponse(currentUserId, authorizationHeader);
    }

    private List<RepostResponse> getRepostResponse(Long userId, String authorizationHeader) {
        return getUserReposts(userId, authorizationHeader);
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}

