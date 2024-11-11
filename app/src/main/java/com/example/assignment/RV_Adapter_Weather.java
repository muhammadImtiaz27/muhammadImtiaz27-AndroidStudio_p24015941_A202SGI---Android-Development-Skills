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

public class RV_Adapter_Weather extends RecyclerView.Adapter<RV_Adapter_Weather.ViewHolder> {

    private Context context;
    private ArrayList<Weather> weather_rv_data_arrayList;

    public RV_Adapter_Weather(Context context, ArrayList<Weather> weather_rv_data_arrayList) {
        this.context = context;
        this.weather_rv_data_arrayList = weather_rv_data_arrayList;
    }

    @NonNull
    @Override
    public RV_Adapter_Weather.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_weather_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RV_Adapter_Weather.ViewHolder holder, int position) {

        Weather data = weather_rv_data_arrayList.get(position);

        holder.tv_temperature.setText(data.getTemperature() + "°C");

        Picasso.get().load("https:".concat(data.getIcon())).into(holder.iv_condition);

        holder.tv_wind_speed.setText(data.getWindSpeed() + "Km/h");

        // yyyy is year. MM is month. dd is day. HH is 24-hour format. mm is minute.
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm");  // 24-hour input format
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");  // 12-hour output format with AM/PM

        try{
            Date t = input.parse(data.getTime());
            holder.tv_time.setText(output.format(t));
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weather_rv_data_arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_wind_speed, tv_temperature, tv_time;
        private ImageView iv_condition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_wind_speed = itemView.findViewById(R.id.tv_wind_speed);
            tv_temperature = itemView.findViewById(R.id.tv_temperature);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_condition = itemView.findViewById(R.id.iv_condition);

        }
    }
}
