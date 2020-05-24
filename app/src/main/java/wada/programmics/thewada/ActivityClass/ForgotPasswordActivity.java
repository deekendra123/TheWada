package wada.programmics.thewada.ActivityClass;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import wada.programmics.thewada.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button btForgotPassword;
    private EditText etPassword, etConfirmPassword;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btForgotPassword = findViewById(R.id.btForgotPassword);

        progressBar = findViewById(R.id.progressBar);

        final int userid = getIntent().getIntExtra("user_id", 0);

        btForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etPassword.getText().toString().isEmpty()) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }

                else if (etConfirmPassword.getText().toString().isEmpty()) {
                    etConfirmPassword.setError("Confirm Password is required");
                    etConfirmPassword.requestFocus();
                    return;
                }

                if (!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())) {
                    etConfirmPassword.setError("Password do not match");
                    etConfirmPassword.requestFocus();
                    return;
                }


                else {

                    userForgetPassword(userid);
                }
            }
        });
    }

    private void userForgetPassword(final int useid){


        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_PASSWORD_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    String status = obj.getString("success");


                    if (obj.getString("success").equals("true")) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(getApplicationContext(), ""+obj.getString("message"),Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), ""+obj.getString("message"),Toast.LENGTH_SHORT).show();

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

                final String password = etPassword.getText().toString();
                final String confirmPassword = etConfirmPassword.getText().toString();


                parms.put("user_id", String.valueOf(useid));
                parms.put("password", password);
                parms.put("confirm_password", confirmPassword);
                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
