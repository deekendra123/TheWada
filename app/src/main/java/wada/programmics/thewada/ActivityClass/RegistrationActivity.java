package wada.programmics.thewada.ActivityClass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;

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
import java.util.Map;

import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.Config.CheckInternet;
import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.PushNotification.NotificationReceiver;
import wada.programmics.thewada.R;


public class RegistrationActivity extends AppCompatActivity {

    private Button btRegistration;
    private EditText etName,etConfirmPassword, etPassword, etNumber, etRef;
    private String phone,token;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        btRegistration = findViewById(R.id.btRegistration);
        etName = findViewById(R.id.etName);
        etNumber = findViewById(R.id.etNumber);
        etPassword = findViewById(R.id.etPassword);
        etRef = findViewById(R.id.etRef);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        progressBar = findViewById(R.id.progressBar);

        phone= getIntent().getStringExtra("phone");
        token = getIntent().getStringExtra("token");
        etNumber.setText(""+phone);
        etNumber.setEnabled(false);

        TextWatcher number_Watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text_value = etNumber.getText().toString().trim();
                if(text_value.equalsIgnoreCase("+91"))
                {
                    etNumber.setText("");
                }else
                {
                    if(!text_value.startsWith("+91") && text_value.length()>0) {
                        etNumber.setText("+91" + s.toString());
                        Selection.setSelection(etNumber.getText(), etNumber.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        etNumber.addTextChangedListener(number_Watcher);

        btRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etName.getText().toString().isEmpty()) {
                    etName.setError("Name is required");
                    etName.requestFocus();
                    return;
                }
                else if (etNumber.getText().toString().isEmpty()) {
                    etNumber.setError("Number is required");
                    etNumber.requestFocus();
                    return;
                }
                else if (etNumber.getText().toString().length()>0 && etNumber.getText().toString().length()<=12 ) {

                    etNumber.setError("Enter valid number");
                    etNumber.requestFocus();
                    return;

                }

                else if (etPassword.getText().toString().isEmpty()) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }

                else if (etConfirmPassword.getText().toString().isEmpty()) {
                    etConfirmPassword.setError("Confirm Password is required");
                    etConfirmPassword.requestFocus();
                    return;
                }

                else if (!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())) {
                    etConfirmPassword.setError("Password do not match");
                    etConfirmPassword.requestFocus();
                    return;
                }

                else {

                    CheckInternet checkInternet = new CheckInternet(getApplicationContext());

                    if (checkInternet.isOnline()==true){
                        userRegistration();
                    }
                    else {

                        new AlertDialog.Builder(RegistrationActivity.this)
                                .setTitle("Alert")
                                .setCancelable(false)
                                .setMessage("No Network. Please check your internet connection")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        userRegistration();
                                    }
                                }).create().show();

                    }
                }
            }
        });
    }

    private void userRegistration(){

       final String name = etName.getText().toString();
        final String password = etPassword.getText().toString();
        final String number = etNumber.getText().toString();
        final String referralcode = etRef.getText().toString();
        final String confirmPassword = etConfirmPassword.getText().toString();

        progressBar.setVisibility(View.VISIBLE);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTRATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("regResponse",response);

                try {
                    JSONObject obj = new JSONObject(response);

                    boolean staus = obj.getBoolean("success");

                    if (staus){

                        JSONObject data = obj.getJSONObject("data");

                        String status = data.getString("do");

                        String token = data.getString("token");

                        if (status.equals("nothing")) {

                            JSONObject jsonObject = data.getJSONObject("user");

                                String name = jsonObject.getString("name");
                                String number = jsonObject.getString("mobile");

                                JSONObject object = jsonObject.getJSONObject("profile");
                                int user_id = object.getInt("user_id");

                                String ref_code = object.getString("refer_code");

                            Toast.makeText(RegistrationActivity.this, ""+ obj.getString("message"), Toast.LENGTH_SHORT).show();

                            User user = new User(
                                    user_id, name, number, ref_code,token
                            );

                            scheduleNotification(getNotification( "Welcome to the TheWada" ) , 500 ) ;
                            progressBar.setVisibility(View.GONE);
                            SessionManager.getInstance(getApplicationContext()).userLogin(user);
                            LoginActivity.loginActiviy.finish();
                            PhoneVerificationActivity.phoneVerificationActivity.finish();
                            finish();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                        }
                        else {
                            Toast.makeText(RegistrationActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }
                    if (!staus){
                        Toast.makeText(RegistrationActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                    }
                }){

            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headerMap = new HashMap<String, String>();
                //   headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + token);
                return headerMap;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();

                parms.put("name",  name);
                parms.put("mobile", number);
                parms.put("password", password);
                parms.put("confirm_password", confirmPassword);
                parms.put("refer_code", referralcode);
                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( RegistrationActivity.this, NotificationReceiver. class ) ;
        notificationIntent.putExtra(NotificationReceiver. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(NotificationReceiver. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( RegistrationActivity.this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }
    private Notification getNotification (String content) {

        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.registeration_notification1);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(RegistrationActivity.this, default_notification_channel_id ) ;
        builder.setCustomBigContentView(notificationLayoutExpanded);
        builder.setSmallIcon(R.drawable.applogo ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }
}
