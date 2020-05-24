package wada.programmics.thewada.ActivityClass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.internal.Util;
import wada.programmics.thewada.AdapterClass.NotificationAdapter;
import wada.programmics.thewada.DialogFragment.NotificationDialogFragment;
import wada.programmics.thewada.ObjectClass.NotificationData;
import wada.programmics.thewada.R;
import wada.programmics.thewada.SqLiteDatabase.DBHelper;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerViewNotification;
    private List<NotificationData> list;
    private NotificationAdapter adapter;
    private DBHelper dbHelper;
    private TextView tvNotification;
    private String title,msg,data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        handleNotification();

        ImageButton closeDialog = findViewById(R.id.fullscreen_dialog_close);
        tvNotification = findViewById(R.id.tvNotification);

        recyclerViewNotification = findViewById(R.id.recyclerViewNotification);
        recyclerViewNotification.addItemDecoration(new DividerItemDecoration(recyclerViewNotification.getContext(), DividerItemDecoration.VERTICAL));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewNotification.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        getNotification();

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (data.equals("yes")){

                    SharedPreferences sharedPreferences = getSharedPreferences("backstatus",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                else {
                    onBackPressed();

                }


            }
        });

    }

    private void getNotification(){
        dbHelper = new DBHelper(this);
        ArrayList<NotificationData> array_list = dbHelper.getAllNotification();

        if (array_list.size()>0) {
            tvNotification.setVisibility(View.GONE);
            recyclerViewNotification.setVisibility(View.VISIBLE);

            for (int i = array_list.size()-1; i >= 0; i--) {
                NotificationData notificationData = array_list.get(i);
                list.add(new NotificationData(notificationData.getId(), notificationData.getTitle(), notificationData.getMessage(), notificationData.getDate()));
            }
            adapter = new NotificationAdapter(this, list);

            recyclerViewNotification.setAdapter(adapter);

        }else {
            tvNotification.setVisibility(View.VISIBLE);
            recyclerViewNotification.setVisibility(View.GONE);
        }
    }

    private void handleNotification(){
        final SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        title = sharedPreferences.getString("title","");
        msg = sharedPreferences.getString("message","");

        SharedPreferences sharedPreferences1 = getSharedPreferences("backstatus",MODE_PRIVATE);
        data = sharedPreferences1.getString("back","");

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM");
        String currentDate = df.format(c);

        DBHelper mydb = new DBHelper(this);

       // mydb.deleteAllData();


        if (!msg.equals("") && !title.equals("")){

            if(mydb.insertNotification(title, msg, currentDate)){
                Log.e("status","done");
            } else{
                Log.e("status","not done");

            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (data.equals("yes")){

            SharedPreferences sharedPreferences = getSharedPreferences("backstatus",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
        }

    }

}
