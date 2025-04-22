package com.threads.events;

public class UpdatePostEvent {
    private Long postId;
    private String content;
    private String topic;

    public UpdatePostEvent() {}
    public UpdatePostEvent(Long postId, String content, String topic) {
        this.postId = postId;
        this.content = content;
        this.topic = topic;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}
