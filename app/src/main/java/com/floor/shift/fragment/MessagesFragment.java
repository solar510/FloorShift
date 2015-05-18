package com.floor.shift.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.floor.shift.MainAct;
import com.floor.shift.R;
import com.floor.shift.adapter.MessageAdapter;
import com.floor.shift.entity.FloorMap;
import com.floor.shift.entity.Messages;
import com.floor.shift.net.NetLoadingDailog;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.floor.shift.utils.WakeLocker;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.floor.shift.utils.CommonMethods.DISPLAY_MESSAGE_ACTION;
import static com.floor.shift.utils.CommonMethods.EXTRA_MESSAGE;

@SuppressLint({ "NewApi", "ValidFragment" })
public class MessagesFragment extends Fragment {
    private CountDownTimer countDownTimer;
    private final long startTime = 3600000;
    private static final long interval = 30000;

    private boolean timerHasStarted = false;
    /*header view*/
    private MainAct mAct;
    private MessageAdapter adapterMessage;
    private ListView listMessages;
    private NetLoadingDailog loadingDlg;
    private ArrayList<Messages> mMessagesList = new ArrayList<Messages>();
    private String mErrorMessage;

    public static MessagesFragment newInstance(MainAct mainAct){
        MessagesFragment messageFrag = new MessagesFragment();
        messageFrag.mAct = mainAct;
        messageFrag.loadingDlg = new NetLoadingDailog(mainAct);
        return messageFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        CommonMethods.setupUI(getActivity(), view.findViewById(R.id.lay_root));
        listMessages = (ListView) view.findViewById(R.id.list_messages);
        listMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Messages selectMessages = mMessagesList.get(position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(countDownTimer == null)
            countDownTimer = new MyTimer(startTime, interval);
        countDownTimer.start();

        getMessages();
        getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
    }

    private void getMessages(){
        loadingDlg.loading();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if(loadingDlg.isShowing())
                    loadingDlg.dismissDialog();
                if(e == null){
                    ArrayList<Messages> messages = new ArrayList<Messages>();
                    for(int i = parseObjects.size() - 1 ; i > -1 ; i--){
                        ParseObject item = parseObjects.get(i);
                        Messages message = new Messages();
                        if(item.has("objectId"))
                            message.setId(item.getString("objectId"));
                        if(item.has("from_loc"))
                            message.setFrom_loc(item.getParseGeoPoint("from_loc"));
                        if(item.has("to_loc"))
                            message.setTo_loc(item.getString("to_loc"));
                        if(item.has("message"))
                            message.setMessage(item.getString("message"));
                        if(item.has("user"))
                            message.setUser_id(item.getString("user"));
                        if(item.has("type"))
                            message.setType(item.getBoolean("type"));

                        message.setTime(item.getUpdatedAt());
                        messages.add(message);
                    }

                    if(messages.size() != 0) {
                        mMessagesList.clear();
                        for(Messages item: messages)
                            mMessagesList.add(item);

                        sortMessages(mMessagesList);
                        updateListView(mMessagesList);
                    }
                }else{
                    CommonMethods.showMessage(getActivity(), e.getMessage());
                }
            }
        });

    }

    private void updateListView(ArrayList<Messages> lists){
        adapterMessage = new MessageAdapter(getActivity(), R.layout.item_message, lists);
        listMessages.setAdapter(adapterMessage);
        adapterMessage.notifyDataSetChanged();
    }

    /**
     * Receiving messages
     * */
    public final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WakeLocker.acquire(context);
            WakeLocker.release();
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);

            if (newMessage.equalsIgnoreCase("UPDATE_MESSAGE") && AppConstants.CURRENT_FRAGMENT == 6){
                getMessages();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if(countDownTimer != null)
            countDownTimer.cancel();

        if (mHandleMessageReceiver != null)
            getActivity().unregisterReceiver(mHandleMessageReceiver);
    }

    @Override
    public void onDestroy() {
        try {
            if (mHandleMessageReceiver != null)
                getActivity().unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }

    private ArrayList<Messages> results;
    ArrayList<Messages> publicItems;
    private void sortMessages(ArrayList<Messages> messages){
        results = new ArrayList<Messages>();
        publicItems = new ArrayList<Messages>();

        ArrayList<Messages> privateItems = new ArrayList<Messages>();
        for(Messages item : messages){
            if(item.isType())
                privateItems.add(item);
            else
                publicItems.add(item);
        }

        for(int i = 0; i < privateItems.size(); i++){
            Messages privateItem = privateItems.get(i);
            results.add(privateItem);

            checkPublicFloorMaps(publicItems, privateItem);
        }
        mMessagesList.clear();
        for(Messages item : results)
            mMessagesList.add(item);
    }

    private void checkPublicFloorMaps(ArrayList<Messages> messages, Messages privateItem){
        ArrayList<Messages> items = new ArrayList<Messages>();
        for(Messages item : messages){
            items.add(item);
        }
        for(Messages publicItem : items){
            if(publicItem.getTo_loc() != null && privateItem.getTo_loc() != null){
                if(privateItem.getTo_loc().equals(publicItem.getTo_loc())){
                    results.add(publicItem);
                    publicItems.remove(publicItem);
                }
            }
        }
    }

    public class MyTimer extends CountDownTimer{
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(adapterMessage != null)
                adapterMessage.notifyDataSetChanged();
        }
    }

}
