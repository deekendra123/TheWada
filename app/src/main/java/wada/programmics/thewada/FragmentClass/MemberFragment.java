package wada.programmics.thewada.FragmentClass;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.util.HashMap;
import java.util.Map;

import wada.programmics.thewada.ActivityClass.SubscriptionFormActivity;
import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.Config.CheckInternet;
import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;

public class MemberFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btMember;
    private RelativeLayout relativelayoutPremium, relativelayoutMember;
    private TextView tvWadaId;
    private String name, email, phone, token;
    private int userId;
    private ProgressBar progressBar;



    private OnFragmentInteractionListener mListener;

    public MemberFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MemberFragment newInstance(String param1, String param2) {
        MemberFragment fragment = new MemberFragment();
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
        View view =inflater.inflate(R.layout.fragment_member, container, false);
        btMember = view.findViewById(R.id.btMember);
        relativelayoutPremium = view.findViewById(R.id.relativelayoutPremium);
        relativelayoutMember = view.findViewById(R.id.relativelayoutMember);
        tvWadaId = view.findViewById(R.id.tvWadaId);
        progressBar = view.findViewById(R.id.progressBar);


        User user = SessionManager.getInstance(getContext()).getUser();
        email = user.getEmail();
        name = user.getUsername();
        phone = user.getNumber();
        userId = user.getId();
        token = user.getToken();

        return view;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onResume() {
        super.onResume();

        CheckInternet checkInternet = new CheckInternet(getActivity());

        if (checkInternet.isOnline()==true){
            fetchMemberShip();
        }
        else {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Alert")
                    .setCancelable(false)
                    .setMessage("No Network. Please check your internet connection")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            fetchMemberShip();
                        }
                    }).create().show();

        }

    }

    private void fetchMemberShip(){

        progressBar.setVisibility(View.VISIBLE);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_FETCH_MEMBERSHIP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("dkresp1", ""+response);

                        try {

                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("success").equals("true")){

                                progressBar.setVisibility(View.GONE);
                                JSONArray jsonArray = obj.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String wada_id = jsonObject.getString("wada_id");
                                    Log.e("wada_id", "" + wada_id);
                                    relativelayoutMember.setVisibility(View.INVISIBLE);
                                    relativelayoutPremium.setVisibility(View.VISIBLE);
                                    tvWadaId.setText(""+wada_id);

                                }

                            }
                            else
                            {
                                progressBar.setVisibility(View.GONE);
                                relativelayoutMember.setVisibility(View.VISIBLE);
                                relativelayoutPremium.setVisibility(View.INVISIBLE);

                                Animation animation = AnimationUtils.loadAnimation(getActivity(),
                                        R.anim.play_anim);
                                btMember.startAnimation(animation);

                                btMember.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        v.clearAnimation();
                                        startActivity(new Intent(getActivity(), SubscriptionFormActivity.class));

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
                        Log.e("errro", ""+error.getMessage());

                    }
                }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
              //  headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + token);
                return headerMap;
            }

//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                HashMap<String,String> parms = new HashMap<>();
//               parms.put("user_id", String.valueOf(userId));
//                return parms;
//            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        requestQueue.add(stringRequest);

    }



}
