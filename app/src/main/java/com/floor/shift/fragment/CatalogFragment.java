package com.floor.shift.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.floor.shift.MainAct;
import com.floor.shift.R;
import com.floor.shift.adapter.FloorMapAdapter;
import com.floor.shift.entity.FloorMap;
import com.floor.shift.net.NetLoadingDailog;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

@SuppressLint({ "NewApi", "ValidFragment" })
public class CatalogFragment extends Fragment {
    /*header view*/
    private MainAct mAct;
    private FloorMapAdapter adapterContacts;
    private ListView listContacts;
    private NetLoadingDailog loadingDlg;
    private String mErrorMessage;

    public static CatalogFragment newInstance(MainAct mainAct){
        CatalogFragment messageFrag = new CatalogFragment();
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

        listContacts = (ListView) view.findViewById(R.id.list_messages);
        adapterContacts = new FloorMapAdapter(getActivity(), R.layout.item_catalog, AppConstants.CurrentFloorMaps);
        listContacts.setAdapter(adapterContacts);

        listContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FloorMap selectMessages = AppConstants.CurrentFloorMaps.get(position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getFloorMap();
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
                        if(item.has("objectId"))
                            floorMap.setId(item.getString("objectId"));
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

                        updateListView(AppConstants.CurrentFloorMaps);

                    }
                }else{
                    CommonMethods.showMessage(getActivity(), e.getMessage());
                }
            }
        });
    }

    private void updateListView(ArrayList<FloorMap> lists){
        adapterContacts = new FloorMapAdapter(getActivity(), R.layout.item_catalog, lists);
        listContacts.setAdapter(adapterContacts);
        adapterContacts.notifyDataSetChanged();
    }
}
