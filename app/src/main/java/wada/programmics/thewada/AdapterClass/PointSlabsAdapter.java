package wada.programmics.thewada.AdapterClass;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.ObjectClass.PointsData;
import wada.programmics.thewada.R;

public class PointSlabsAdapter extends RecyclerView.Adapter<PointSlabsAdapter.ViewHolder> {

    private Context context;
    private List<PointsData> list;
    private int userId;
    private int current_point;
    private String token;
    private TextView textView;
    private ProgressBar progressBar;

    public PointSlabsAdapter(Context context, List<PointsData> list, int userId, int current_point, String token, TextView textView, ProgressBar progressBar) {
        this.context = context;
        this.list = list;
        this.userId = userId;
        this.current_point = current_point;
        this.token = token;
        this.textView = textView;
        this.progressBar = progressBar;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.points_list_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final PointsData data = list.get(position);
        holder.imgOffer.setBackgroundResource(data.getImage());



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (current_point >= 300) {

                        if (data.getPoins() >= 300 && (data.getPoins() < 5000 && current_point >= 300 && data.getPoins()<500) ) {

                            Log.e("dkdsa",data.getPoins()+"");

                            showAlertDialog(data.getId(), holder.cardView, data.getPoins(), holder.imgChecke, position);

                        } else if (data.getPoins() >= 500 && (data.getPoins() < 5000 && current_point < 5000 && current_point >= 500)) {

                            Log.e("dkdsa",data.getPoins()+"");

                            showAlertDialog(data.getId(), holder.cardView, data.getPoins(), holder.imgChecke, position);

                        } else if (data.getPoins() >= 300 && (data.getPoins() >= 300 && current_point >= 5000)) {
                            Log.e("dkdsa",data.getPoins()+"");

                            showAlertDialog(data.getId(), holder.cardView, data.getPoins(), holder.imgChecke, position);
                        }

                    }
                    else {
                        Toast.makeText(context, "It seems like you don't have enough point to redeem!",Toast.LENGTH_SHORT).show();
                    }
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
            imgChecke = itemView.findViewById(R.id.imgChecke);
        }
    }

    public void showAlertDialog(final int slabId, final CardView relativeLayout, final int points, final ImageView imgChecke, final int position) {

        View alertLayout = LayoutInflater.from(context).inflate(R.layout.redeam_dialog, null);
        final Button btRedeam = alertLayout.findViewById(R.id.btRedeam);

        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.CustomDialogTheme);

        alert.setView(alertLayout);

        final AlertDialog dialog = alert.create();
        dialog.show();

        btRedeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               progressBar.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REDEAM,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject obj = new JSONObject(response);

                                    if (obj.getString("success").equals("true")){

                                        JSONObject object = obj.getJSONObject("data");
                                        String msg = object.getString("msg");

                                        Toast.makeText(context,""+ msg, Toast.LENGTH_SHORT).show();

                                        progressBar.setVisibility(View.GONE);
                                        dialog.dismiss();
                                        notifyDataSetChanged();
                                        getPoints();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error", error.getMessage());
                            }
                        }){

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        Map<String, String> headerMap = new HashMap<String, String>();
                        headerMap.put("Authorization", "Bearer " + token);
                        return headerMap;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        HashMap<String,String> parms = new HashMap<>();
                        parms.put("slab_id", String.valueOf(slabId));
                        parms.put("point", String.valueOf(points));
                        return parms;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);

            }
        });
    }

    public void getPoints() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_POINTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respo",response+"");

                        try {

                            JSONObject obj = new JSONObject(response);
                            int points = obj.getInt("data");
                            textView.setText(""+points);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("error", error.getMessage());

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Authorization", "Bearer " + token);
                return headerMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }



}
