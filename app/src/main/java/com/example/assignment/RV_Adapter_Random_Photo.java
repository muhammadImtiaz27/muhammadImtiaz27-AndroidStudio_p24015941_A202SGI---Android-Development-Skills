package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RV_Adapter_Random_Photo extends RecyclerView.Adapter<RV_Adapter_Random_Photo.ViewHolder> {

    private Context context;
    private List<Unsplash_Random_Photo> list_of_unsplash_photos;

    public RV_Adapter_Random_Photo(Context context, List<Unsplash_Random_Photo> list_of_unsplash_photos) {
        this.context = context;
        this.list_of_unsplash_photos = list_of_unsplash_photos;
    }

    @NonNull
    @Override
    public RV_Adapter_Random_Photo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_random_photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RV_Adapter_Random_Photo.ViewHolder holder, int position) {

        Unsplash_Random_Photo photo = list_of_unsplash_photos.get(position);

        // Some images provided by Unsplash API, set the description key to null. So we need to adjust.
        // Furthermore, the long text inside the setText() method, converts the first character of the string to uppercase
        // Check if the description is null
        if(photo.getDescription().equals("null")){
            holder.tv_unsplash_photo_description.setText(String.format("%s%s", photo.getAlt_description().substring(0, 1).toUpperCase(), photo.getAlt_description().substring(1)));
        }

        // If the description is not null
        else {
            holder.tv_unsplash_photo_description.setText(String.format("%s%s", photo.getDescription().substring(0, 1).toUpperCase(), photo.getDescription().substring(1)));
        }

        // Load the image into the ImageView using Picasso
        Picasso.get()
                .load(photo.getUrl())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .into(holder.iv_unsplash_photo, new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess() {
                        holder.iv_unsplash_photo.setContentDescription(photo.getAlt_description());
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.iv_unsplash_photo.setContentDescription("error");
                    }
                });

    }

    @Override
    public int getItemCount() {
        return list_of_unsplash_photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_unsplash_photo_description;
        private ImageView iv_unsplash_photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_unsplash_photo_description = itemView.findViewById(R.id.tv_unsplash_photo_description);
            iv_unsplash_photo = itemView.findViewById(R.id.iv_unsplash_photo);

        }
    }
}