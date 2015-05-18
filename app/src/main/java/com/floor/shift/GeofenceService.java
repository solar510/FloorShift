package com.floor.shift;

import static com.floor.shift.utils.CommonMethods.displayMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.floor.shift.entity.FloorMap;
import com.floor.shift.net.NetLoadingDailog;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.CommonMethods;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class GeofenceService extends Service {
	private LocationManager mLocationMgr;
	private Handler mHandler;

	private Location mLastLocation;
	private boolean mGeocoderAvailable;
	
	private static final int UPDATE_LASTLATLNG = 4;
	private static final int LAST_UP = 3;
	private static final int UPDATE_LATLNG = 2;
	private static final int UPDATE_ADDRESS = 1;

	private static final int SECONDS_TO_UP = 10000;
	private static final int METERS_TO_UP = 10;
	private static final int MINUTES_TO_STALE = 1000 * 60 * 2;
	double latti = 33.268644, longi = -87.624207;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE_ADDRESS:
					Log.e("xxx" ,"xxxx");
					// update address
					break;
				case UPDATE_LATLNG:
					break;
				case LAST_UP:
                    int position = -1;
                    for(int i = 0 ;  i < AppConstants.CurrentFloorMaps.size(); i++){
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

                        if(containsInPolygon(new LatLng(latti, longi), arrayPoints)){
                            position = i;
                            break;
                        }
                    }

                    if(position != -1){
                        FloorMap floorMap = AppConstants.CurrentFloorMaps.get(position);
                        AppConstants.curLat = latti;
                        AppConstants.curLng = longi;
                        FloorShiftApp.getInstance().setFloorMapId(floorMap.getId());
                        pushMessage(floorMap);
                    }
					break;
				}
			}
		};

		mGeocoderAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent();
		mLocationMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		setup();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	private void setup() {
		Location newLocation = null;
		mLocationMgr.removeUpdates(listener);
		newLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER, R.string.no_gps_support);

		// If gps location doesn't work, try network location
		if (newLocation == null) {
			newLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER, R.string.no_network_support);
		}

		if (newLocation != null) {
			updateUILocation(getBestLocation(newLocation, mLastLocation));
		}
	}

	private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
		Location location = null;
		if (mLocationMgr.isProviderEnabled(provider)) {
			mLocationMgr.requestLocationUpdates(provider, SECONDS_TO_UP, METERS_TO_UP, listener);
			location = mLocationMgr.getLastKnownLocation(provider);
		} else {
			Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
		}
		return location;
	}

	private final LocationListener listener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			updateUILocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void updateUILocation(Location location) {
		Message.obtain(mHandler, UPDATE_LATLNG, location.getLatitude() + ", " + location.getLongitude()).sendToTarget();
		latti = location.getLatitude();
		longi = location.getLongitude();
		
		if (mLastLocation != null) {
			Message.obtain(mHandler, UPDATE_LASTLATLNG, mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude()).sendToTarget();
		}
		mLastLocation = location;
		Date now = new Date();
		
		Message.obtain(mHandler, LAST_UP, now.toString()).sendToTarget();
		
		if (mGeocoderAvailable)
			doReverseGeocoding(location);
	}

	private void doReverseGeocoding(Location location) {
		(new ReverseGeocode(this)).execute(new Location[] { location });
	}

	/**
	 * This code is based on this code:
	 * http://developer.android.com/guide/topics
	 * /location/obtaining-user-location.html
	 * 
	 * @param newLocation
	 * @param currentBestLocation
	 * @return
	 */
	protected Location getBestLocation(Location newLocation, Location currentBestLocation) {
		if (currentBestLocation == null) {
			return newLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isNewerThanStale = timeDelta > MINUTES_TO_STALE;
		boolean isOlderThanStale = timeDelta < -MINUTES_TO_STALE;
		boolean isNewer = timeDelta > 0;

		if (isNewerThanStale) {
			return newLocation;
		} else if (isOlderThanStale) {
			return currentBestLocation;
		}

		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return newLocation;
		}
		return currentBestLocation;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	private class ReverseGeocode extends AsyncTask<Location, Void, Void> {
		Context mContext;

		public ReverseGeocode(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected Void doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

			Location loc = params[0];
			List<Address> addresses = null;
			try {
				addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			} catch (IOException e) {
				e.printStackTrace();
				// Update address field with the exception.
				Message.obtain(mHandler, UPDATE_ADDRESS, e.toString())
						.sendToTarget();
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and
				// country name.
				String addressText = String.format("%s, %s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address.getLocality(),
						address.getCountryName());
				// Update address field on UI.
				Message.obtain(mHandler, UPDATE_ADDRESS, addressText)
						.sendToTarget();
			}
			return null;
		}
	}

    private void pushMessage(FloorMap map){
        final JSONObject jObj;
        try{
            jObj = new JSONObject();
            jObj.put("action", MyPushCustomReceiver.IntentAction);
            ParseObject query = new ParseObject("Message");
            ParseGeoPoint from_loc = new ParseGeoPoint(AppConstants.curLat, AppConstants.curLng);
            query.put("from_loc", from_loc);
            String message = String.format("Arrived @%s to help", map.getName());
            query.put("to_loc", map.getId());
            query.put("type", false);
            jObj.put("type", false);
            jObj.put("to_loc", map.getId());
            query.put("message", message);
            jObj.put("message", message);
            ParseUser user = ParseUser.getCurrentUser();
            query.put("user", user.getObjectId());
            query.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ParseQuery pushQuery = ParseInstallation.getQuery();
                        pushQuery.whereEqualTo("deviceType", "android");
                        ParsePush push = new ParsePush();
                        push.setData(jObj);
                        push.sendInBackground();
                    } else {
                    }
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private boolean containsInPolygon(LatLng latLng, ArrayList<LatLng> polygon) {

        boolean oddTransitions = false;
        List<LatLng> verticesPolygon = polygon;
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
