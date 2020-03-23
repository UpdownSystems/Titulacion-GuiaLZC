package com.example.guiazlzc.viewholder;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.guiazlzc.R;
import com.example.guiazlzc.models.Post;

public class MapViewHolder extends RecyclerView.ViewHolder {
    public TextView txtMapa;

    public MapViewHolder(View itemView) {
        super(itemView);

        txtMapa = itemView.findViewById(R.id.txtMapaAdd);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        txtMapa.setText(post.latitud);
    }
}