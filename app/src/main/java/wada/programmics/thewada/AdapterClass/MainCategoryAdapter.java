package wada.programmics.thewada.AdapterClass;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.DialogFragment.CategoryDialogFragment;
import wada.programmics.thewada.ObjectClass.DiscountCardData;
import wada.programmics.thewada.ObjectClass.MainCategoryData;
import wada.programmics.thewada.R;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.ViewHolder> {

    private Context mCtx;
    private ArrayList<MainCategoryData> list;

    public MainCategoryAdapter(Context mCtx, ArrayList<MainCategoryData> list) {
        this.mCtx = mCtx;
        this.list = list;
    }


    @NonNull
    @Override
    public MainCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.main_category_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainCategoryAdapter.ViewHolder holder, int position) {

        final MainCategoryData mainCategoryData = list.get(position);

        holder.imageView.setImageResource(mainCategoryData.getImageUrl());
        holder.tvMainCategory.setText(mainCategoryData.getCategoryName());
        holder.tvDesc.setText(mainCategoryData.getAddress());
        holder.tvLoc.setText(mainCategoryData.getLocation());
        holder.tvDiscount.setText(mainCategoryData.getDiscount()+ "% Discount");

        final String imageurl = AppConfig.BASE_IMAGE_URL+ mainCategoryData.getImage_url();

        Glide.with(mCtx)
                .load(imageurl)
                .into(holder.imageView);


        holder.itemView.setClickable(false);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity)mCtx).getSupportFragmentManager();
                DialogFragment dialog = CategoryDialogFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("categoryId", mainCategoryData.getId());
                bundle.putString("categoryImg", imageurl);
                dialog.setArguments(bundle);
                dialog.show(fm,"tag");
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvMainCategory, tvDesc, tvLoc, tvDiscount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgCategory);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvMainCategory = itemView.findViewById(R.id.tvMainCategory);
            tvLoc = itemView.findViewById(R.id.tvLoc);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
        }
    }

    public void filterList(ArrayList<MainCategoryData> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

}
