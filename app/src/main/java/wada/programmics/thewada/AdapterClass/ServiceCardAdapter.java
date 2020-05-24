package wada.programmics.thewada.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import wada.programmics.thewada.ObjectClass.BannerData;
import wada.programmics.thewada.R;

public class ServiceCardAdapter extends RecyclerView.Adapter<ServiceCardAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BannerData> discountCardModelArrayList;

    public ServiceCardAdapter(Context context, ArrayList<BannerData> discountCardModelArrayList) {
        this.context = context;
        this.discountCardModelArrayList = discountCardModelArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.discount_card_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        BannerData bannerData = discountCardModelArrayList.get(position);
        Glide.with(context)
                .load(bannerData.getService_images())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return discountCardModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.discount_card_images);
        }
    }
}
