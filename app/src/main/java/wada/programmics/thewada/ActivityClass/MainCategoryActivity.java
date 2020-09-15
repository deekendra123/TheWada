package wada.programmics.thewada.ActivityClass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wada.programmics.thewada.AdapterClass.MainCategoryAdapter;
import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.Config.CheckInternet;
import wada.programmics.thewada.ObjectClass.MainCategoryData;
import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;

public class MainCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategory;
    private MainCategoryAdapter mainCategoryAdapter;
    private List<MainCategoryData> list;
    private int cat_id, city_id;
    private String location,token;
    private Spinner spinner;
    private ImageView imgNoDataFound;
    private EditText etSearchView;
    private TextView textview;
    private ProgressBar progressBar;



    private int[] catImg = new int[]{R.drawable.wada, R.drawable.wada1, R.drawable.wada2, R.drawable.wada};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_category);
        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        spinner = findViewById(R.id.spinner);
        etSearchView = findViewById(R.id.etSearchView);
        imgNoDataFound = findViewById(R.id.imgNoDataFound);
        textview = findViewById(R.id.textview);

        progressBar = findViewById(R.id.progressBar);
       // progressBar.setVisibility(View.VISIBLE);


        User user = SessionManager.getInstance(MainCategoryActivity.this).getUser();
        token = user.getToken();

        CheckInternet checkInternet = new CheckInternet(getApplicationContext());

        if (checkInternet.isOnline()==true){
            getCities();
        }
        else {

            new AlertDialog.Builder(MainCategoryActivity.this)
                    .setTitle("Alert")
                    .setCancelable(false)
                    .setMessage("No Network. Please check your internet connection")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            getCities();
                        }
                    }).create().show();

        }


        SharedPreferences sharedPreferences = getSharedPreferences("category", MODE_PRIVATE);
        cat_id = sharedPreferences.getInt("category_id",0);
        city_id = sharedPreferences.getInt("city_id", 0);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewCategory.setLayoutManager(layoutManager);
        list = new ArrayList<>();

        searchQuery();

    }



    private void searchQuery(){
        etSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                editable.toString();
                filter(editable.toString());
            }
        });

    }

    private void filter(String text) {
        ArrayList<MainCategoryData> list1 = new ArrayList<>();

        for (MainCategoryData mainCategoryData : list){
            if (mainCategoryData.getCategoryName().toLowerCase().contains(text.toLowerCase())) {
                list1.add(mainCategoryData);
            }
        }
        mainCategoryAdapter.filterList(list1);
    }


    private void getCities() {

        final List<String> categories = new ArrayList<String>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("success").equals("true")){

                                JSONArray jsonArray = obj.getJSONArray("data");

                                categories.add("All");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String city = jsonObject.getString("name");

                                    categories.add(city);
                                }

                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(dataAdapter);

                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        location = parent.getItemAtPosition(position).toString();

                                        progressBar.setVisibility(View.VISIBLE);
                                        list.clear();
                                        loadSubCategory(cat_id, position, location);

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(MainCategoryActivity.this);

        requestQueue.add(stringRequest);
    }

    private void loadSubCategory(final int category_id, final int city_id, final String location) {

        Log.e("data",city_id+ "  "+category_id);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SUBCATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response",response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("success").equals("true")){

                                JSONObject data = obj.getJSONObject("data");
                                JSONArray  jsonArray = data.getJSONArray("entity");

                                if (jsonArray.length()>0) {

                                    recyclerViewCategory.setVisibility(View.VISIBLE);
                                    imgNoDataFound.setVisibility(View.INVISIBLE);
                                    textview.setVisibility(View.INVISIBLE);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String name = jsonObject.getString("title");
                                        String des = jsonObject.getString("short_des");
                                        String discount = jsonObject.getString("discount");
                                        String address = jsonObject.getString("address");

                                        int id = jsonObject.getInt("id");
                                        JSONObject object = jsonObject.getJSONObject("thumbnail");

                                        String imageUrl = object.getString("image_loc");

                                        JSONObject jsonObject1 = jsonObject.getJSONObject("city_data");
                                        String location = jsonObject1.getString("name");

                                        list.add(new MainCategoryData(name, des, location, imageUrl, discount, id,address));
                                    }
                                }
                                else {
                                    recyclerViewCategory.setVisibility(View.INVISIBLE);
                                    imgNoDataFound.setVisibility(View.VISIBLE);
                                    textview.setVisibility(View.VISIBLE);

                                }

                                mainCategoryAdapter = new MainCategoryAdapter(MainCategoryActivity.this, (ArrayList<MainCategoryData>) list);
                                recyclerViewCategory.setAdapter(mainCategoryAdapter);
                                mainCategoryAdapter.notifyDataSetChanged();
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
                params.put("category", String.valueOf(category_id));
                params.put("city", String.valueOf(city_id));

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainCategoryActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("category", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
