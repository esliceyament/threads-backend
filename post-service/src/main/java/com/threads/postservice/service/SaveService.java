package com.threads.postservice.service;

import com.threads.postservice.response.PostResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SaveService {

    void savePost(Long postId, String authorizationHeader);

    void unsavePost(Long postId, String authorizationHeader);

    List<PostResponse> getMySaves(String authorizationHeader);
}
