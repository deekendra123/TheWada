package wada.programmics.thewada.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import wada.programmics.thewada.ActivityClass.ForgotPasswordActivity;
import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.R;

public class OtpVerificationForgetPasswrodBottomSheet extends BottomSheetDialogFragment {

    Button btVerify;
    private int userId;
    private String phone;
    private TextView tvNumber, tvResendOtp, textview;
    private PinEntryEditText etOtp;
    private ProgressBar progressBar;

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.otp_verification_dialog, container, false);

        btVerify = view.findViewById(R.id.btVerify);
        tvNumber = view.findViewById(R.id.tvNumber);
        etOtp = view.findViewById(R.id.etOtp);
        tvResendOtp = view.findViewById(R.id.tvResendOtp);
        textview = view.findViewById(R.id.textview);
        progressBar = view.findViewById(R.id.progressBar);

        String number = getArguments().getString("mobile");
        tvNumber.setText("A OTP has been sent to "+ number);

        final int user_id = getArguments().getInt("user_id");

        countDownTimer();


        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                verifyNumber(user_id);
            }
        });

        return view;
    }



    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.otp_verification_dialog,null,false);
        dialog.setContentView(rootView);
        FrameLayout bottomSheet = (FrameLayout) dialog.getWindow().findViewById(R.id.design_bottom_sheet);
        bottomSheet.setBackgroundResource(R.drawable.bglinear);


        super.setupDialog(dialog, style);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        System.gc();
    }



    private void verifyNumber(final int userId){

        final String otp = etOtp.getText().toString();
        progressBar.setVisibility(View.VISIBLE);



        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_PHONE_VERIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {


                    JSONObject obj = new JSONObject(response);

                    if (obj.getString("success").equals("true")) {
                        progressBar.setVisibility(View.GONE);

                        dismiss();
                        Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Oops! You have entered wrong otp", Toast.LENGTH_SHORT).show();

                        Log.e("eror", error.getMessage()+ "");

                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();

                parms.put("user_id", String.valueOf(userId));
                parms.put("otp", otp);
                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }


    private void resendOtp(final int userId){

        final String otp = etOtp.getText().toString();
        progressBar.setVisibility(View.VISIBLE);



        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_RESEND_OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {


                    JSONObject obj = new JSONObject(response);


                    if (obj.getString("success").equals("true")) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(getActivity(), ""+ obj.getString("message"), Toast.LENGTH_SHORT).show();
                     }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);


                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();
                parms.put("user_id", String.valueOf(userId));
                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }


    private void countDownTimer(){
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                textview.setText("Sending Otp in : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                tvResendOtp.setVisibility(View.VISIBLE);
                textview.setText("Didn't get otp ?");
                tvResendOtp.setText("Resend OTP");

                tvResendOtp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendOtp(userId);
                    }
                });

            }

        }.start();
    }


}

