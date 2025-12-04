package com.edufelip.meer.dto;

import java.util.List;

public class PhotoUploadResponse {
    private List<PhotoUploadSlot> uploads;

    public PhotoUploadResponse(List<PhotoUploadSlot> uploads) {
        this.uploads = uploads;
    }

    public List<PhotoUploadSlot> getUploads() {
        return uploads;
    }
}
