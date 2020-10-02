package com.pymespace.guiazlzc.models;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {
    public String uid;
    public String author;
    public String title;
    public String body;
    public String phone;
    public String email;
    public String categoria;
    public String imageBg;
    public String latitud;
    public String longitud;
    public String id;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Constructor predeterminado requerido para llamadas a DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body, String email, String phone, String categoria, String imageBg, String latitud, String longitud,String id) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.email = email;
        this.phone = phone;
        this.categoria = categoria;
        this.imageBg = imageBg;
        this.latitud = latitud;
        this.longitud = longitud;
        this.id = id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("phone", phone);
        result.put("email", email);
        result.put("categoria", categoria);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("imageBg", imageBg);
        result.put("latitud", latitud);
        result.put("longitud", longitud);
        result.put("id", id);

        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLongitud(){ return longitud; }
    public String getLatitud(){
        return latitud;
    }

}
// [END post_class]