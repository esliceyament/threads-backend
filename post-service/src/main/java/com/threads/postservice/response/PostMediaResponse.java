package com.threads.postservice.response;

import com.threads.postservice.enums.MediaType;
import lombok.Data;

@Data
public class PostMediaResponse {
    private String mediaUrl;
    private MediaType type;
}
