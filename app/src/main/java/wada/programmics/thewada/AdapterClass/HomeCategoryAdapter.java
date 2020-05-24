package wada.programmics.thewada.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

import wada.programmics.thewada.ActivityClass.MainCategoryActivity;
import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.DialogFragment.CategoryDialogFragment;
import wada.programmics.thewada.ObjectClass.MainCategoryData;
import wada.programmics.thewada.R;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {

    private Context mCtx;
    private List<MainCategoryData> list;

    public HomeCategoryAdapter(Context mCtx, List<MainCategoryData> list) {
        this.mCtx = mCtx;
        this.list = list;
    }


    @NonNull
    @Override
    public HomeCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.home_category_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCategoryAdapter.ViewHolder holder, int position) {

        final MainCategoryData mainCategoryData = list.get(position);

        String image = AppConfig.BASE_IMAGE_URL+mainCategoryData.getImage_url();

        Glide.with(mCtx)
                .load(image)
                .into(holder.imageView);


        holder.tvMainCategory.setText(""+mainCategoryData.getCategoryName());
//        holder.tvDesc.setText(mainCategoryData.getCategoryDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences sharedPreferences = mCtx.getSharedPreferences("category", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("category_id", mainCategoryData.getId());
                editor.putInt("city_id",1);
                editor.commit();

                Intent intent = new Intent(mCtx, MainCategoryActivity.class);
                mCtx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Math.min(list.size(), 8);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvMainCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgCategory);
            tvMainCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
