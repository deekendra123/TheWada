package wada.programmics.thewada.ActivityClass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import wada.programmics.thewada.Config.AppConfig;

import wada.programmics.thewada.Config.CheckInternet;
import wada.programmics.thewada.DialogFragment.OtpVerificationForgetPasswrodBottomSheet;

import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;

public class LoginActivity extends AppCompatActivity {

    private Button btLogin;
    private LinearLayout llRegister;
    private EditText etNumber, etPassword;

    private TextView tvForgotPassword;
    public static LoginActivity loginActiviy;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btLogin = findViewById(R.id.btLogin);
        llRegister = findViewById(R.id.llRegister);
        etNumber = findViewById(R.id.etNumber);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        progressBar = findViewById(R.id.progressBar);

        loginActiviy =this;

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


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etNumber.getText().toString().isEmpty()) {
                    etNumber.setError("Number is required");
                    etNumber.requestFocus();
                    return;
                }
                else if (etPassword.getText().toString().isEmpty()) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }

                else {

                    CheckInternet checkInternet = new CheckInternet(getApplicationContext());

                    if (checkInternet.isOnline()==true){
                        userLogin();
                    }
                    else {

                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Alert")
                                .setCancelable(false)
                                .setMessage("No Network. Please check your internet connection")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        userLogin();
                                    }
                                }).create().show();

                    }

                }

            }
        });

        llRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PhoneVerificationActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
    }

    private void userLogin(){

        final String phone = etNumber.getText().toString();
        final String password = etPassword.getText().toString();

        progressBar.setVisibility(View.VISIBLE);



        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    Log.e("res", response);
                    JSONObject obj = new JSONObject(response);

                    boolean staus = obj.getBoolean("success");

                    if (staus){

                        JSONObject data = obj.getJSONObject("data");

                        String status = data.getString("do");
                        int user_id1 = data.getInt("user_id");

                        String token = data.getString("token");


                        if (status.equals("nothing")) {

                            JSONArray jsonArray = data.getJSONArray("user");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String name = jsonObject.getString("name");
                                String number = jsonObject.getString("mobile");

                                JSONObject object = jsonObject.getJSONObject("profile");
                                int user_id = object.getInt("user_id");

                                String ref_code = object.getString("refer_code");

                                Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                               progressBar.setVisibility(View.GONE);

                                User user = new User(
                                        user_id, name, number, ref_code, token
                                );

                                SessionManager.getInstance(getApplicationContext()).userLogin(user);
                                finish();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                            }
                        }

                           if (status.equals("verfiy")){

                                Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                               progressBar.setVisibility(View.GONE);

                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                    }
                    if (!staus){
                        Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();

                parms.put("mobile",  phone);
                parms.put("password", password);

                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        LoginActivity.super.onBackPressed();
                    }
                }).create().show();

    }

    public void showForgotPasswordDialog() {

        View alertLayout = LayoutInflater.from(LoginActivity.this).inflate(R.layout.forgot_password_dialog, null);
        final Button btForgotPassword = alertLayout.findViewById(R.id.btForgotPassword);
        final EditText etNumber = alertLayout.findViewById(R.id.etNumber);

        final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this, R.style.CustomDialogTheme);

        alert.setView(alertLayout);

        final AlertDialog dialog = alert.create();
        dialog.show();

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

                }
                else
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

        btForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String number = etNumber.getText().toString();

                if (etNumber.getText().toString().isEmpty()) {
                    etNumber.setError("Number is required");
                    etNumber.requestFocus();
                    return;
                }

                else if (etNumber.getText().toString().length()>0 && etNumber.getText().toString().length()<=12 ) {

                    etNumber.setError("Enter valid number");
                    etNumber.requestFocus();
                    return;

                }

                else {
                    CheckInternet checkInternet = new CheckInternet(getApplicationContext());

                    if (checkInternet.isOnline()==true){
                        dialog.dismiss();

                        sendOtp(number);
                    }
                    else {

                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Alert")
                                .setCancelable(false)
                                .setMessage("No Network. Please check your internet connection")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        dialog.dismiss();

                                        sendOtp(number);
                                    }
                                }).create().show();

                    }

                }

            }
        });
    }


    private void sendOtp(final String number){

        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_FORGOT_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    if (obj.getString("success").equals("true")) {

                        progressBar.setVisibility(View.GONE);

                        JSONObject jsonObject = obj.getJSONObject("data");
                        Toast.makeText(LoginActivity.this, ""+jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();

                        int user_id = jsonObject.getInt("user_id");

                        OtpVerificationForgetPasswrodBottomSheet otpVerificationBottomSheet = new OtpVerificationForgetPasswrodBottomSheet();
                        otpVerificationBottomSheet.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

                        Bundle bundle = new Bundle();
                        bundle.putInt("user_id", user_id);
                        bundle.putString("mobile", number);
                        otpVerificationBottomSheet.setArguments(bundle);
                        otpVerificationBottomSheet.show(getSupportFragmentManager(), "theWada");


                    }
                    else {
                        Toast.makeText(getApplicationContext(), ""+obj.getString("msg"),Toast.LENGTH_SHORT).show();

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

                parms.put("mobile", number);
                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }




}
