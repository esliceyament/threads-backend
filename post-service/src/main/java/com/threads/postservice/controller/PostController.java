package com.threads.postservice.controller;

import com.threads.postservice.dto.PostDto;
import com.threads.postservice.dto.PostUpdateDto;
import com.threads.postservice.dto.ReplyUpdateDto;
import com.threads.postservice.payload.PostRequest;
import com.threads.postservice.payload.QuotePostRequest;
import com.threads.postservice.payload.ReplyRequest;
import com.threads.postservice.response.PostResponse;
import com.threads.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/create-post")
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequest request,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.createPost(request, authorizationHeader));
    }

    @PostMapping("/reply/{postId}")
    public ResponseEntity<PostDto> replyToPost(@RequestBody ReplyRequest request, @PathVariable Long postId,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.replyToPost(request, postId, authorizationHeader));
    }

    @PutMapping("/update-post/{id}")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostUpdateDto postDto, @PathVariable Long id,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.updatePost(postDto, id, authorizationHeader));
    }

    @PutMapping("/update-reply/{id}")
    public ResponseEntity<PostDto> updateReply(@RequestBody ReplyUpdateDto dto, @PathVariable Long id,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.updateReply(dto, id, authorizationHeader));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.getPost(id, authorizationHeader));
    }

    @GetMapping("/{postId}/get-replies")
    public ResponseEntity<List<PostResponse>> getRepliesOfPost(@PathVariable Long postId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.getRepliesOfPost(postId, authorizationHeader));
    }

    @PostMapping("/quote/{id}")
    public ResponseEntity<PostResponse> quotePost(@PathVariable Long id,
                                                  @RequestBody QuotePostRequest request,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.quotePost(id, request, authorizationHeader));
    }

    @GetMapping("/get-posts/{authorId}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long authorId,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.getUserPosts(authorId, authorizationHeader));
    }

    @GetMapping("/get-replies/{authorId}")
    public ResponseEntity<List<PostResponse>> getUserReplies(@PathVariable Long authorId,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.getUserReplies(authorId, authorizationHeader));
    }

    @GetMapping("/get-posts")
    public ResponseEntity<List<PostResponse>> getMyPosts(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.getMyPosts(authorizationHeader));
    }

    @GetMapping("/get-replies")
    public ResponseEntity<List<PostResponse>> getMyReplies(String authorizationHeader) {
        return ResponseEntity.ok(postService.getMyReplies(authorizationHeader));
    }

    @DeleteMapping("/delete-post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postService.deletePost(id, authorizationHeader);
        return ResponseEntity.ok("Post " + id + " was successfully deleted!");
    }

    @PutMapping("/archive/{id}")
    public ResponseEntity<?> archivePost(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postService.archivePost(id, authorizationHeader);
        return ResponseEntity.ok("Post " + id + " was successfully archived!");
    }

    @GetMapping("/get-archived-posts")
    public ResponseEntity<List<PostResponse>> getArchivedPosts(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postService.getArchivedPosts(authorizationHeader));
    }

    @PutMapping("/unarchive/{id}")
    public ResponseEntity<?> unarchivePost(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postService.unarchivePost(id, authorizationHeader);
        return ResponseEntity.ok("Post " + id + " was successfully unarchived!");
    }

    @PutMapping("/pin-reply/{replyId}")
    public ResponseEntity<?> pinReply(@PathVariable Long replyId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postService.pinReply(replyId, authorizationHeader);
        return ResponseEntity.ok("Reply " + replyId + " is pinned!");
    }

    @PutMapping("/unpin-reply/{replyId}")
    public ResponseEntity<?> unpinReply(@PathVariable Long replyId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postService.unpinReply(replyId, authorizationHeader);
        return ResponseEntity.ok("Reply " + replyId + " is unpinned!");
    }

    @PutMapping("/pin-post/{postId}")
    public ResponseEntity<?> pinPost(@PathVariable Long postId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postService.pinPost(postId, authorizationHeader);
        return ResponseEntity.ok("Post " + postId + " is pinned!");
    }

    @PutMapping("/unpin-post/{postId}")
    public ResponseEntity<?> unpinPost(@PathVariable Long postId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postService.unpinPost(postId, authorizationHeader);
        return ResponseEntity.ok("Post " + postId + " is unpinned!");
    }
}




















//PostService.sendPostToUser()
//
//PostService.getSentPosts()
//
//PostService.getReceivedPosts()
