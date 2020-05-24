package wada.programmics.thewada.AdapterClass;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wada.programmics.thewada.ObjectClass.NewsData;
import wada.programmics.thewada.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mCtx;
    private ArrayList<NewsData> list;

    public NewsAdapter(Context mCtx, ArrayList<NewsData> list) {
        this.mCtx = mCtx;
        this.list = list;
    }


    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.news_list_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {

        final NewsData NewsData = list.get(position);

        holder.imageView.setImageResource(NewsData.getImageUrl());
        holder.tvMainCategory.setText(NewsData.getNewsTitle());
        holder.tvDesc.setText(NewsData.getNewsDesc());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvMainCategory, tvDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgCategory);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvMainCategory = itemView.findViewById(R.id.tvMainCategory);
        }
    }
}
