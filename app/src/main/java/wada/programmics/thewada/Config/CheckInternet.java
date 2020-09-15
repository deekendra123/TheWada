package wada.programmics.thewada.Config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckInternet {
    Context mctx;

    public CheckInternet(Context mctx) {
        this.mctx = mctx;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)mctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void checkConnection(){
        if(isOnline()==false){
            Toast.makeText(mctx, "No Network. Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
