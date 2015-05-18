package com.floor.shift;

import static com.floor.shift.utils.CommonMethods.DISPLAY_MESSAGE_ACTION;
import static com.floor.shift.utils.CommonMethods.EXTRA_MESSAGE;
import static com.floor.shift.utils.CommonMethods.displayMessage;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.floor.shift.adapter.NavDrawerListAdapter;
import com.floor.shift.entity.FloorMap;
import com.floor.shift.entity.NavDrawerItem;
import com.floor.shift.fragment.AddCatalogFragment;
import com.floor.shift.fragment.CatalogFragment;
import com.floor.shift.fragment.FloorFragment;
import com.floor.shift.fragment.MessagesFragment;
import com.floor.shift.fragment.PeopleFragment;
import com.floor.shift.fragment.SelfileFragment;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.floor.shift.utils.GPSTracker;
import com.floor.shift.utils.WakeLocker;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

import java.util.ArrayList;
import java.util.List;


public class MainAct extends ActionBarActivity implements View.OnClickListener{
    public final int FRAGEMENT_HOME = 0;
    public final int FRAGEMENT_PEOPLE = 1;
    public final int FRAGEMENT_CATALOG = 2;
    public final int FRAGEMENT_ADD_CATALOG = 3;
    public final int FRAGEMENT_SELFIE = 4;
    public final int FRAGEMENT_FLOOR = 5;
    public final int FRAGEMENT_MESSAGES = 6;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private android.support.v7.app.ActionBar actionBar;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private NavDrawerListAdapter adapter;

