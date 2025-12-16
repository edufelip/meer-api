package com.edufelip.meer.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    private static final int MAX_PART_LENGTH = 2048;

    @PostMapping("/communities")
    public ResponseEntity<String> uploadCommunity(@RequestPart("community") String communityJson,
                                                  @RequestPart("community") MultipartFile communityFile) {
        sanitizeInPlace(communityJson);
        return ResponseEntity.ok("queued community upload");
    }

    @PostMapping("/posts")
    public ResponseEntity<String> uploadPost(@RequestPart("post") String postJson,
                                             @RequestPart(value = "post", required = false) MultipartFile postFile) {
        sanitizeInPlace(postJson);
        return ResponseEntity.ok("queued post upload");
    }

    private String sanitizeInPlace(String raw) {
        if (raw == null) return null;
        String cleaned = Jsoup.clean(raw, Safelist.none()).trim();
        if (cleaned.length() > MAX_PART_LENGTH) {
            cleaned = cleaned.substring(0, MAX_PART_LENGTH);
        }
        return cleaned;
    }
}
