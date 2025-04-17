package com.threads.postservice.service.implementation;

import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.PostShare;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.feign.SecurityFeignClient;
import com.threads.postservice.mapper.PostMapper;
import com.threads.postservice.payload.ShareRequest;
import com.threads.postservice.repository.PostRepository;
import com.threads.postservice.repository.PostShareRepository;
import com.threads.postservice.response.PostResponse;
import com.threads.postservice.service.PostShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PostShareServiceImpl implements PostShareService {
    private final PostRepository postRepository;
    private final PostShareRepository repository;
    private final SecurityFeignClient securityFeignClient;
    private final PostAccessGuard accessGuard;
    private final PostMapper mapper;

    @Override
    public void sendPost(ShareRequest request, Long receiverId, String authorizationHeader) {
        accessGuard.checkUserExists(receiverId);
        Post post = postRepository.findByIdAndHiddenFalse(request.getPostId())
                .orElseThrow(() -> new NotFoundException("Post " + request.getPostId() + " not found!"));
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkCanSendPost(currentUserId, receiverId);
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        post.setSendCount(post.getSendCount() + 1);
        PostShare share = new PostShare();
        share.setPostId(request.getPostId());
        share.setSenderId(currentUserId);
        share.setReceiverId(receiverId);
        share.setSentAt(LocalDateTime.now());
        repository.save(share);
        postRepository.save(post);
    }

    @Override
    public List<PostResponse> getPostsSharedByUser(Long userId, String authorizationHeader) {
        accessGuard.checkUserExists(userId);
        Long currentUserId = getUserId(authorizationHeader);
        List<PostResponse> responseList = new ArrayList<>();
        List<PostShare> receivedPosts = repository.findBySenderIdAndReceiverId(userId, currentUserId);
        for (PostShare share : receivedPosts) {
            postRepository.findByIdAndHiddenFalse(share.getPostId())
                            .ifPresent(post -> responseList.add(mapper.toResponse(post)));
        }
        return responseList;
    }

    @Override
    public List<PostResponse> getPostsSentToUser(Long userId, String authorizationHeader) {
        accessGuard.checkUserExists(userId);
        Long currentUserId = getUserId(authorizationHeader);
        List<PostResponse> responseList = new ArrayList<>();
        List<PostShare> sentPosts = repository.findBySenderIdAndReceiverId(currentUserId, userId);
        for (PostShare share : sentPosts) {
            postRepository.findByIdAndHiddenFalse(share.getPostId())
                            .ifPresent(post -> responseList.add(mapper.toResponse(post)));
        }
        return responseList;
    }

    @Override
    public void deleteShare(Long shareId, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        PostShare share = repository.findById(shareId)
                        .orElseThrow(() -> new NotFoundException("Share " + shareId + " not found!"));
        accessGuard.checkOwnership(share.getSenderId(), currentUserId);
        repository.delete(share);
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
