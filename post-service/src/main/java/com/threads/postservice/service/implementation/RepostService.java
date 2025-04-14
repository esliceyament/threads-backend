package com.threads.postservice.service.implementation;

import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.Repost;
import com.threads.postservice.exception.NotFoundException;
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
public class RepostService {
    private final RepostRepository repostRepository;
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final SecurityFeignClient securityFeignClient;

    public void repostPost(Long postId, String authorizationHeader) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("Post " + postId + " not found!");
        }
        Long currentUser = getUserId(authorizationHeader);

        if (repostRepository.existsByUserIdAndPostId(currentUser, postId)) {
            throw new RuntimeException("Already reposted");
        }

        Repost repost = new Repost();
        repost.setUserId(currentUser);
        repost.setPostId(postId);
        repostRepository.save(repost);
    }

    public void removeRepost(Long repostId, String authorizationHeader) {
        Repost repost = repostRepository.findById(repostId)
                .orElseThrow(() -> new NotFoundException(""));
        if (!repost.getUserId().equals(getUserId(authorizationHeader))) {
            throw new RuntimeException("");
        }
        repostRepository.delete(repost);
    }

    public List<RepostResponse> getUserReposts(Long authorId) {
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
        return getRepostResponse(currentUserId);
    }

    private List<RepostResponse> getRepostResponse(Long userId) {
        return getUserReposts(userId);
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}

