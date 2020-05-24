package wada.programmics.thewada.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;

import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import wada.programmics.thewada.ActivityClass.HomeActivity;
import wada.programmics.thewada.ActivityClass.LoginActivity;
import wada.programmics.thewada.ActivityClass.RegistrationActivity;
import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.PushNotification.NotificationReceiver;
import wada.programmics.thewada.R;

public class OtpVerificationBottomSheet extends BottomSheetDialogFragment {

    Button btVerify;
    private int userId;
    private String phone, password;
    private TextView tvNumber, tvResendOtp, textview;
    private PinEntryEditText etOtp;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
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

        phone = getArguments().getString("mobile");

        tvNumber.setText("A OTP has been sent to "+ phone);


        countDownTimer();

        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                verifyNumber(phone);
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



    private void verifyNumber(final String phone){

        final String otp = etOtp.getText().toString();
        progressBar.setVisibility(View.VISIBLE);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_OTP_VERIFY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("dkrsp",response);

                try {


                    JSONObject obj = new JSONObject(response);

                    if (obj.getString("success").equals("true")) {

                        //userLogin(pd, phone, password);
                        JSONObject data = obj.getJSONObject("data");

                        String token = data.getString("token");

                        Toast.makeText(getActivity(), ""+ obj.getString("message"), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        dismiss();
                        Intent intent = new Intent(getActivity(),RegistrationActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("token",token);
                        startActivity(intent);

                    }

                    else {
                        Toast.makeText(getActivity(), ""+obj.getString("message"),Toast.LENGTH_SHORT).show();

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
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();
                parms.put("mobile",phone);
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

    private void userLogin(final String phone, final String password){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    if (obj.getString("success").equals("true")){

                        JSONObject data = obj.getJSONObject("data");

                        String token = data.getString("token");

                        JSONArray jsonArray = data.getJSONArray("user");


                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String name = jsonObject.getString("name");

                            String number = jsonObject.getString("mobile");

                            JSONObject object = jsonObject.getJSONObject("profile");
                            int user_id = object.getInt("user_id");
                            String ref_code = object.getString("refer_code");


                            User user = new User(
                                    user_id, name, number, ref_code, token
                            );

                            scheduleNotification(getNotification( "Welcome to the TheWada" ) , 500 ) ;
                            SessionManager.getInstance(getActivity()).userLogin(user);
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                            getActivity().finish();

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();

                parms.put("mobile",  phone);
                parms.put("password", password);
                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }


    private void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( getActivity(), NotificationReceiver. class ) ;
        notificationIntent.putExtra(NotificationReceiver. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(NotificationReceiver. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( getActivity(), 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }
    private Notification getNotification (String content) {

        RemoteViews notificationLayoutExpanded = new RemoteViews(getActivity().getPackageName(), R.layout.registeration_notification1);

        NotificationCompat.Builder builder = new NotificationCompat.Builder( getActivity(), default_notification_channel_id ) ;
        builder.setCustomBigContentView(notificationLayoutExpanded);
        builder.setSmallIcon(R.drawable.applogo ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }


}

