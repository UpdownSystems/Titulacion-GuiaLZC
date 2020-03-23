package com.example.guiazlzc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guiazlzc.MapsActivity;
import com.example.guiazlzc.PostDetailScrollingActivity;
import com.example.guiazlzc.R;
import com.example.guiazlzc.models.Post;
import com.example.guiazlzc.viewholder.MapViewHolder;
import com.example.guiazlzc.viewholder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
public abstract class MyMapFragment extends Fragment {
    private static final String TAG = "PostListFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Post, MapViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public MyMapFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.include_post_detail, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = rootView.findViewById(R.id.messagesList);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Configurar Layout Manager, diseño inverso
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Configurar FirebaseRecyclerAdapter con la consulta
        Query postsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Post, MapViewHolder>(options) {

            @Override
            protected void onBindViewHolder(MapViewHolder mapViewHolder, int position, final Post model) {
                final DatabaseReference postRef = getRef(position);

                // Establecer click listener para toda la vista de publicación
                final String postKey = postRef.getKey();
                mapViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Lanzamiento MapsActivity
                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        intent.putExtra(MapsActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public MapViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MapViewHolder(inflater.inflate(R.layout.include_post_detail, viewGroup, false));
            }
        };
        mRecycler.setAdapter(mAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}

