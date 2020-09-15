package wada.programmics.thewada.ActivityClass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                if (SessionManager.getInstance(SplashActivity.this).isLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();

                }
                else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 1000);


    }
}
