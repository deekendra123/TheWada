package wada.programmics.thewada.ActivityClass;

import android.annotation.SuppressLint;

import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.Field;

import wada.programmics.thewada.DialogFragment.FeedbackFullScreenDialog;
import wada.programmics.thewada.FragmentClass.CategoryFragment;

import wada.programmics.thewada.FragmentClass.DetailsFragment;
import wada.programmics.thewada.FragmentClass.HomeFragment;
import wada.programmics.thewada.FragmentClass.MemberFragment;
import wada.programmics.thewada.FragmentClass.ProfileFragment;
import wada.programmics.thewada.ObjectClass.User;
import wada.programmics.thewada.Preference.SessionManager;
import wada.programmics.thewada.R;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Fragment currentFragment = null;
    private ImageView imgSubscription;
    private RelativeLayout layoutSubscription;
    private String email;
    private TextView userName;
    public static HomeActivity homeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        imgSubscription = findViewById(R.id.imgSubscription);
        userName = findViewById(R.id.userName);
        layoutSubscription = findViewById(R.id.layoutSubscription);
        FirebaseMessaging.getInstance().subscribeToTopic("global");
        homeActivity = this;

        init();

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        BottomNavigationView navigation1 = findViewById(R.id.nav_view1);

        BottomNavigationViewHelper.removeShiftMode(navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation1);
        navigation.setItemIconTintList(null);
        navigation1.setItemIconTintList(null);

        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);


                        switch (item.getItemId()) {


                            case R.id.navigation_home:
                                item.setChecked(true);


                                if (!(currentFragment instanceof HomeFragment)) {
                                    HomeFragment homeFragment = new HomeFragment();
                                    loadFragment(homeFragment);
                                }

                                break;


                            case R.id.navigation_category:
                                item.setChecked(true);


                                if (!(currentFragment instanceof CategoryFragment)) {
                                    CategoryFragment categoryFragment = new CategoryFragment();
                                    loadFragment(categoryFragment);
                                }
                                break;

                        }
                        return false;
                    }
                });

        navigation1.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);


                        switch (item.getItemId()) {


                            case R.id.navigation_details:
                                item.setChecked(true);


                                if (!(currentFragment instanceof DetailsFragment)) {
                                    DetailsFragment detailsFragment = new DetailsFragment();
                                    loadFragment(detailsFragment);

                                }

                                break;


                            case R.id.navigation_profile:

                                item.setChecked(true);

                                if (!(currentFragment instanceof ProfileFragment)) {
                                    ProfileFragment profileFragment = new ProfileFragment();
                                    loadFragment(profileFragment);

                                }

                                break;


                        }
                        return false;
                    }
                });


        layoutSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(currentFragment instanceof ProfileFragment)) {
                    MemberFragment memberFragment = new MemberFragment();
                    loadFragment(memberFragment);
                }

            }
        });
        loadFragment(new HomeFragment());
    }


    private boolean loadFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
        return true;

    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences sharedPreferences = getSharedPreferences("backstatus",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        finishAndRemoveTask();
                        HomeActivity.super.onBackPressed();
                    }
                }).create().show();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    public static class BottomNavigationViewHelper {

        @SuppressLint("RestrictedApi")
        public static void removeShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
                    item.setChecked(item.getItemData().isChecked());
                }

            } catch (NoSuchFieldException e) {
                Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
            } catch (IllegalAccessException e) {
                Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
            }
        }
    }

    private void init() {

        DrawerLayout drawer = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        androidx.appcompat.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        View header = navigationView.getHeaderView(0);

        TextView tvprofile = header.findViewById(R.id.userNames);

        ImageView imgHelp = findViewById(R.id.imgHelp);
        RelativeLayout notificationLayout = findViewById(R.id.relativelayout1);

        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
                DialogFragment dialog = FeedbackFullScreenDialog.newInstance();
                dialog.show(fm,"tag");
            }
        });


        final User user = SessionManager.getInstance(this).getUser();
        email = user.getEmail();
        tvprofile.setText(""+user.getUsername());

        Log.e("userId", String.valueOf(user.getId())+ "   "+user.getToken()+ "  " +user.getUsername()+ "   "+  user.getRef_code()+ "  "+user.getNumber());

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("backstatus",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);

            }
        });

        Button btRefer = findViewById(R.id.btRefer);
        btRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Join me on THE WADA, A Discount App for your locals Shop, Hospital, Gym and much more. Enter my code "+ user.getRef_code() + " to earn 20 points and 50 Points on your Premium register and win paytm cash."+ "\n" +  "https://play.google.com/store/apps/details?id=wada.programmics.thewada");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.userNames);

    }

    private void displaySelectedScreen(int itemId) {

        switch (itemId) {
            case R.id.nav_profile:

                Fragment newFragment = new ProfileFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, newFragment);
                transaction.commit();
                break;
            case R.id.about:

                Fragment newFragment1 = new DetailsFragment();
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.replace(R.id.frame_container, newFragment1);
                transaction1.commit();

                break;


            case R.id.contact:

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","info@thewada.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;



            case R.id.joinus:

                FragmentManager fm = getSupportFragmentManager();
                DialogFragment dialog = FeedbackFullScreenDialog.newInstance();
                dialog.show(fm,"tag");
                break;

            case R.id.logout:

                SessionManager.getInstance(getApplicationContext()).logout();
                finish();
                break;


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

}