    private TextView txtMessages, txtFloor, txtHeader;
    private LinearLayout laySelectMessages, laySelectFloor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpened(getIntent());

        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            AppConstants.curLat = gps.getLatitude();
            AppConstants.curLng = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
            return;
        }

        startService(new Intent(this, GeofenceService.class));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        int screenWidth = CommonMethods.getWindowWidth(this);
        mDrawerList.getLayoutParams().width = ((int) (screenWidth * 0.8));

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        for(int i = 0; i < navMenuTitles.length; i++){
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
        }

        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_bg)));
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        mDrawerToggle = new ActionBarDrawerToggle(MainAct.this,
                mDrawerLayout,
                new android.support.v7.widget.Toolbar(MainAct.this),
               R.string.desc_list_item_icon,
                R.string.media
        ) {
            public void onDrawerClosed(View view) {
                actionBar.setTitle("Action Bar");
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle("Menu Opened");
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mCustomView = inflater.inflate(R.layout.custom_actionbar, null);
        actionBar.setCustomView(mCustomView);

        /*init messages and floor tabs*/
        laySelectMessages = (LinearLayout) mCustomView.findViewById(R.id.lay_selectMessages);
        laySelectFloor = (LinearLayout) mCustomView.findViewById(R.id.lay_selectFloor);
        txtMessages = (TextView) mCustomView.findViewById(R.id.pager_messages);
        txtFloor =(TextView) mCustomView.findViewById(R.id.pager_floor);
        txtMessages.setOnClickListener(this);
        txtFloor.setOnClickListener(this);
        mCustomView.findViewById(R.id.btn_menu).setOnClickListener(this);

        initPager(true);

        txtHeader = (TextView) mCustomView.findViewById(R.id.menu_title);
        /*init title view*/

        getFloorMap();

        displayView(AppConstants.CURRENT_FRAGMENT);
    }

//    public void ChangeFragment(int id) {
//        CURRENT_FRAGMENT = id;
//        android.support.v4.app.FragmentManager fragMgr = getSupportFragmentManager();
//        FragmentTransaction fragTrs = fragMgr.beginTransaction();
//        android.support.v4.app.Fragment fragment;
//        switch(id) {
//            case FRAGEMENT_MESSAGES:
//                fragment = new MessagesFragment(this);
//                fragTrs.replace(R.id.fragment_content, fragment);
//                break;
//            case FRAGEMENT_FLOOR:
//                fragment = new FloorFragment(this);
//                fragTrs.replace(R.id.fragment_content, fragment);
//                break;
//            case FRAGEMENT_PEOPLE:
//                fragment = new PeopleFragment(this);
//                fragTrs.replace(R.id.fragment_content, fragment);
//                break;
//            case FRAGEMENT_CATALOG:
//                fragment = new CatalogFragment(this);
//                fragTrs.replace(R.id.fragment_content, fragment);
//                break;
//            case FRAGEMENT_ADD_CATALOG:
//                fragment = new AddCatalogFragment(this);
//                fragTrs.replace(R.id.fragment_content, fragment);
//                break;
//            case FRAGEMENT_SELFIE:
//                fragment = new SelfileFragment(this);
//                fragTrs.replace(R.id.fragment_content, fragment);
//                break;
//        }
//        fragTrs.addToBackStack(null);
//        fragTrs.commit();
//    }

    private void initPager(boolean flag){
        if(flag){
            laySelectFloor.setVisibility(View.GONE);
            laySelectMessages.setVisibility(View.VISIBLE);
            txtFloor.setTextColor(getResources().getColor(R.color.txt_color));
            txtMessages.setTextColor(getResources().getColor(android.R.color.white));
        }else{
            laySelectFloor.setVisibility(View.VISIBLE);
            laySelectMessages.setVisibility(View.GONE);
            txtFloor.setTextColor(getResources().getColor(android.R.color.white));
            txtMessages.setTextColor(getResources().getColor(R.color.txt_color));
        }
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    private void displayView(int id) {
        Fragment fragment = null;
        switch(id) {
            case FRAGEMENT_MESSAGES:
                fragment = MessagesFragment.newInstance(this);
                break;
            case FRAGEMENT_FLOOR:
                fragment = FloorFragment.newInstance(this);
                break;
//            case FRAGEMENT_PEOPLE:
//                fragment = new PeopleFragment(this);
//                break;
            case FRAGEMENT_CATALOG:
                fragment = CatalogFragment.newInstance(this);
                break;
//            case FRAGEMENT_ADD_CATALOG:
//                fragment = new AddCatalogFragment(this);
//                break;
//            case FRAGEMENT_SELFIE:
//                fragment = new SelfileFragment(this);
//                break;
        }
        if (fragment != null) {
            FragmentManager fragMgr = getSupportFragmentManager();
            FragmentTransaction fragTrs = fragMgr.beginTransaction();
            fragTrs.replace(R.id.frame_container, fragment);
            fragTrs.addToBackStack(null);
            fragTrs.commit();

            if(id < 5){
                // update selected item and title, then close the drawer
                mDrawerList.setItemChecked(id, true);
                mDrawerList.setSelection(id);
                setTitle(navMenuTitles[id]);
                mDrawerLayout.closeDrawer(mDrawerList);
            }

            AppConstants.CURRENT_FRAGMENT = id;
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state
//        mDrawerToggle.syncState();
//    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.pager_messages){
            initPager(true);
            MessagesFragment fragment = MessagesFragment.newInstance(this);
            FragmentManager fragMgr = getSupportFragmentManager();
            FragmentTransaction fragTrs = fragMgr.beginTransaction();
            fragTrs.replace(R.id.frame_container, fragment);
            fragTrs.addToBackStack(null);
            fragTrs.commit();
            AppConstants.CURRENT_FRAGMENT = FRAGEMENT_FLOOR;
        }
        if(v.getId() == R.id.pager_floor){
            initPager(false);
            FloorFragment fragment = FloorFragment.newInstance(this);
            FragmentManager fragMgr = getSupportFragmentManager();
            FragmentTransaction fragTrs = fragMgr.beginTransaction();
            fragTrs.replace(R.id.frame_container, fragment);
            fragTrs.addToBackStack(null);
            fragTrs.commit();
            AppConstants.CURRENT_FRAGMENT = FRAGEMENT_FLOOR;
        }
        if(v.getId() == R.id.btn_menu){
            if(mDrawerLayout.isDrawerOpen(mDrawerList)){
                mDrawerLayout.closeDrawer(mDrawerList);
            }else{
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
    }

    public void setTitle(String title){
        txtHeader.setText(title);
    }

    public TextView getTitleText(){
        return txtHeader;
    }

    private void getFloorMap(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FloorMap");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    ArrayList<FloorMap> floorMaps = new ArrayList<FloorMap>();
                    for(int i = 0; i < parseObjects.size(); i++){
                        ParseObject item = parseObjects.get(i);
                        FloorMap floorMap = new FloorMap();
                        floorMap.setId(item.getObjectId());
                        if(item.has("name"))
                            floorMap.setName(item.getString("name"));
                        if(item.has("corner1"))
                            floorMap.setPos1(item.getParseGeoPoint("corner1"));
                        if(item.has("corner2"))
                            floorMap.setPos2(item.getParseGeoPoint("corner2"));
                        if(item.has("corner3"))
                            floorMap.setPos3(item.getParseGeoPoint("corner3"));
                        if(item.has("corner4"))
                            floorMap.setPos4(item.getParseGeoPoint("corner4"));
//                        if(item.has("image"))
//                            floorMap.setImgUrl(item.getString("image"));
                        floorMaps.add(floorMap);
                    }

                    if(floorMaps.size() != 0) {
                        AppConstants.CurrentFloorMaps.clear();
                        for(FloorMap item: floorMaps)
                            AppConstants.CurrentFloorMaps.add(item);
                    }
                }else{
                    CommonMethods.showMessage(MainAct.this, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
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

            if (newMessage.equalsIgnoreCase("UPDATE_MESSAGE")){
                if(AppConstants.CURRENT_FRAGMENT != FRAGEMENT_MESSAGES){
                    initPager(true);
                    MessagesFragment fragment = MessagesFragment.newInstance(MainAct.this);
                    FragmentManager fragMgr = getSupportFragmentManager();
                    FragmentTransaction fragTrs = fragMgr.beginTransaction();
                    fragTrs.replace(R.id.frame_container, fragment);
                    fragTrs.addToBackStack(null);
                    fragTrs.commit();
                    AppConstants.CURRENT_FRAGMENT = FRAGEMENT_MESSAGES;
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandleMessageReceiver != null)
            unregisterReceiver(mHandleMessageReceiver);
    }

    @Override
    protected void onDestroy() {
        try {
            if (mHandleMessageReceiver != null)
                unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }
}
