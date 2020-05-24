package wada.programmics.thewada.AdapterClass;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wada.programmics.thewada.ObjectClass.NewsData;
import wada.programmics.thewada.ObjectClass.NotificationData;
import wada.programmics.thewada.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mCtx;
    private List<NotificationData> list;

    public NotificationAdapter(Context mCtx, List<NotificationData> list) {
        this.mCtx = mCtx;
        this.list = list;
    }


    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.notification_list_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {

        final NotificationData notificationData = list.get(position);

       // holder.imageView.setImageResource(NewsData.getImageUrl());

        holder.tvTitle.setText(notificationData.getTitle());
        holder.tvMsg.setText(notificationData.getMessage());

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM");
        String currentDate = df.format(c);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        String yesterdayAsString = dateFormat.format(calendar.getTime());


        if (notificationData.getDate().equals(currentDate)){
            holder.tvDate.setText("today");
        }
        else if (notificationData.getDate().equals(yesterdayAsString)){
            holder.tvDate.setText("yesterday");

        }
        else {
            holder.tvDate.setText(notificationData.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgNotification;
        TextView tvTitle, tvMsg,tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgNotification = itemView.findViewById(R.id.imgNotification);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
