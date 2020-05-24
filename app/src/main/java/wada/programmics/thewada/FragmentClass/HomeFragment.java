package wada.programmics.thewada.FragmentClass;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import wada.programmics.thewada.AdapterClass.BannerPagerAdapter;

import wada.programmics.thewada.AdapterClass.HomeCategoryAdapter;
import wada.programmics.thewada.AdapterClass.ServiceCardAdapter;
import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.DialogFragment.ServicesDialogFragment;
import wada.programmics.thewada.ObjectClass.BannerData;
import wada.programmics.thewada.ObjectClass.MainCategoryData;
import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;

public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView  servicerecyclerView;
    private TextView tvViewAll;
    private Button btnjoinmember, btShare;
    private ViewPager viewPager,viewPagerService;
    private CircleIndicator indicator,indicatorService;
    private RecyclerView recyclerViewHomeCategory;
    private LinearLayout linearLayoutServices, linearLayoutAboutCard, linearLayoutTravelCard, linearLayoutProduct;


    private String name, email, phone, ref_code,token;
    private int userId;
    private static int currentPage = 0;
    private static int curentPage1 = 0;
    private static int NUM_PAGES = 0;
    private static int NUM_PAGES1 = 0;

    private ArrayList<BannerData> list, list1,list2;
    private ImageView imgHelp;
    private ProgressBar progressBar;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        servicerecyclerView = view.findViewById(R.id.servicecardlist);
        linearLayoutServices = view.findViewById(R.id.linearLayoutServices);
        linearLayoutAboutCard = view.findViewById(R.id.linearLayoutAboutCard);
        linearLayoutTravelCard = view.findViewById(R.id.linearLayoutTravelCard);
        linearLayoutProduct = view.findViewById(R.id.linearLayoutProduct);
        tvViewAll = view.findViewById(R.id.tvViewAll);
        btnjoinmember = view.findViewById(R.id.btnjoinmember);
        btShare = view.findViewById(R.id.btshare);
        imgHelp = view.findViewById(R.id.imgHelp);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        loadBanner(view);

        User user = SessionManager.getInstance(getContext()).getUser();
        email = user.getEmail();
        name = user.getUsername();
        phone = user.getNumber();
        userId = user.getId();
        ref_code = user.getRef_code();
        token = user.getToken();


        loadServiceImages();

        onClickButtons();
        loadHomeCategory(view);
        loadServiceBanner(view);




        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void loadBanner(View view) {

        viewPager = view.findViewById(R.id.pager);
        indicator = view.findViewById(R.id.indicator);

        list = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_BANNER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("success").equals("true")){

                                JSONArray jsonArray = obj.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.getString("remark");

                                    int id = jsonObject.getInt("id");

                                    JSONObject object = jsonObject.getJSONObject("image");
                                    String imageUrl = object.getString("image_loc");

                                    String image = AppConfig.BASE_IMAGE_URL+imageUrl;
                                    final BannerData imageModel = new BannerData();


                                    imageModel.setImage_drawable(image);
                                    list.add(imageModel);

                                }

                                viewPager.setAdapter(new BannerPagerAdapter(getActivity(), list));

                                indicator.setViewPager(viewPager);
                                NUM_PAGES = list.size();

                                final Handler handler = new Handler();
                                final Runnable Update = new Runnable() {
                                    public void run() {
                                        if (currentPage == NUM_PAGES) {
                                            currentPage = 0;
                                        }
                                        viewPager.setCurrentItem(currentPage++, true);
                                    }
                                };
                                Timer swipeTimer = new Timer();
                                swipeTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.post(Update);
                                    }
                                }, 3000, 3000);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);



    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void loadServiceImages(){
        servicerecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        list1 = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SERVICES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("success").equals("true")){

                                JSONArray jsonArray = obj.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.getString("remark");

                                    int id = jsonObject.getInt("id");

                                    JSONObject object = jsonObject.getJSONObject("image");
                                    String imageUrl = object.getString("image_loc");

                                    String image = AppConfig.BASE_IMAGE_URL+imageUrl;

                                    list1.add(new BannerData(image, id));

                                }

                                servicerecyclerView.setAdapter(new ServiceCardAdapter(getActivity(), list1));
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
                   //     Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void onClickButtons(){

        tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new CategoryFragment();
                FragmentTransaction transaction = ((AppCompatActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, newFragment);
                transaction.commit();
            }
        });


        btnjoinmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment newFragment = new MemberFragment();
                FragmentTransaction transaction = ((AppCompatActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, newFragment);
                transaction.commit();

            }
        });

        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Join me on THE WADA, A Discount App for your locals Shop, Hospital, Gym and much more. Enter my code "+ ref_code + " to earn 20 points and 50 Points on your Premium register and win paytm cash."+ "\n" +  "https://play.google.com/store/apps/details?id=wada.programmics.thewada");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });
        linearLayoutServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                DialogFragment dialog = ServicesDialogFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("title", "services");
                bundle.putString("webUrl", "http://www.thewada.com/health_new/discountcard_services.php");
                dialog.setArguments(bundle);
                dialog.show(fm,"tag");
            }
        });

        linearLayoutAboutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                DialogFragment dialog = ServicesDialogFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("title", "about card");
                bundle.putString("webUrl", "http://www.thewada.com/health_new/about_card.php");

                dialog.setArguments(bundle);
                dialog.show(fm,"tag");
            }
        });

        linearLayoutTravelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                DialogFragment dialog = ServicesDialogFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("title", "travel card");
                bundle.putString("webUrl", "http://www.thewada.com/health_new/travel_card.php");

                dialog.setArguments(bundle);
                dialog.show(fm,"tag");
            }
        });

        linearLayoutProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                androidx.fragment.app.DialogFragment dialog1 = ServicesDialogFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("title", "discount");
                bundle.putString("webUrl", "http://www.thewada.com/health_new/discountcard.php");
                dialog1.setArguments(bundle);
                dialog1.show(fm,"tag");
            }
        });

    }

    private void loadHomeCategory(View view){
        final List<MainCategoryData> list = new ArrayList<>();
        recyclerViewHomeCategory = view.findViewById(R.id.recyclerViewHomeCategory);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL);
        recyclerViewHomeCategory.setLayoutManager(staggeredGridLayoutManager);
        final HomeCategoryAdapter homeCategoryAdapter = new HomeCategoryAdapter(getActivity(), list);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("success").equals("true")){

                                JSONArray jsonArray = obj.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String name = jsonObject.getString("name");
                                    JSONObject object = jsonObject.getJSONObject("app_icon");
                                    String imageUrl = object.getString("image_loc");

                                    list.add(new MainCategoryData(name, id, imageUrl));

                                }
                                recyclerViewHomeCategory.setAdapter(homeCategoryAdapter);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Authorization", "Bearer " + token);
                return headerMap;
            }

        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void loadServiceBanner(View view) {

        viewPagerService = view.findViewById(R.id.viewPagerService);
        indicatorService = view.findViewById(R.id.indicatorService);


        list2 = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_BOTTOM_BANNER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);

                        try {

                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("success").equals("true")){

                                JSONArray jsonArray = obj.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.getString("remark");

                                    int id = jsonObject.getInt("id");

                                    JSONObject object = jsonObject.getJSONObject("image");
                                    String imageUrl = object.getString("image_loc");

                                    String image = AppConfig.BASE_IMAGE_URL+imageUrl;
                                    final BannerData imageModel = new BannerData();


                                    imageModel.setImage_drawable(image);
                                    list2.add(imageModel);

                                }

                                viewPagerService.setAdapter(new BannerPagerAdapter(getActivity(), list2));

                                indicatorService.setViewPager(viewPagerService);
                                NUM_PAGES1 = list2.size();

                                final Handler handler = new Handler();
                                final Runnable Update = new Runnable() {
                                    public void run() {
                                        if (curentPage1 == NUM_PAGES1) {
                                            curentPage1 = 0;
                                        }
                                        viewPagerService.setCurrentItem(curentPage1++, true);
                                    }
                                };
                                Timer swipeTimer = new Timer();
                                swipeTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.post(Update);
                                    }
                                }, 3000, 3000);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);


    }

}