package com.example.projectbeer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context c;

    int images[];
    float ratings[],  degrees[];;
    String beerNames[];
    int idCatalogs[];

    ImageButton deleteButton, modifyButton;


    public Adapter(Context a_c, int a_images[], String a_beerNames[], float a_degrees[], float a_ratings[], int idCatalogs[]){
        c = a_c;
        images = a_images;
        degrees = a_degrees;
        ratings = a_ratings;
        beerNames = a_beerNames;
        this.idCatalogs = idCatalogs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.beer_layout_in_catalogue, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.image.setImageResource(images[position]);

        holder.tv_beerName.setText(beerNames[position]);
        holder.tv_degree.setText(String.valueOf(degrees[position]) + "°");
        holder.rating.setRating(ratings[position]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenBeerDetails(position);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { DeleteBeer(position); }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ModifyBeer(position); }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
            modifyButton = itemView.findViewById(R.id.modifyButton);
        }
    }

    @Override
    public int getItemCount() { return beerNames.length; }

    void OpenBeerDetails(int position){
        Intent beerDetailsActivity = new Intent(c, BeerDetails.class);

        Log.i("test", "item " + String.valueOf(position));

        beerDetailsActivity.putExtra("idCatalog", idCatalogs[position]);
        c.startActivity(beerDetailsActivity);
    }

    void ModifyBeer(int position){
        Intent modifyActivity = new Intent(c, AddPersonalBeerData.class);
        modifyActivity.putExtra("idCatalog", idCatalogs[position]);
        c.startActivity(modifyActivity);
    }

    void DeleteBeer(int position){
        //Supprime bière de la bdd personnelle seulement
        Toast.makeText(c, "Beer deleted", Toast.LENGTH_SHORT).show();
    }
}