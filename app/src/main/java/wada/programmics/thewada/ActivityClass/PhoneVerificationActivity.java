package wada.programmics.thewada.ActivityClass;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.DialogFragment.OtpVerificationBottomSheet;
import wada.programmics.thewada.R;

public class PhoneVerificationActivity extends AppCompatActivity {

    private Button btRegistration;
    private EditText etNumber;
    private ProgressBar progressBar;


    public static PhoneVerificationActivity phoneVerificationActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        btRegistration = findViewById(R.id.btRegistration);
        etNumber = findViewById(R.id.etNumber);
        progressBar = findViewById(R.id.progressBar);


        phoneVerificationActivity = this;
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

                    userRegistration();
                }
            }
        });
    }
    private void userRegistration(){

        progressBar.setVisibility(View.VISIBLE);


        final String number = etNumber.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_PHONE_VERIFY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("rsp",response);

                try {

                    JSONObject obj = new JSONObject(response);


                    if (obj.getString("success").equals("true")) {

                        JSONObject jsonObject = obj.getJSONObject("data");
                        String status = jsonObject.getString("do");

                        if (status.equals("verfiy")){

                            progressBar.setVisibility(View.GONE);
                            OtpVerificationBottomSheet otpVerificationBottomSheet = new OtpVerificationBottomSheet();
                            otpVerificationBottomSheet.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
                            Bundle bundle = new Bundle();
                            bundle.putString("mobile", number);
                            otpVerificationBottomSheet.setArguments(bundle);
                            otpVerificationBottomSheet.show(getSupportFragmentManager(), "theWada");

                        }

                        if (status.equals("nothing")){

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(getApplicationContext(), ""+obj.getString("message"),Toast.LENGTH_SHORT).show();

                        }

                    }

                    else{

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
                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(PhoneVerificationActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
