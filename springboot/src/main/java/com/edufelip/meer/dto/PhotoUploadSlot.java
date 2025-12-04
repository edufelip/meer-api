package com.edufelip.meer.dto;

public class PhotoUploadSlot {
    private String uploadUrl;
    private String fileKey;
    private String contentType;

    public PhotoUploadSlot(String uploadUrl, String fileKey, String contentType) {
        this.uploadUrl = uploadUrl;
        this.fileKey = fileKey;
        this.contentType = contentType;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getFileKey() {
        return fileKey;
    }

    public String getContentType() {
        return contentType;
    }
}
