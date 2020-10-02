package com.pymespace.guiazlzc;
/*
  Echo por: Updown Systems
  Programado por: Jorge Luis Perez Medina
*/
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.pymespace.guiazlzc.service.MyDownloadService;
import com.pymespace.guiazlzc.service.MyUploadService;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pymespace.guiazlzc.models.Comment;
import com.pymespace.guiazlzc.models.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetailScrollingActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PostDetailScrollingActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mPostReference, mPostReference2;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private CommentAdapter mAdapter;

    private TextView mAuthorView;
    private TextView mTitleView, mEmailView, mPhoneView, mMapaView;
    private TextView mBodyView, phoneCall;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarColapsing;
    private ImageView scrollingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail_scrolling);

        // Obtener la clave de la publicación del Intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Inicializamos la Base de datos
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference().child("post-comments").child(mPostKey);

        // Inicializamos las Vistas Widgets
        mAuthorView = findViewById(R.id.postAuthor);
        mTitleView = findViewById(R.id.postTitle);
        mBodyView = findViewById(R.id.postBody);
        mCommentField = findViewById(R.id.fieldCommentText);
        mCommentButton = findViewById(R.id.buttonPostComment);
        mCommentsRecycler = findViewById(R.id.recyclerPostComments);
        mPhoneView = findViewById(R.id.txtPhoneAdd);
        mMapaView = findViewById(R.id.txtMapaAdd);
        mEmailView = findViewById(R.id.txtEmaiAdd);
        phoneCall = findViewById(R.id.txtPhoneAdd);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarColapsing = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Establecer click listener para mandar a aplicacion de llamadas
        phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = phoneCall.getText().toString();
                if(!TextUtils.isEmpty(phoneNo)){
                    String dial = phoneNo;
                    Intent intent =  new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", dial, null));
                    startActivity(intent);
                }
            }
        });

        // Establecemos Listener para que me lleve  a Vista del Mapa


        //final String postKey = mPostReference;
        mMapaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailScrollingActivity.this, MapsActivity.class);
                intent.putExtra(MapsActivity.EXTRA_POST_KEY, mPostKey);
                startActivity(intent);
                Log.i("Holaaaa: ", mPostKey);

                //startActivity(new Intent(PostDetailScrollingActivity.EXTRA_POST_KEY, MapsActivity.class));
            }
        });



        // Obtenemos la direccion de la imagen que se mostrará en la vista del Post
        DatabaseReference databaseReference11 = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);

        Log.i("Este es el UID del Post", mPostKey);

        //Query lastQuery = databaseReference11.child("posts").child(mPostKey);
        databaseReference11.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageView imageView = findViewById(R.id.image_scrolling_top);
                String imgBg = dataSnapshot.child("imageBg").getValue().toString();

                Glide.with(getApplicationContext())
                        .load(imgBg)
                        .into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });

        /*mPostReference2 = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey).child("imageBg");
        String url = "https://firebasestorage.googleapis.com/v0/b/otros-7b06b.appspot.com/o/photos%2Fimage%3A12209?alt=media&token=65ea422f-2cb4-49cb-902c-b3a4608e8a65";*/
        //FirebaseDatabase Reference = FirebaseDatabase.getInstance().getReference().child("users-posts").child(uid);

        // ImageView in your Activity
        //ImageView imageView = findViewById(R.id.image_scrolling_top);

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        /*Glide.with(getApplicationContext())
                .load(url)
                .into(img);*/

    }

    @Override
    public void onStart() {
        super.onStart();

        // Agregue valor al oyente de eventos a la publicación
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Obtenga el objeto Post y use los valores para actualizar la IU
                Post post = dataSnapshot.getValue(Post.class);
                mAuthorView.setText(post.author);
                mTitleView.setText(post.title);
                mBodyView.setText(post.body);
                toolbarColapsing.setTitle(post.title);
                mEmailView.setText(post.email);
                mPhoneView.setText(post.phone);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error al obtener la publicación, registre un mensaje
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException());

                Toast.makeText(PostDetailScrollingActivity.this, "No se pudo cargar los negocios",
                        Toast.LENGTH_SHORT).show();

            }
        };
        mPostReference.addValueEventListener(postListener);

        // Guarde una copia del post, escucha para que podamos eliminarla cuando la aplicación se detenga
        mPostListener = postListener;

        // Escucha los comentarios
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Maneje los elementos de la barra de acción haciendo clic aquí La barra de acción
        // maneja automáticamente los clics en el botón Inicio / Arriba, tanto tiempo
        // al especificar una actividad principal en AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Eliminar el detector de eventos de valor de publicación
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        // Limpiar comentarios oyente
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonPostComment) {
            postComment();
        }
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Obtenemos la inforamacion del Usuario
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.nombreJefe;

                        // Creamos el nuevo objeto Comment
                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Envia el comentario y aparecerá en la lista
                        mCommentsReference.push().setValue(comment);

                        // Limpia el Field
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.commentAuthor);
            bodyView = itemView.findViewById(R.id.commentBody);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Crear oyente de eventos hijo
            ChildEventListener childEventListener = new ChildEventListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // Se ha agregado un nuevo comentario, agréguelo a la lista mostrada
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // Uctualizamos RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);

                }

                @SuppressLint("LongLogTag")
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // Un comentario ha cambiado, use la tecla para determinar si estamos mostrando esto
                    // comente y, si es así, muestra el comentario modificado.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();


                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Reemplazar con los nuevos datos
                        mComments.set(commentIndex, newComment);

                        // Actualizar el RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }

                }

                @SuppressLint("LongLogTag")
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // Un comentario ha cambiado, use la tecla para determinar si estamos mostrando esto
                    // comentar y si es así, eliminarlo.
                    String commentKey = dataSnapshot.getKey();


                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Eliminar datos de la lista
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Actualizamos el RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }

                }

                @SuppressLint("LongLogTag")
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // Un comentario ha cambiado de posición, use la tecla para determinar si estamos
                    // mostrando este comentario y si es así muévalo.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);


            // Almacene la referencia al oyente para que se pueda eliminar al detener la aplicación
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
}
