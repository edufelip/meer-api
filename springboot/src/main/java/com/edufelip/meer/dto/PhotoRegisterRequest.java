package com.edufelip.meer.dto;

import java.util.List;

public class PhotoRegisterRequest {
    private List<Item> photos;
    private List<Integer> deletePhotoIds;

    public List<Item> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Item> photos) {
        this.photos = photos;
    }

    public List<Integer> getDeletePhotoIds() {
        return deletePhotoIds;
    }

    public void setDeletePhotoIds(List<Integer> deletePhotoIds) {
        this.deletePhotoIds = deletePhotoIds;
    }

    public static class Item {
        private Integer photoId;
        private String fileKey;
        private Integer position;

        public Integer getPhotoId() {
            return photoId;
        }

        public void setPhotoId(Integer photoId) {
            this.photoId = photoId;
        }

        public String getFileKey() {
            return fileKey;
        }

        public void setFileKey(String fileKey) {
            this.fileKey = fileKey;
        }

        public Integer getPosition() {
            return position;
        }

        public void setPosition(Integer position) {
            this.position = position;
        }
    }
}
