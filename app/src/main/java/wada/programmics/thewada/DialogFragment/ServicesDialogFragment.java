package wada.programmics.thewada.DialogFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import wada.programmics.thewada.R;

public class ServicesDialogFragment extends androidx.fragment.app.DialogFragment {
    private Callback callback;
    private TextView tvContent,tvTitleName;
    private String title, webUrl;
    private ImageView imageIcon;
    private FloatingActionButton floatingWebLinkActionButton;
    private ProgressBar progressBar;


    public static ServicesDialogFragment newInstance() {
        return new ServicesDialogFragment();
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
        View view = inflater.inflate(R.layout.services_dialog, container, false);
        ImageButton closeDialog = view.findViewById(R.id.fullscreen_dialog_close);
        tvContent = view.findViewById(R.id.tvContent);
        imageIcon = view.findViewById(R.id.imageIcon);
        tvTitleName = view.findViewById(R.id.tvTitleName);
        floatingWebLinkActionButton = view.findViewById(R.id.webLink);



        Bundle bundle = getArguments();
        title = bundle.getString("title");
        webUrl = bundle.getString("webUrl");


        if (title.equals("services")){
            Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.service);

            imageIcon.setImageBitmap(bitmap);
            tvTitleName.setText("Services");

            tvContent.setText(getString(R.string.service_card));

        }
        else if (title.equals("about card")){
            Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.aboutcard);

            imageIcon.setImageBitmap(bitmap);
            tvTitleName.setText("About Card");
            tvContent.setText(getString(R.string.about_card));

        }
        else if (title.equals("travel card")){
            Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.travelcard);

            imageIcon.setImageBitmap(bitmap);
            tvTitleName.setText("Travel Card");
            tvContent.setText(getString(R.string.travel_card));

        }

        else if (title.equals("discount")){

            Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.travelcard);

            imageIcon.setImageBitmap(bitmap);

            tvTitleName.setText("Discount Card");

            tvContent.setText(getString(R.string.discount_card));
        }


        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        floatingWebLinkActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(webUrl);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        return view;
    }


    public interface Callback {

        void onActionClick(String name);
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        System.gc();
    }
}
