package com.threads.postservice.service;

import com.threads.postservice.dto.PageDto;
import com.threads.postservice.dto.PostDto;
import com.threads.postservice.dto.PostUpdateDto;
import com.threads.postservice.dto.ReplyUpdateDto;
import com.threads.postservice.payload.PostRequest;
import com.threads.postservice.payload.QuotePostRequest;
import com.threads.postservice.payload.ReplyRequest;
import com.threads.postservice.response.PostResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface PostService {

    PostDto createPost(PostRequest request, List<MultipartFile> media, String authorizationHeader);
    PostDto replyToPost(ReplyRequest request, Long postId, List<MultipartFile> media, String authorizationHeader);
    PostDto updatePost(PostUpdateDto postDto, Long id, String authorizationHeader);
    PostDto updateReply(ReplyUpdateDto dto, Long id, String authorizationHeader);
    PostResponse getPost(Long id, String authorizationHeader);
    List<PostResponse> getRepliesOfPost(Long postId, String authorizationHeader);
    PostResponse quotePost(Long id, QuotePostRequest request, String authorizationHeader);
    List<PostResponse> getUserPosts(Long authorId, String authorizationHeader);
    List<PostResponse> getUserReplies(Long authorId, String authorizationHeader);
    List<PostResponse> getMyPosts(String authorizationHeader);
    List<PostResponse> getMyReplies(String authorizationHeader);
    PageDto<PostResponse> getPostByTopic(String topic, int page);
    void deletePost(Long id, String authorizationHeader);
    void archivePost(Long id, String authorizationHeader);
    List<PostResponse> getArchivedPosts(String authorizationHeader);
    void unarchivePost(Long id, String authorizationHeader);
    void pinReply(Long replyId, String authorizationHeader);
    void unpinReply(Long replyId, String authorizationHeader);
    void pinPost(Long postId, String authorizationHeader);
    void unpinPost(Long postId, String authorizationHeader);
}
