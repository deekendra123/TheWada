package wada.programmics.thewada.FragmentClass;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import wada.programmics.thewada.AdapterClass.PointSlabsAdapter;

import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.Config.CheckInternet;
import wada.programmics.thewada.ObjectClass.PointsData;
import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String name, email, phone, referCode,token;
    private int userId;

    private OnFragmentInteractionListener mListener;
    private TextView userName, userNumber, tvReferCode, tvInvite,tvCurrentPoints;
    private RecyclerView recyclerViewPoints;
    private ImageView imgCopy;
    private int points;

    private PointSlabsAdapter pointSlabsAdapter;
    private List<PointsData> list;
    private int[] image = {R.drawable.image1, R.drawable.image2, R.drawable.image3};
    private ProgressBar progressBar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userName = view.findViewById(R.id.userName);
        userNumber = view.findViewById(R.id.userNumber);
        tvReferCode = view.findViewById(R.id.tvReferCode);
        recyclerViewPoints = view.findViewById(R.id.recyclerViewPoints);
        tvCurrentPoints = view.findViewById(R.id.tvCurrentPoints);
        imgCopy = view.findViewById(R.id.imgCopy);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        tvInvite = view.findViewById(R.id.tvInvite);

        User user = SessionManager.getInstance(getContext()).getUser();
        email = user.getEmail();
        name = user.getUsername();
        phone = user.getNumber();
        userId = user.getId();
        token = user.getToken();
        referCode = user.getRef_code();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerViewPoints.setLayoutManager(layoutManager);
        list = new ArrayList<>();

        userName.setText(name);
        userNumber.setText(phone);
        tvReferCode.setText(""+referCode);

        try {

            CheckInternet checkInternet = new CheckInternet(getActivity());

            if (checkInternet.isOnline()==true){
                getPoints(userId,token);
            }
            else {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Alert")
                        .setCancelable(false)
                        .setMessage("No Network. Please check your internet connection")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                getPoints(userId,token);
                            }
                        }).create().show();

            }


        }catch (Exception e){
            e.printStackTrace();
        }


        tvInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Join me on THE WADA, A Discount App for your locals Shop, Hospital, Gym and much more. Enter my code "+ referCode + " to earn 20 points and 50 Points on your Premium register and win paytm cash."+ "\n" +  "https://play.google.com/store/apps/details?id=wada.programmics.thewada");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });

        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", referCode);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), "Text Copied",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    private void getPointSlabs(final int userid, final int points, final String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_POINT_SLABS,
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
                                    int points = jsonObject.getInt("points");

                                    Log.e("dk", ""+points);
                                    list.add(new PointsData(points, id, image[i]));
                                }

                                pointSlabsAdapter = new PointSlabsAdapter(getActivity(), list, userid, points,token, tvCurrentPoints,progressBar);
                                recyclerViewPoints.setAdapter(pointSlabsAdapter);
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
                       // Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Authorization", "Bearer " + token);
                return headerMap;
            }

        };

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


    public void getPoints(final int userid, final String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_POINTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respo",response+"");

                        try {

                            JSONObject obj = new JSONObject(response);
                            points = obj.getInt("data");
//                           points = 300;
                            Log.e("respo",points+"");
                            tvCurrentPoints.setText(""+points);
                            getPointSlabs(userId, points, token);

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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

}
