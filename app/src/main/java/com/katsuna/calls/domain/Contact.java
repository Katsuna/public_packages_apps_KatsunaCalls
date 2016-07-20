package com.katsuna.calls.domain;

public class Contact {

    private long id;
    private String name;
    private String photoUri;

    public Contact() {
    }

    public Contact(Contact contact) {
        id = contact.getId();
        name = contact.getName();
        photoUri = contact.getPhotoUri();
    }

    private long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
