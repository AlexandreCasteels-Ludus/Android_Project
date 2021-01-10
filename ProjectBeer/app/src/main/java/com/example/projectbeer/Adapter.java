package com.example.projectbeer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context c;

    int images[];
    float ratings[],  degrees[];;
    String beerNames[];

    ImageButton deleteButton;

    public Adapter(Context a_c, int a_images[], String a_beerNames[], float a_degrees[], float a_ratings[]){
        c = a_c;
        images = a_images;
        degrees = a_degrees;
        ratings = a_ratings;
        beerNames = a_beerNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.beer_layout_in_catalogue, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { OpenBeerDetails(); }
        });

        return new ViewHolder(view);
    }

    //public interface Listener{ void onClickDeleteButton(int position); }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageResource(images[position]);
        holder.tv_beerName.setText(beerNames[position]);
        holder.tv_degree.setText(String.valueOf(degrees[position]) + "°");
        holder.rating.setRating(ratings[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView tv_beerName, tv_degree;
        RatingBar rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_rv);
            tv_beerName = itemView.findViewById(R.id.beerName_rv);
            tv_degree = itemView.findViewById(R.id.degree_rv);
            rating = itemView.findViewById(R.id.rating_rv);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteBeer();
                }
            });
        }
    }

    void OpenBeerDetails(){
        Intent beerDetailsActivity = new Intent(c, BeerDetails.class);
        c.startActivity(beerDetailsActivity);
    }

    void DeleteBeer(){
        //Supprime bière de la bdd personnelle seulement
    }
}