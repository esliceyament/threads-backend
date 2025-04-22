package com.threads.postservice.service.implementation;

import com.threads.events.PostStatusEvent;
import com.threads.postservice.entity.Like;
import com.threads.postservice.entity.Post;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.feign.SecurityFeignClient;
import com.threads.postservice.kafka.KafkaPostProducer;
import com.threads.postservice.repository.LikeRepository;
import com.threads.postservice.repository.PostRepository;
import com.threads.postservice.response.LikeResponse;
import com.threads.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PostAccessGuard accessGuard;
    private final SecurityFeignClient securityFeignClient;
    private final KafkaPostProducer producer;

    @Override
    public void likePost(Long postId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        if (likeRepository.existsByUserIdAndPostId(currentUserId, postId)) {
            return;
        }
        Post post = postRepository.findByIdAndHiddenFalse(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found!"));
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        post.setLikeCount(post.getLikeCount() + 1);
        likeRepository.save(new Like(currentUserId, postId, LocalDateTime.now()));
        postRepository.save(post);
        PostStatusEvent postStatusEvent = new PostStatusEvent(post.getId(), post.getLikeCount(),
                post.getRepostCount(), post.getReplyCount(), post.getSendCount());
        producer.sendPostStatusEvent(postStatusEvent);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        if (likeRepository.existsByUserIdAndPostId(currentUserId, postId)) {
            Post post = postRepository.findByIdAndHiddenFalse(postId)
                    .orElseThrow(() -> new NotFoundException("Post " + postId + " not found!"));
            accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
            post.setLikeCount(post.getLikeCount() - 1);
            likeRepository.deleteByUserIdAndPostId(currentUserId, postId);
            postRepository.save(post);
            PostStatusEvent postStatusEvent = new PostStatusEvent(post.getId(), post.getLikeCount(),
                    post.getRepostCount(), post.getReplyCount(), post.getSendCount());
            producer.sendPostStatusEvent(postStatusEvent);
        }
    }

    @Override
    public List<LikeResponse> getLikesOfPost(Long postId, String authorizationHeader) {
        Post post = postRepository.findByIdAndHiddenFalse(postId).
            orElseThrow(() -> new NotFoundException("Post " + postId + " not found!"));
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        return likeRepository.findByPostId(postId)
                .stream()
                .map(like -> {
                    LikeResponse likeResponse = new LikeResponse();
                    likeResponse.setUserId(like.getUserId());
                    likeResponse.setLikedAt(like.getLikedAt());
                    return likeResponse;
                }).toList();
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
