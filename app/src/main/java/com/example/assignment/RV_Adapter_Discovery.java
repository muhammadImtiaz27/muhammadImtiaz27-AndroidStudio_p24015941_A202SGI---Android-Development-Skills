package com.example.assignment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RV_Adapter_Discovery extends RecyclerView.Adapter<RV_Adapter_Discovery.ViewHolder> {

    private List<TouristSpot> list_of_tourist_spots;
    private Context context;
    private int layout_resource;

    // Constructor
    public RV_Adapter_Discovery(Context context, List<TouristSpot> list_of_tourist_spots, int layout_resource){
        this.context = context;
        this.list_of_tourist_spots = list_of_tourist_spots;
        this.layout_resource = layout_resource;
    }

    @NonNull
    @Override
    public RV_Adapter_Discovery.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RV_Adapter_Discovery.ViewHolder(LayoutInflater.from(context).inflate(layout_resource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RV_Adapter_Discovery.ViewHolder holder, int position) {

        //Get current tourist spot
        TouristSpot tourist_spot = list_of_tourist_spots.get(position);

        //Populate the views with data
        holder.bindTo(tourist_spot);

    }

    @Override
    public int getItemCount() {
        return list_of_tourist_spots.size();
    }

    // ViewHolder class to hold individual item views
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_tourist_spot_name, tv_tourist_spot_state, tv_tourist_spot_category;
        private final ImageView iv_tourist_spot_state, iv_tourist_spot_thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views
            tv_tourist_spot_name = itemView.findViewById(R.id.tv_tourist_spot_name);
            tv_tourist_spot_state = itemView.findViewById(R.id.tv_tourist_spot_state);
            tv_tourist_spot_category = itemView.findViewById(R.id.tv_tourist_spot_category);
            iv_tourist_spot_state = itemView.findViewById(R.id.iv_tourist_spot_state);
            iv_tourist_spot_thumbnail = itemView.findViewById(R.id.iv_tourist_spot_thumbnail);

            //What happens when the user click the discovery
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    TouristSpot tourist_spot = list_of_tourist_spots.get(getAdapterPosition());
                    Intent intent = new Intent(context, Page_TouristSpot_Details_Activity.class);
                    intent.putExtra("tourist_spot", tourist_spot);
                    context.startActivity(intent);
                }
            });

        }

        public void bindTo(TouristSpot tourist_spot) {

            tv_tourist_spot_name.setText(tourist_spot.getName());
            tv_tourist_spot_state.setText(tourist_spot.getState());
            tv_tourist_spot_category.setText(tourist_spot.getCategory());

            //Picasso makes loading the images from url easily
            Picasso
                    .get()
                    .load(tourist_spot.tourist_spot_uri.getThumbnail_uri())
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(iv_tourist_spot_thumbnail);

            switch (tourist_spot.getState()){
                case "Johor":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_johor);
                    break;
                case "Kedah":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_kedah);
                    break;
                case "Kelantan":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_kelantan);
                    break;
                case "Melaka":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_melaka);
                    break;
                case "Negeri Sembilan":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_negeri_sembilan);
                    break;
                case "Perak":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_perak);
                    break;
                case "Perlis":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_perlis);
                    break;
                case "Pahang":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_pahang);
                    break;
                case "Pulau Pinang":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_pulau_pinang);
                    break;
                case "Sabah":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_sabah);
                    break;
                case "Sarawak":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_sarawak);
                    break;
                case "Selangor":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_selangor);
                    break;
                case "Terengganu":
                    iv_tourist_spot_state.setImageResource(R.drawable.flag_terengganu);
                    break;
            }

        }
    }
}
