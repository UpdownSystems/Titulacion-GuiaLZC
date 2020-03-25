package com.pymespace.guiazlzc.models;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import com.google.firebase.database.IgnoreExtraProperties;

// [COMIENZO comment_class]
@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String author;
    public String text;

    public Comment() {
        // Constructor predeterminado requerido para llamadas a DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String author, String text) {
        this.uid = uid;
        this.author = author;
        this.text = text;
    }

}
// [FIN comment_class]