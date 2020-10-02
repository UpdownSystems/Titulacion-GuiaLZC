package com.pymespace.guiazlzc.fragment;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pymespace.guiazlzc.EditPostActivity;
import com.pymespace.guiazlzc.PostDetailScrollingActivity;
import com.pymespace.guiazlzc.R;
import com.pymespace.guiazlzc.models.Post;
import com.pymespace.guiazlzc.viewholder.PostPerfilViewHolder;
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

public abstract class PostPerfilListFragment extends Fragment {
    private static final String TAG = "PostListFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Post, PostPerfilViewHolder> mAdapterP;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    Post publicacion;

    public PostPerfilListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts_perfil, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = rootView.findViewById(R.id.messagesListPerfil);
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

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(postsQuery, Post.class).build();

        mAdapterP = new FirebaseRecyclerAdapter<Post, PostPerfilViewHolder>(options) {

            @Override
            public PostPerfilViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PostPerfilViewHolder(inflater.inflate(R.layout.item_post_perfil, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(PostPerfilViewHolder viewHolder, int position, final Post model) {
                final DatabaseReference postRef = getRef(position);

                // Establecer click listener para toda la vista de publicación
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Lanzamiento PostDetailScrollingActivity
                        Intent intent = new Intent(getActivity(), PostDetailScrollingActivity.class);
                        intent.putExtra(PostDetailScrollingActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                // Establecer click listener para la vista Editar Publicacion
                viewHolder.btnEditPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EditPostActivity.class);
                        intent.putExtra(EditPostActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });


                // Determine si al usuario actual le ha gustado esta publicación y configure la IU en consecuencia
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_favorite_red_24dp);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }

                // Enlazar publicación a ViewHolder, configurando OnClickListener para el botón de la estrella
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Necesito escribir en ambos lugares donde se almacena la publicación
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());

                        // Ejecuta dos transacciones
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapterP);
    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Desmarque la publicación y elimínese de las estrellas
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Destacar la publicación y agregarse a las estrellas
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Establecer valor e informar el éxito de la transacción
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transacción completada
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapterP != null) {
            mAdapterP.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapterP != null) {
            mAdapterP.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}
