package wada.programmics.thewada.DialogFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wada.programmics.thewada.AdapterClass.EntityImageAdapter;

import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.ObjectClass.EntityImages;

import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;

public class CategoryDialogFragment extends DialogFragment {
    private Callback callback;
    private TextView tvCategory, tvDesc,tvEntityName,tvAddress,tvImg;
    private String categoryName, categoryDesc, categoryImageUrl, token;
    private ImageView imageCategory;
    private int categoryId;
    private FloatingActionButton phoneLink,webLink;
    private ProgressBar progressBar;

    private RecyclerView recyclerViewEntity;
    public static CategoryDialogFragment newInstance() {
        return new CategoryDialogFragment();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_dialog, container, false);
        ImageButton closeDialog = view.findViewById(R.id.fullscreen_dialog_close);
        tvCategory = view.findViewById(R.id.tvCategory);
        imageCategory = view.findViewById(R.id.imgCategory);
        tvDesc = view.findViewById(R.id.tvDesc);
        tvEntityName = view.findViewById(R.id.tvEntityName);
        tvAddress = view.findViewById(R.id.tvAddress);
        phoneLink = view.findViewById(R.id.phoneLink);
        webLink = view.findViewById(R.id.webLink);
        recyclerViewEntity = view.findViewById(R.id.recyclerViewEntity);
        tvImg =view.findViewById(R.id.tvImg);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        User user = SessionManager.getInstance(getActivity()).getUser();

        token = user.getToken();

        Bundle bundle = getArguments();
        categoryId = bundle.getInt("categoryId");
        categoryImageUrl = bundle.getString("categoryImg");

        Glide.with(getActivity())
                .load(categoryImageUrl)
                .into(imageCategory);


        getCategoryDetails(categoryId);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
    public interface Callback {

        void onActionClick(String name);
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        System.gc();
    }

    private void getCategoryDetails(final int categoryId){

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        recyclerViewEntity.setLayoutManager(staggeredGridLayoutManager);
        final List<EntityImages> list = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_VIEW_ENTITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("images",response);
                        try {

                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("success").equals("true")){

                                JSONObject jsonObject = obj.getJSONObject("data");

                                String title = jsonObject.getString("title");
                                String short_des = jsonObject.getString("short_des");
                                String long_des = jsonObject.getString("long_des");
                                String address = jsonObject.getString("address");

                                final String phone = jsonObject.getString("phone");
                                final String website = jsonObject.getString("website");

                                JSONArray jsonArray = jsonObject.getJSONArray("images");

                                if (jsonArray.length()>0){

                                    tvImg.setVisibility(View.VISIBLE);
                                    recyclerViewEntity.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        int id = jsonObject1.getInt("id");
                                        String imageUrl = jsonObject1.getString("image_loc");
                                        Log.e("imagesdk",imageUrl);
                                        list.add(new EntityImages(id,imageUrl));

                                    }

                                    EntityImageAdapter adapter = new EntityImageAdapter(getActivity(),list);
                                    recyclerViewEntity.setAdapter(adapter);
                                }
                                else {
                                    tvImg.setVisibility(View.GONE);
                                    recyclerViewEntity.setVisibility(View.GONE);
                                }


                                if (!website.equals("null")){
                                    webLink.setVisibility(View.VISIBLE);

                                    webLink.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Uri uriUrl = Uri.parse(website);
                                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                                            startActivity(launchBrowser);
                                        }
                                    });

                                }


                                phoneLink.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+phone));
                                        startActivity(intent);
                                    }
                                });

                                tvCategory.setText(title);
                                tvEntityName.setText(title);
                                tvAddress.setText(""+address);

                                tvDesc.setText(long_des);



                                progressBar.setVisibility(View.GONE);



                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Log.e("errr", ""+error.getMessage());
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
                HashMap<String,String> params = new HashMap<>();
                params.put("entity_id", String.valueOf(categoryId));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);



    }
}
