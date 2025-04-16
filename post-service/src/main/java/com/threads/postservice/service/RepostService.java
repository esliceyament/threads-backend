package com.threads.postservice.service;

import com.threads.postservice.response.RepostResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RepostService {

    void repostPost(Long postId, String authorizationHeader);

    void removeRepost(Long repostId, String authorizationHeader);
    List<RepostResponse> getUserReposts(Long authorId, String authorizationHeader);

    List<RepostResponse> getMyReposts(String authorizationHeader);

}
