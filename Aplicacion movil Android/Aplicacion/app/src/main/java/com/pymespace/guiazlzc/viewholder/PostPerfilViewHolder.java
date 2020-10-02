package com.pymespace.guiazlzc.viewholder;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pymespace.guiazlzc.R;
import com.pymespace.guiazlzc.models.Post;

public class PostPerfilViewHolder extends RecyclerView.ViewHolder {
    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public Button btnEditPost, btnDeletePost;

    public PostPerfilViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.postTitle);
        authorView = itemView.findViewById(R.id.postAuthor);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.postNumStars);
        bodyView = itemView.findViewById(R.id.postBody);
        btnEditPost = itemView.findViewById(R.id.btnEditPost);
        btnDeletePost = itemView.findViewById(R.id.btnDeletePost);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);

        starView.setOnClickListener(starClickListener);
        //btnDeletePost.setOnClickListener(starClickListener);
        //btnEditPost.setOnClickListener(starClickListener);
    }
}
