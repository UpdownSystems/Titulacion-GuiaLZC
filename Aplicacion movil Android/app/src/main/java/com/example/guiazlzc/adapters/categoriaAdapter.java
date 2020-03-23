package com.example.guiazlzc.adapters;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guiazlzc.R;
import com.example.guiazlzc.models.categorias;

import java.util.ArrayList;

public class categoriaAdapter extends RecyclerView.Adapter<categoriaAdapter.ViewHolder> implements View.OnClickListener {
    private int resource;
    private ArrayList<categorias> categoriasList;
    private View.OnClickListener listener;

    public categoriaAdapter(ArrayList<categorias> categoriasList, int resource) {
        this.categoriasList = categoriasList;
        this.resource = resource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        return  new ViewHolder(view);
    }

    // Lo que queremos hacer con el RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        categorias categoria = categoriasList.get(index);

        viewHolder.nameCategoria.setText(categoria.getNameCategoria());

        //final DatabaseReference postRef = getRef(index);
        /*final String postKey = postRef.getKey();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lanzamiento PostDetailScrollingActivity
                Intent intent = new Intent(getActivity(), PostDetailScrollingActivity.class);
                intent.putExtra(PostDetailScrollingActivity.EXTRA_POST_KEY, postKey);
                startActivity(intent);
            }
        });*/
    }

    // Obtiene el Numero de categorias creadas
    @Override
    public int getItemCount() {
        return categoriasList.size();
    }

    //
    public void setOnClickListener (View.OnClickListener listener) {this.listener = listener;}

    // Metodo para el OnClickListener
    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameCategoria;
        public View view;

        public ViewHolder(View view) {
            super(view);

            this.view = view;
            this.nameCategoria = (TextView)view.findViewById(R.id.setCategoria);
        }
    }
}
