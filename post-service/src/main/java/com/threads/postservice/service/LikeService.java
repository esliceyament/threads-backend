package com.threads.postservice.service;

import com.threads.postservice.response.LikeResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LikeService {

    void likePost(Long postId, String authorizationHeader);

    void unlikePost(Long postId, String authorizationHeader);

    List<LikeResponse> getLikesOfPost(Long postId, String authorizationHeader);
}
