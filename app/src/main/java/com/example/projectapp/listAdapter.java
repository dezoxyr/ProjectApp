package com.example.projectapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projectapp.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class listAdapter extends RecyclerView.Adapter<listAdapter.ViewHolder> {
    private List<Genres> values;
    private List<Anime> animevalues;
    private String img_url = "https://www.pngfind.com/pngs/m/140-1404349_logo-manga-png-manga-entertainment-logo-transparent-png.png";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView txtHeader;
        TextView txtFooter;
        View layout;
        ImageView img;

        ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
            img = (ImageView) v.findViewById(R.id.icon);
        }
    }

    public void add(int position, Genres item) {
        values.add(position, item);
        notifyItemInserted(position);
    }
    public void addA(int position, Anime item) {
        animevalues.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        if(values != null) {
            values.remove(position);
        }
        if(animevalues != null) {
            animevalues.remove(position);
        }
        notifyItemRemoved(position);
    }

    public int getID(int position){
       return animevalues.get(position).getMal_id();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public listAdapter(List<Genres> myDataset,List<Anime> anime) {
        values = myDataset;
        animevalues = anime;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public listAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(values != null) {
            final Genres genre = values.get(position);
            holder.txtHeader.setText(genre.getName());
            holder.txtFooter.setText("Type : " + genre.getType());

            Picasso.get().load(img_url).resize(170,180).into(holder.img);

        }else if(animevalues != null){
            final Anime anime = animevalues.get(position);
            holder.txtHeader.setText(anime.getTitle());
            /*holder.txtHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
            holder.txtFooter.setText("Type : " + anime.getType());

            Picasso.get().load(anime.getImage_url()).resize(150,180).into(holder.img);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(values != null) {
            return values.size();
        }else if(animevalues != null){
            return animevalues.size();
        }
        return 0;
    }

}
