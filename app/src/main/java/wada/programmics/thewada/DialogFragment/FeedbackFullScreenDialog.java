package wada.programmics.thewada.DialogFragment;


import android.content.DialogInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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
import java.util.regex.Pattern;

import wada.programmics.thewada.Config.AppConfig;
import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;

public class FeedbackFullScreenDialog extends androidx.fragment.app.DialogFragment {
    private Callback callback;

    private ProgressBar progressBar;

    private String name, email, phone;
    private int userId;
    public static FeedbackFullScreenDialog newInstance() {
        return new FeedbackFullScreenDialog();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_dialog, container, false);
        ImageView btSendFeedback = view.findViewById(R.id.sendfeedback);
        TextView tvname = view.findViewById(R.id.t1);
        TextView tvemail = view.findViewById(R.id.t2);
        TextView tvmsg = view.findViewById(R.id.t3);
        final EditText etName = view.findViewById(R.id.etName);
        final EditText etEmail = view.findViewById(R.id.etEmail);
        final EditText etMsg = view.findViewById(R.id.etDesc);
        final EditText etNumber = view.findViewById(R.id.etNumber);
        final EditText etCity = view.findViewById(R.id.etCity);
        ImageButton closeDialog = view.findViewById(R.id.fullscreen_dialog_close);
        progressBar = view.findViewById(R.id.progressBar);

        User user = SessionManager.getInstance(getActivity()).getUser();
        email = user.getEmail();
        name = user.getUsername();
        phone = user.getNumber();
        userId = user.getId();

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.gc();
                dismiss();
            }
        });

        btSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name, email, messgae, number, city;
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                messgae = etMsg.getText().toString();
                number = etNumber.getText().toString();
                city = etCity.getText().toString();


                if (name.isEmpty()) {

                    etName.setError("Name is required");
                    etName.requestFocus();
                    return;

                }

                else if (email.isEmpty()) {

                    etEmail.setError("Email is required");
                    etEmail.requestFocus();
                    return;

                }

                else if(!isValidEmailId(etEmail.getText().toString().trim())){

                    etEmail.setError("Enter Valid Email");
                    etEmail.requestFocus();
                    return;

                }

                else if (number.isEmpty()) {

                    etNumber.setError("Number is required");
                    etNumber.requestFocus();
                    return;

                }

                else if (etNumber.getText().toString().length()>0 && etNumber.getText().toString().length()<=9 ) {

                    etNumber.setError("Enter valid number");
                    etNumber.requestFocus();
                    return;

                }

                else if (city.isEmpty()) {

                    etCity.setError("City is required");
                    etCity.requestFocus();
                    return;

                }
                else if (messgae.isEmpty()){

                    etMsg.setError("Message is required");
                    etMsg.requestFocus();
                    return;

                }

                else {

                    progressBar.setVisibility(View.VISIBLE);

                    insertFeedback(name,email,number,city,messgae);

                }

            }
        });


        return view;
    }

    private void insertFeedback(final String name, final String email, final String number, final String city, final String message){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_FEEDBACK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    String status = obj.getString("message");

                    if (obj.getString("success").equals("true")) {

                        Toast.makeText(getActivity(), ""+ status,Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                        dismiss();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "You have already joined", Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();

                parms.put("name",  name);
                parms.put("mobile", number);
                parms.put("email", email);
                parms.put("city", city);
                parms.put("message", message);

                return parms;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }
    public interface Callback {

        void onActionClick(String name);

    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        System.gc();
    }

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }


}
