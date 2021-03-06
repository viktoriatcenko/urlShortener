package com.stackfortech.urlShorteningService.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Visit {
    @Id
    @GeneratedValue
    private Long id;
    private String url;

    public Visit(String url) {
        this.url = url;
    }

    public Visit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
