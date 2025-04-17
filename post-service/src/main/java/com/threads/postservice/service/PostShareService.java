package com.threads.postservice.service;

import com.threads.postservice.payload.ShareRequest;
import com.threads.postservice.response.PostResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostShareService {
    void sendPost(ShareRequest request, Long receiverId, String authorizationHeader);
    List<PostResponse> getPostsSharedByUser(Long userId, String authorizationHeader);
    List<PostResponse> getPostsSentToUser(Long userId, String authorizationHeader);
    void deleteShare(Long shareId, String authorizationHeader);
}