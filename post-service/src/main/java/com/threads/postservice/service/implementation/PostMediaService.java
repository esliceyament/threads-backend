package com.threads.postservice.service.implementation;

import com.threads.postservice.enums.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class PostMediaService {

    protected String saveFileLocally(MultipartFile file) {
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
        File dir = new File(uploadDir);

        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create upload directory");
        }

        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

        File destination = new File(uploadDir + uniqueFileName);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save media file", e);
        }

        return uniqueFileName;
    }



    protected MediaType detectMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("File content type is missing");
        }


        if (contentType.startsWith("image")) {
            return MediaType.IMAGE;
        } else if (contentType.startsWith("video")) {
            return MediaType.VIDEO;
        } else {
            throw new IllegalArgumentException("Unsupported media type: " + contentType);
        }
    }
}
