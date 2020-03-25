package com.pymespace.guiazlzc.fragment;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsPerfilFragment extends PostPerfilListFragment {

    public MyPostsPerfilFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // Todas mis publicaciones
        return databaseReference.child("user-posts")
                .child(getUid());
    }
}