package com.pymespace.guiazlzc.fragment;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyTopPostsFragment extends PostListFragment {

    public MyTopPostsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // Mis publicaciones principales por número de estrellas
        String myUserId = getUid();
        Query myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
                .orderByChild("starCount");

        return myTopPostsQuery;
    }
}