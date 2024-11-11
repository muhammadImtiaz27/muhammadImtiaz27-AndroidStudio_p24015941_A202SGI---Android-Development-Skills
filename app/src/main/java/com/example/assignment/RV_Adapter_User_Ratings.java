package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RV_Adapter_User_Ratings extends RecyclerView.Adapter<RV_Adapter_User_Ratings.ViewHolder> {

    private Context context;
    private List<UserRatings> list_of_user_ratings;

    public RV_Adapter_User_Ratings(Context context, List<UserRatings> list_of_user_ratings) {
        this.context = context;
        this.list_of_user_ratings = list_of_user_ratings;
    }

    @NonNull
    @Override
    public RV_Adapter_User_Ratings.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_user_rating_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RV_Adapter_User_Ratings.ViewHolder holder, int position) {

        UserRatings user_rating = list_of_user_ratings.get(position);

        holder.tv_user_rating_email.setText(user_rating.getUser_email());
        holder.tv_user_rating_description.setText(user_rating.getDescription());
        holder.rb_user_rating_num_of_stars.setRating(user_rating.getNum_of_stars());

        // Load the image into the ImageView using Picasso
        Picasso.get()
                .load(user_rating.getPhoto_uri())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .into(holder.iv_user_rating_photo);

    }

    @Override
    public int getItemCount() {
        return list_of_user_ratings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_user_rating_email, tv_user_rating_description;
        private ImageView iv_user_rating_photo;
        private RatingBar rb_user_rating_num_of_stars;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_user_rating_email = itemView.findViewById(R.id.tv_user_rating_email);
            tv_user_rating_description = itemView.findViewById(R.id.tv_user_rating_description);
            iv_user_rating_photo = itemView.findViewById(R.id.iv_user_rating_photo);
            rb_user_rating_num_of_stars = itemView.findViewById(R.id.rb_user_rating_num_of_stars);

        }
    }
}