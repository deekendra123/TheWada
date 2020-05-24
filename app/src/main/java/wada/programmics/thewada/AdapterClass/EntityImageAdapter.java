package wada.programmics.thewada.AdapterClass;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.List;

import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.ObjectClass.EntityImages;
import wada.programmics.thewada.R;

public class EntityImageAdapter extends RecyclerView.Adapter<EntityImageAdapter.ViewHolder> {

    private Context context;
    private List<EntityImages> list;

    public EntityImageAdapter(Context context, List<EntityImages> list) {
        this.context = context;
        this.list = list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.image_list_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final EntityImages data = list.get(position);

        final String image = AppConfig.BASE_IMAGE_URL+data.getImageUrl();

        Glide.with(context)
                .load(image)
                .into(holder.imgOffer);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View mView = LayoutInflater.from(context).inflate(R.layout.zoom_image_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageView);

                Glide.with(context).load(image).into(photoView);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgOffer,imgChecke;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgOffer = itemView.findViewById(R.id.imgOffer);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }





}
