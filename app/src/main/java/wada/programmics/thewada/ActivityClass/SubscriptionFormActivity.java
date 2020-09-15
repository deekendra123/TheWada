package wada.programmics.thewada.ActivityClass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.Config.CheckInternet;
import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;

public class SubscriptionFormActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = SubscriptionFormActivity.class.getSimpleName();

    private Button btPayment;
    private EditText etName, etFatherName, etAddressLine, etCardNumber;
    private Spinner spinnerState, spinnerCity;
    private String city_id, state_id, phone, order_id, created_at, description, amount, currency, murchent_name, image_url, token;
    private int userId;
    private CheckBox checkBox;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Checkout.preload(getApplicationContext());
        setContentView(R.layout.activity_subscription_form);
        btPayment = findViewById(R.id.btPayment);
        etName = findViewById(R.id.etName);
        etFatherName = findViewById(R.id.etFatherName);
        spinnerState = findViewById(R.id.spinnerState);
        spinnerCity = findViewById(R.id.spinnerCity);
        etAddressLine = findViewById(R.id.etAddressLine);
        etCardNumber = findViewById(R.id.etCardNumber);
        checkBox= findViewById(R.id.checkBox);

        progressBar = findViewById(R.id.progressBar);

        User user = SessionManager.getInstance(SubscriptionFormActivity.this).getUser();
        phone = user.getNumber();
        userId = user.getId();
        token = user.getToken();

        CheckInternet checkInternet = new CheckInternet(getApplicationContext());

        if (checkInternet.isOnline()==true){
            getState();
        }
        else {
            new AlertDialog.Builder(SubscriptionFormActivity.this)
                    .setTitle("Alert")
                    .setCancelable(false)
                    .setMessage("No Network. Please check your internet connection")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            getState();
                        }
                    }).create().show();
        }



        btPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etName.getText().toString().isEmpty()) {
                    etName.setError("Name is required");
                    etName.requestFocus();
                    return;
                }
                else if (etFatherName.getText().toString().isEmpty()) {
                    etFatherName.setError("Father Name is required");
                    etFatherName.requestFocus();
                    return;
                }

                else if (etAddressLine.getText().toString().isEmpty()) {
                    etAddressLine.setError("Address Line is required");
                    etAddressLine.requestFocus();
                    return;
                }

                else if (etCardNumber.getText().toString().isEmpty()) {
                    etCardNumber.setError("Id Number is required");
                    etCardNumber.requestFocus();
                    return;
                }
                else if (!checkBox.isChecked()){

                    Toast.makeText(SubscriptionFormActivity.this, "Please check the box if you want to proceed", Toast.LENGTH_SHORT).show();
                }

                else {

                    CheckInternet checkInternet = new CheckInternet(getApplicationContext());

                    if (checkInternet.isOnline()==true){
                        insertMemberData();
                    }
                    else {

                        new AlertDialog.Builder(SubscriptionFormActivity.this)
                                .setTitle("Alert")
                                .setCancelable(false)
                                .setMessage("No Network. Please check your internet connection")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        insertMemberData();
                                    }
                                }).create().show();

                    }
                }

            }
        });
    }

    public void startPayment() {
        final Activity activity = this;
        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", murchent_name);
            options.put("description", description);
            options.put("image", image_url);
          //options.put("order_id", "order_9A33XWu170gUtm");
            options.put("currency", currency);
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "test@razorpay.com");
            preFill.put("contact", phone);
            options.put("prefill", preFill);

            co.open(activity, options);


        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                   .show();

            e.printStackTrace();

        }
    }



    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            insertPaymentData(order_id, razorpayPaymentID, "success", created_at);
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);

            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();

        }
    }


    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, " " + response, Toast.LENGTH_SHORT).show();

            Log.e("paymenterror", code + "      "+ response );
            insertPaymentData(order_id, "null", "failed", created_at);


        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


    private void getState() {

        final List<String> categories = new ArrayList<String>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_STATE,
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
                                    String state = jsonObject.getString("name");

                                    categories.add(state);

                                }

                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerState.setAdapter(dataAdapter);

                                spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        state_id = String.valueOf(position+1);
                                        String state = parent.getItemAtPosition(position).toString();
                                        getCity(String.valueOf(position+1));
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

        RequestQueue requestQueue = Volley.newRequestQueue(SubscriptionFormActivity.this);

        requestQueue.add(stringRequest);
    }


    private void getCity(final String state_id) {

        final List<String> categories = new ArrayList<String>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CITY_BY_STATE,
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
                                    String city = jsonObject.getString("name");
                                    categories.add(city);
                                }

                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCity.setAdapter(dataAdapter);

                                spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String city = parent.getItemAtPosition(position).toString();
                                        city_id = String.valueOf(position+1);
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
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> parms = new HashMap<>();
                parms.put("state", state_id);
                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SubscriptionFormActivity.this);

        requestQueue.add(stringRequest);
    }

    private void insertMemberData() {
        progressBar.setVisibility(View.VISIBLE);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_MEMBERSHIP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("dkresp", ""+response);

                        try {

                            JSONObject obj = new JSONObject(response);



                            if (obj.getString("success").equals("true")){

                               progressBar.setVisibility(View.GONE);

                                JSONObject jsonObject = obj.getJSONObject("data");
                                int id = jsonObject.getInt("id");
                                order_id = jsonObject.getString("order_id");
                                String user_id = jsonObject.getString("user_id");
                                created_at = jsonObject.getString("created_at");
                                description = jsonObject.getString("description");
                                amount = jsonObject.getString("amount");
                                currency = jsonObject.getString("currency");
                                murchent_name = jsonObject.getString("name");
                                image_url = jsonObject.getString("image");
                                startPayment();

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
             //   headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + token);
            return headerMap;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final String user_name = etName.getText().toString();
                final String father_name = etFatherName.getText().toString();
                final String full_address = etAddressLine.getText().toString();
                final String card_no = etCardNumber.getText().toString();
                final String userid = String.valueOf(userId);
                final String stateid = state_id;
                final  String cityid = city_id;


                HashMap<String,String> parms = new HashMap<>();

                parms.put("user_id", userid);
                parms.put("name", user_name);
                parms.put("father_name", father_name);
                parms.put("state", stateid);
                parms.put("city", cityid);
                parms.put("address", full_address);
                parms.put("aadhar_pan", card_no);

                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SubscriptionFormActivity.this);
        requestQueue.add(stringRequest);

    }


    private void insertPaymentData(final String order_id, final String payment_id, final String  status, final String created_at){

       progressBar.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_PAYMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response", response);
                        try {

                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("success").equals("true")){

                                progressBar.setVisibility(View.GONE);

                                JSONObject jsonObject = obj.getJSONObject("data");
                                String message = jsonObject.getString("msg");
                                String payment_status = jsonObject.getString("payment_status");

                                if (payment_status.equals("true")){
                                    onBackPressed();
                                }
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

                Log.e("token", token);
                Map<String, String> headerMap = new HashMap<String, String>();
               // headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + token);
                return headerMap;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final String userid = String.valueOf(userId);
                HashMap<String,String> parms = new HashMap<>();
                parms.put("user_id", userid);
                parms.put("order_id", order_id);
                parms.put("payment_id", payment_id);
                parms.put("status", String.valueOf(status));
                parms.put("created_at", created_at);
                return parms;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SubscriptionFormActivity.this);
        requestQueue.add(stringRequest);

    }
}
