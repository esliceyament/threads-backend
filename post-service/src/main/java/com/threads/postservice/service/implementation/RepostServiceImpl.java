package com.threads.postservice.service.implementation;

import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.Repost;
import com.threads.postservice.exception.AlreadyRepostedException;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.exception.OwnershipException;
import com.threads.postservice.feign.SecurityFeignClient;
import com.threads.postservice.mapper.PostMapper;
import com.threads.postservice.repository.PostRepository;
import com.threads.postservice.repository.RepostRepository;
import com.threads.postservice.response.RepostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepostServiceImpl {
    private final RepostRepository repostRepository;
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final SecurityFeignClient securityFeignClient;
    private final PostAccessGuard accessGuard;

    public void repostPost(Long postId, String authorizationHeader) {
        Post originalPost = postRepository.findByIdAndHiddenFalse(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found!"));
        Long currentUser = getUserId(authorizationHeader);

        accessGuard.checkUserAccessToPost(currentUser, originalPost.getAuthorId());
        if (repostRepository.existsByUserIdAndPostId(currentUser, postId)) {
            throw new AlreadyRepostedException("This post is already reposted!");
        }
        originalPost.setRepostCount(originalPost.getRepostCount() + 1);

        Repost repost = new Repost();
        repost.setUserId(currentUser);
        repost.setPostId(postId);
        postRepository.save(originalPost);
        repostRepository.save(repost);
    }

    public void removeRepost(Long repostId, String authorizationHeader) {
        Repost repost = repostRepository.findByIdAndHiddenFalse(repostId)
                .orElseThrow(() -> new NotFoundException("Repost " + repostId + " not found!"));
        Post originalPost = postRepository.findById(repost.getPostId())
                .orElseThrow(() -> new NotFoundException("Post " + repost.getPostId() + " not found!"));
        if (!repost.getUserId().equals(getUserId(authorizationHeader))) {
            throw new OwnershipException("You are not allowed to remove this repost!");
        }
        originalPost.setRepostCount(originalPost.getRepostCount() - 1);
        postRepository.save(originalPost);
        repostRepository.delete(repost);
    }

    public List<RepostResponse> getUserReposts(Long authorId, String authorizationHeader) {
        Long currentUser = getUserId(authorizationHeader);
        accessGuard.checkUserAccessToPost(currentUser, authorId);
        List<Repost> reposts = repostRepository.findByUserIdAndHiddenFalse(authorId);
        return reposts.stream().map(repost -> {
            Post post = postRepository.findById(repost.getPostId()).get();
            RepostResponse response = new RepostResponse();
            response.setUserId(authorId);
            response.setPost(mapper.toResponse(post));
            return response;
        }).toList();
    }

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

