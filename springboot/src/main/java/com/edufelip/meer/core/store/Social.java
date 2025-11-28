package com.edufelip.meer.core.store;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Social {
    @Column(length = 2048)
    private String facebook;
    @Column(length = 2048)
    private String instagram;
    @Column(length = 2048)
    private String website;

    public Social() {}

    public Social(String facebook, String instagram, String website) {
        this.facebook = facebook;
        this.instagram = instagram;
        this.website = website;
    }

    public String getFacebook() { return facebook; }
    public String getInstagram() { return instagram; }
    public String getWebsite() { return website; }

    public void setFacebook(String facebook) { this.facebook = facebook; }
    public void setInstagram(String instagram) { this.instagram = instagram; }
    public void setWebsite(String website) { this.website = website; }
}
