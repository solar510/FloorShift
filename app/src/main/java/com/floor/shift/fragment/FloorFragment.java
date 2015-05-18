package com.floor.shift.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floor.shift.FloorShiftApp;
import com.floor.shift.MainAct;
import com.floor.shift.MyPushCustomReceiver;
import com.floor.shift.R;
import com.floor.shift.entity.FloorMap;
import com.floor.shift.net.NetLoadingDailog;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.floor.shift.utils.CommonMethods.displayMessage;

@SuppressLint({ "NewApi", "ValidFragment" })
public class FloorFragment extends Fragment implements View.OnClickListener{
    private MainAct mAct;
    GoogleMap googleMap;

    ArrayList<PolygonOptions> mPolygonOptionses = new ArrayList<PolygonOptions>();

    private String message;
    private NetLoadingDailog loadingDlg;

    private LinearLayout layFloorMap;
    private TextView txtName, txtDesc;
    private FloorMap floorMap;
    private EditText editMessage;
    private LatLng pickLocation;

    public FloorFragment(MainAct mainActivity) {
        mAct = mainActivity;
    }

    public static FloorFragment newInstance(MainAct act){
        FloorFragment frag = new FloorFragment(act);
        frag.mAct = act;
        frag.loadingDlg = new NetLoadingDailog(act);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_floor, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        CommonMethods.setupUI(getActivity(), view.findViewById(R.id.lay_root));

        mAct.getTitleText().setText(getActivity().getResources().getString(R.string.about));
        layFloorMap = (LinearLayout) view.findViewById(R.id.lay_floormap);
        txtName = (TextView) view.findViewById(R.id.txt_name);
        txtDesc = (TextView) view.findViewById(R.id.txt_desc);
        editMessage = (EditText) view.findViewById(R.id.edit_message);
        view.findViewById(R.id.btn_sendmessage).setOnClickListener(this);
        layFloorMap.setVisibility(View.GONE);

        MapsInitializer.initialize(getActivity());
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) )
        {
            case ConnectionResult.SUCCESS:
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                googleMap = mapFragment.getMap();
                googleMap.setMyLocationEnabled(true);

                drawFloorMap();
                break;
            case ConnectionResult.SERVICE_MISSING:
                Log.e("MAP", "SERVICE MISSING");
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.e("MAP", "UPDATE REQUIRED");
                break;
            default:
                Log.e("MAP", "Map State:" + GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()));
        }

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng _latLng) {
                int position = -1;
                for(int i = 0 ;  i < mPolygons.size(); i++){
                    Polygon item = mPolygons.get(i);
                    if(containsInPolygon(_latLng, item)){
                        position = i;
                        break;
                    }
                }

                if(position != -1){
                    floorMap = AppConstants.CurrentFloorMaps.get(position);
                    layFloorMap.setVisibility(View.VISIBLE);
                    txtName.setText(floorMap.getName());
                }else{
                    floorMap = null;
                    layFloorMap.setVisibility(View.GONE);
                }

                pickLocation = _latLng;

                MarkerOptions markerOptions = new MarkerOptions();
                // Setting the position for the marker
                markerOptions.position(_latLng);
                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(_latLng.latitude + " : " + _latLng.longitude);

                // Clears the previously touched position
                googleMap.clear();
                for(PolygonOptions item : mPolygonOptionses)
                    googleMap.addPolygon(item);

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(_latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sendmessage:
                message = editMessage.getText().toString().trim();
                if(message.isEmpty()){
                    CommonMethods.showMessage(getActivity(), "Please type message over here.");
                    return;
                }

                if(!loadingDlg.isShowing())
                    loadingDlg.loading();

                final JSONObject jObj;
                try{
                    jObj = new JSONObject();
                    jObj.put("action", MyPushCustomReceiver.IntentAction);
                    jObj.put("type", true);
                    ParseObject query = new ParseObject("Message");
                    ParseGeoPoint from_loc = new ParseGeoPoint(AppConstants.curLat, AppConstants.curLng);
                    query.put("from_loc", from_loc);
                    if(floorMap == null){
                        message = String.format("%s @%s", message, getString(R.string.app_name));
                    }else{
                        message = String.format("%s @%s", message, floorMap.getName());
                        if(floorMap.getId() != null) {
                            query.put("to_loc", floorMap.getId());
                            jObj.put("to_loc", floorMap.getId());
                        }
                    }
                    query.put("type", true);
                    query.put("message", message);
                    jObj.put("message", message);
                    ParseUser user = ParseUser.getCurrentUser();
                    query.put("user", user.getObjectId());
                    query.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            loadingDlg.dismissDialog();
                            editMessage.setText("");
                            if (e == null) {
                                ParseQuery pushQuery = ParseInstallation.getQuery();
                                pushQuery.whereEqualTo("deviceType", "android");
                                ParsePush push = new ParsePush();
                                push.setData(jObj);
                                push.sendInBackground();
                            } else {
                                CommonMethods.showMessage(getActivity(), e.getMessage());
                            }

                        }
                    });

                }catch (JSONException e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void drawFloorMap(){
        if(AppConstants.CurrentFloorMaps.size() <= 0)
            return;
        if(AppConstants.CurrentFloorMaps.get(0) == null || AppConstants.CurrentFloorMaps.get(0).getPos1() == null)
            return;

        LatLng latLng = new LatLng(AppConstants.CurrentFloorMaps.get(0).getPos1().getLatitude(), AppConstants.CurrentFloorMaps.get(0).getPos1().getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20);
        googleMap.animateCamera(cameraUpdate);


        for(int i = 0; i < AppConstants.CurrentFloorMaps.size(); i++ ){
            FloorMap map = AppConstants.CurrentFloorMaps.get(i);
            if(map == null)
                return;
            if(map.getPos1() == null && map.getPos2() == null && map.getPos3() == null && map.getPos4() == null)
                return;

            LatLng pos1 = new LatLng(map.getPos1().getLatitude(), map.getPos1().getLongitude());
            LatLng pos2 = new LatLng(map.getPos2().getLatitude(), map.getPos2().getLongitude());
            LatLng pos3 = new LatLng(map.getPos3().getLatitude(), map.getPos3().getLongitude());
            LatLng pos4 = new LatLng(map.getPos4().getLatitude(), map.getPos4().getLongitude());

            ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();
            arrayPoints.add(pos1);
            arrayPoints.add(pos2);
            arrayPoints.add(pos4);
            arrayPoints.add(pos3);
            PolygonOptions polygonOptions = new PolygonOptions();
            mPolygonOptionses.add(polygonOptions);
            polygonOptions.addAll(arrayPoints);
            polygonOptions.strokeColor(Color.BLUE);
            polygonOptions.strokeWidth(4);
            int num = (int) Math.floor(Math.random() * 255) + 1;
            int color = Color.argb(210, (int) Math.floor(Math.random() * 255) + 1, (int) Math.floor(Math.random() * 255) + 1, (int) Math.floor(Math.random() * 255) + 1);
            polygonOptions.fillColor(color);
            Polygon polygon = googleMap.addPolygon(polygonOptions);
            mPolygons.add(polygon);

        }

    }
    ArrayList<Polygon> mPolygons = new ArrayList<Polygon>();

    private boolean containsInPolygon(LatLng latLng, Polygon polygon) {

        boolean oddTransitions = false;
        List<LatLng> verticesPolygon = polygon.getPoints();
        float[] polyY, polyX;
        float x = (float) (latLng.latitude);
        float y = (float) (latLng.longitude);

        // Create arrays for vertices coordinates
        polyY = new float[verticesPolygon.size()];
        polyX = new float[verticesPolygon.size()];
        for (int i=0; i<verticesPolygon.size() ; i++) {
            LatLng verticePolygon = verticesPolygon.get(i);
            polyY[i] = (float) (verticePolygon.longitude);
            polyX[i] = (float) (verticePolygon.latitude);
        }
        // Check if a virtual infinite line cross each arc of the polygon
        for (int i = 0, j = verticesPolygon.size() - 1; i < verticesPolygon.size(); j = i++) {
            if ((polyY[i] < y && polyY[j] >= y)
                    || (polyY[j] < y && polyY[i] >= y)
                    && (polyX[i] <= x || polyX[j] <= x)) {
                if (polyX[i] + (y - polyY[i]) / (polyY[j] - polyY[i])
                        * (polyX[j] - polyX[i]) < x) {
                    // The line cross this arc
                    oddTransitions = !oddTransitions;
                }
            }
        }
        // Return odd-even number of intersecs
        return oddTransitions;
    }
}
