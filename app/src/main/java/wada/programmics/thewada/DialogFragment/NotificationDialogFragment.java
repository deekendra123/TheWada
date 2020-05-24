package wada.programmics.thewada.DialogFragment;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wada.programmics.thewada.AdapterClass.NotificationAdapter;
import wada.programmics.thewada.ObjectClass.NotificationData;
import wada.programmics.thewada.R;
import wada.programmics.thewada.SqLiteDatabase.DBHelper;

public class NotificationDialogFragment extends androidx.fragment.app.DialogFragment {
    private Callback callback;

    private RecyclerView recyclerViewNotification;
    private List<NotificationData> list;
    private NotificationAdapter adapter;
    private DBHelper dbHelper;
    private TextView tvNotification;

    public static NotificationDialogFragment newInstance() {
        return new NotificationDialogFragment();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_dialog, container, false);
        ImageButton closeDialog = view.findViewById(R.id.fullscreen_dialog_close);
        tvNotification = view.findViewById(R.id.tvNotification);

        recyclerViewNotification = view.findViewById(R.id.recyclerViewNotification);
        recyclerViewNotification.addItemDecoration(new DividerItemDecoration(recyclerViewNotification.getContext(), DividerItemDecoration.VERTICAL));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewNotification.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        getNotification();

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private void getNotification(){
        dbHelper = new DBHelper(getActivity());
        ArrayList<NotificationData> array_list = dbHelper.getAllNotification();

        if (array_list.size()>0) {
            tvNotification.setVisibility(View.GONE);
            recyclerViewNotification.setVisibility(View.VISIBLE);

            for (int i = 0; i < array_list.size(); i++) {
                NotificationData notificationData = array_list.get(i);
                list.add(new NotificationData(notificationData.getId(), notificationData.getTitle(), notificationData.getMessage(),""));
            }
            adapter = new NotificationAdapter(getActivity(), list);

            recyclerViewNotification.setAdapter(adapter);

        }else {
            tvNotification.setVisibility(View.VISIBLE);
            recyclerViewNotification.setVisibility(View.GONE);
        }
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
