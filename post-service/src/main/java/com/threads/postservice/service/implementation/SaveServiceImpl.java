package com.threads.postservice.service.implementation;

import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.SavedPost;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.feign.SecurityFeignClient;
import com.threads.postservice.mapper.PostMapper;
import com.threads.postservice.repository.PostRepository;
import com.threads.postservice.repository.SaveRepository;
import com.threads.postservice.response.PostResponse;
import com.threads.postservice.service.SaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveServiceImpl implements SaveService {
    private final PostRepository postRepository;
    private final SaveRepository saveRepository;
    private final SecurityFeignClient securityFeignClient;
    private final PostAccessGuard accessGuard;
    private final PostMapper mapper;

    @Override
    public void savePost(Long postId, String authorizationHeader) {
        Post post = postRepository.findByIdAndHiddenFalse(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found!"));
        Long currentUserId = getUserId(authorizationHeader);
        if (saveRepository.existsByUserIdAndPostId(currentUserId, postId)) {
            return;
        }
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        saveRepository.save(new SavedPost(currentUserId, postId));
    }

    @Override
    public void unsavePost(Long postId, String authorizationHeader) {
        Post post = postRepository.findByIdAndHiddenFalse(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found!"));
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        saveRepository.deleteByUserIdAndPostId(currentUserId, postId);
    }

    @Override
    public List<PostResponse> getMySaves(String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        List<SavedPost> savedPosts = saveRepository.findByUserId(currentUserId);
        List<PostResponse> responseList = new ArrayList<>();
        for (SavedPost save : savedPosts) {
            responseList.add(mapper.toResponse(postRepository.findByIdAndHiddenFalse(save.getPostId()).get()));
        }
        return responseList;
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
