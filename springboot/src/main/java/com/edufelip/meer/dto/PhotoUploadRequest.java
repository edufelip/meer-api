package com.edufelip.meer.dto;

import java.util.List;

public class PhotoUploadRequest {
    private Integer count;
    private List<String> contentTypes;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }
}
