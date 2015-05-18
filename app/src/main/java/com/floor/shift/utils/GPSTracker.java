package com.floor.shift.utils;

import android.app.Service;
import android.content.*;
import android.location.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10L;
	private static final long MIN_TIME_BW_UPDATES = 60000L;
	boolean canGetLocation;
	boolean isGPSEnabled;
	boolean isNetworkEnabled;
	double latitude;
	Location location;
	protected LocationManager locationManager;
	double longitude;
	private final Context mContext;

	public GPSTracker(Context context) {
		isGPSEnabled = false;
		isNetworkEnabled = false;
		canGetLocation = false;
		mContext = context;
		getLocation();
	}

	public boolean canGetLocation() {
		return canGetLocation;
	}

	public double getLatitude() {
		if (location != null)
			latitude = location.getLatitude();
		return latitude;
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService("location");
			isGPSEnabled = locationManager.isProviderEnabled("gps");
			isNetworkEnabled = locationManager.isProviderEnabled("network");
			if (isGPSEnabled || isNetworkEnabled) {
				canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates("network", 60000L,
							10F, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation("network");
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				if (isGPSEnabled && location == null) {
					locationManager.requestLocationUpdates("gps", 60000L, 10F,
							this);
					Log.d("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation("gps");
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return location;
	}

	public double getLongitude() {
		if (location != null)
			longitude = location.getLongitude();
		return longitude;
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onLocationChanged(Location location1) {
	}

	public void onProviderDisabled(String s) {
	}

	public void onProviderEnabled(String s) {
	}

	public void onStatusChanged(String s, int i, Bundle bundle) {
	}

	public void showSettingsAlert() {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				mContext);
		builder.setTitle("GPS is settings");
		builder.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		builder.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						Intent intent = new Intent(
								"android.settings.LOCATION_SOURCE_SETTINGS");
						mContext.startActivity(intent);
					}

				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						dialoginterface.cancel();
					}
				});
		builder.show();
	}

	public void stopUsingGPS() {
		if (locationManager != null)
			locationManager.removeUpdates(this);
	}

}
