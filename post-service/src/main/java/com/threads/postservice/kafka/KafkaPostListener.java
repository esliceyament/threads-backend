package com.threads.postservice.kafka;

import com.threads.events.CreatePostEvent;
import com.threads.events.NewFollowEvent;
import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.PostMedia;
import com.threads.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaPostListener {
    private final PostRepository postRepository;
    private final KafkaPostProducer producer;

    @KafkaListener(topics = "new-follow-events", groupId = "post-service")
    public void handleNewFollowEvent(NewFollowEvent newFollowEvent) {
        List<Post> posts = postRepository.findRecent3Posts(newFollowEvent.getFollowingId());
        List<CreatePostEvent> events = new ArrayList<>();
        posts.forEach(post -> {
            CreatePostEvent createPostEvent = new CreatePostEvent();
            createPostEvent.setUserId(newFollowEvent.getFollowerId());
            createPostEvent.setPostId(post.getId());
            createPostEvent.setAuthorId(newFollowEvent.getFollowingId());
            createPostEvent.setAuthorUsername(newFollowEvent.getFollowingUsername());
            createPostEvent.setContent(post.getContent());
            createPostEvent.setTopic(post.getTopic());
            createPostEvent.setCreatedAt(post.getCreatedAt());
            createPostEvent.setIsRepost(false);
            createPostEvent.setLikeCount(post.getLikeCount());
            createPostEvent.setRepostCount(post.getRepostCount());
            createPostEvent.setReplyCount(post.getReplyCount());
            createPostEvent.setSendCount(post.getSendCount());
            if (post.getMedia() != null) {
                List<String> mediaUrls = post.getMedia().stream()
                        .map(PostMedia::getMediaUrl).toList();
                createPostEvent.setMediaUrls(mediaUrls);
            }
            events.add(createPostEvent);
        });
        producer.sendNewFollowPostEvent(events);
    }
}
