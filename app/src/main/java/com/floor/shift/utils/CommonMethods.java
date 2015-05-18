package com.floor.shift.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.floor.shift.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.ParseGeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    Copyright (C) 2015 SeniorPanda
    Created by SeniorPanda on 2/15/2015.
*/

public class CommonMethods {
	static int counter = 0;
	public CommonMethods() {
	}

	public static String LOGIN_SERVICE_NAME = "none";
	public static String USER_ACCESS_TOKEN, USER_PROFILE_URL,
			USER_SOCIALAUTH_ID, USER_ACCESS_SECRET;
	public static long USER_ACCESS_EXPIRES;
	public static final String PUSH_ACTION = "com.crubysoft.soundstation.UPDATE_SCREEN";
	public static final String CHANNEL_NAME = "channel";
	public static final String CHANNEL_ID = "channel_id";
    public static final String TAG = "SongsForUs";
	
    public static void displayMessage(Context context, String channelName, String channelID) {
        Intent intent = new Intent(PUSH_ACTION);
        intent.putExtra(CHANNEL_NAME, channelName);
        intent.putExtra(CHANNEL_ID, channelID);
        context.sendBroadcast(intent);
    }
    
	public static void hideKeyboard(Activity act, View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) act
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

    public static void showKeyboard(Activity act, View view){
        InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInputFromInputMethod(view.getWindowToken(), 0);
//        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

	public static File getTempFilePath(Context context, String str_profile) {
		// it will return /sdcard/image.tmp
		final File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}

		return new File(path, str_profile);
	}

	public static boolean imageReduceAndCompressJPG(String imagePath, boolean isCamera) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		int width = options.outWidth;
		int height = options.outHeight;

		Bitmap picture;

		// reduce size
		if (width > 320) {
			BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
			bmpOptions.inSampleSize = width / 320 + 1;
			picture = BitmapFactory.decodeFile(imagePath, bmpOptions);
		} else {
			picture = BitmapFactory.decodeFile(imagePath);
		}

		// make square size
		boolean cameraRotated = false;
		width = picture.getWidth();
		height = picture.getHeight();
		int left, top;
		if (width > height) {

			if (isCamera)
				cameraRotated = true;

			left = (width - height) / 2;
			top = 0;
			width = height;
		} else {
			left = 0;
			top = (height - width) / 2;
			height = width;
		}
		if (cameraRotated) {
			Matrix mtx = new Matrix();
			mtx.postRotate(90);
			picture = Bitmap.createBitmap(picture, left, top, width, height,
					mtx, true);
		} else
			picture = Bitmap.createBitmap(picture, left, top, width, height);

		// comress JPEG
		try {
			FileOutputStream bmpFile = new FileOutputStream(imagePath);
			picture.compress(Bitmap.CompressFormat.JPEG, 60, bmpFile);
			bmpFile.flush();
			bmpFile.close();
			picture.recycle();
			picture = null;
		} catch (Exception e) {
			picture.recycle();
			picture = null;

			return false;
		}

		return true;

	}

	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth,
			int reqHeight) { // BEST QUALITY MATCH

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		int inSampleSize = 1;

		if (height > reqHeight) {
			inSampleSize = Math.round((float) height / (float) reqHeight);
		}

		int expectedWidth = width / inSampleSize;

		if (expectedWidth > reqWidth) {
			inSampleSize = Math.round((float) width / (float) reqWidth);
		}

		options.inSampleSize = inSampleSize;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static String getRealPathFromURI(Uri uri, Activity act) {
		String filePath;
		if (uri != null && "content".equals(uri.getScheme())) {
			Cursor cursor = act
					.getContentResolver()
					.query(uri,
							new String[] { android.provider.MediaStore.Images.ImageColumns.DATA },
							null, null, null);
			cursor.moveToFirst();
			filePath = cursor.getString(0);
			cursor.close();
		} else {
			filePath = uri.getPath();
		}
		return (filePath);
	}

	public static String getUnixTimeStamp() {
		long unixTime = System.currentTimeMillis() / 1000;
		return Long.toString(unixTime);
	}

	public static void showMessage(Activity activity, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setTitle(activity.getString(R.string.app_name))
				.setIcon(R.drawable.ic_launcher).setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

		builder.create().show();
	}

    public static void showMessage(Activity activity, String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }

	public static int getWindowWidth(Activity act) {
		DisplayMetrics metrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	public static int getWindowHeight(Activity act) {
		DisplayMetrics metrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	public static void showCloseMessage(final Activity activity) {
		final AlertDialog.Builder alertDlgBuilder = new AlertDialog.Builder(
				activity)
				.setTitle("SoundStation")
				.setIcon(android.R.drawable.stat_sys_warning)
				.setMessage("SoundStation requires internet \n data connection to work")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
						System.exit(0);
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}).setCancelable(false);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog alertDlg = alertDlgBuilder.create();
				alertDlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				alertDlg.show();
			}
		});
	}

	public static Boolean checkFbInstalled(Context context) {
		PackageManager pm = context.getPackageManager();
		boolean flag = false;
		try {
			pm.getPackageInfo("com.facebook.katana",
					PackageManager.GET_ACTIVITIES);
			flag = true;
		} catch (NameNotFoundException e) {
			flag = false;
		}
		return flag;
	}

	public static boolean isInternetAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = mgr.getActiveNetworkInfo();

		return (netInfo != null && netInfo.isConnected() && netInfo
				.isAvailable());
	}

    private static boolean PLAYING_STATE = false;

    public static void setPlayingState(boolean state) {
        PLAYING_STATE = state;
    }

    // true: playing, false: stop or pause
    public static boolean getPlayingState() {
        return PLAYING_STATE;
    }

    public static Bitmap checkIfSoundIconExists(String urlName, Context context) {
		String currentPath = "";
		PackageManager m = context.getPackageManager();
		String s = context.getPackageName();
		try {
			PackageInfo p = m.getPackageInfo(s, 0);
			currentPath = p.applicationInfo.dataDir;
			currentPath = currentPath + "/SoundStation";
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error Package name not found ", e);
		}

		File SDCardRoot = new File(currentPath);
		if (!SDCardRoot.exists()) {
			SDCardRoot.mkdir();
			return null;
		}

        try{
            urlName = URLEncoder.encode(urlName, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
        }
		File imageFile = new File(currentPath + "/" + urlName + ".jpg");

        if (imageFile.exists()) {
			return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
		}
		return null;
	}

	public static boolean saveBitmapTosdCard(Bitmap bitmap, String url, Context context) {
		try {
			String currentPath = "";
			PackageManager m = context.getPackageManager();
			String s = context.getPackageName();
			try {
				PackageInfo p = m.getPackageInfo(s, 0);
				currentPath = p.applicationInfo.dataDir;
				currentPath = currentPath + "/SoundStation/";
			} catch (NameNotFoundException e) {
				Log.e(TAG, "Error Package name not found ", e);
			}

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            url = URLEncoder.encode(url, "UTF-8");
            File f = new File(currentPath, url + ".jpg");

            if(!f.exists())
    			f.createNewFile();

			// write the bytes in file
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());

			fo.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static int dpToPx(int dp, Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

	public static boolean isSpecialChar(String str) {
		Pattern p = Pattern.compile("[!*'\"();:@&=+$,/?%#%]");
		Matcher m = p.matcher(str);
		return m.find();
	}

	public static InputFilter alphaNumericFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence arg0, int arg1, int arg2,
				Spanned arg3, int arg4, int arg5) {
			for (int k = arg1; k < arg2; k++) {
                if(Character.isSpaceChar(arg0.charAt(k))){

                }else if(!Character.isLetterOrDigit(arg0.charAt(k))) {
					return "";
				}
			}
			return null;
		}
	};

    public static void hideSoftKeyboard(Activity act, View v) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static String getProfiling(String strCount, String type, boolean IsUpper){
        if(strCount != null){
            int count = Integer.parseInt(strCount);
            if(count == 1)
                strCount = String.format("1 %s", type);
            else if(count < 1000) {
                if(count < 0)
                    count = 0;
                if(IsUpper)
                    strCount = String.format("%d %sS", count, type);
                else
                    strCount = String.format("%d %ss", count, type);
            }else {
                if(IsUpper)
                    strCount = String.format("%.1fK %sS", (double) count / 1000, type);
                else
                    strCount = String.format("%.1fK %ss", (double) count / 1000, type);
            }
        }
        return strCount;
    }

    public static String getProfiling(int count, String type, boolean IsUpper){
        String strCount = "";
        if(count < 0)
            count = 0;
        if(count == 1)
            strCount = String.format("1 %s", type);
        else if(count < 1000){
            if(IsUpper)
                strCount = String.format("%d %sS", count, type);
            else
                strCount = String.format("%d %ss", count, type);
        }else {
            if(IsUpper)
                strCount = String.format("%.1fK %sS", (double) count / 1000, type);
            else
                strCount = String.format("%.1fK %ss", (double) count / 1000, type);
        }
        return strCount;
    }

    public static boolean deleteInternalStorageData(Context context){
        String currentPath = "";
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            currentPath = p.applicationInfo.dataDir;
            currentPath = currentPath + "/SoundStation/";
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Error Package name not found ", e);
        }

        File dir = new File(currentPath);
        if( dir.exists() ) {
            for(File files :  dir.listFiles()) {
                files.delete();
            }
        }
        return dir.delete();
    }

    public static void setupUI(final Activity act, View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(act, v);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(act, innerView);
            }
        }
    }

    public static boolean isValidEmail(String s) {
        return Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

    public static String convertBase64(String str){
        byte[] data;
        try {
            data = str.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            return base64.trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isValidPhoneNumberNational(String phoneNumber){
        if(!TextUtils.isEmpty(phoneNumber)){
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber, "CH");
                return phoneUtil.isValidNumber(swissNumberProto);
            } catch (NumberParseException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException numberformatexception) {
            return false;
        }
        return true;
    }

    public static final String DISPLAY_MESSAGE_ACTION = "com.floor.shift.DISPLAY_MESSAGE";
    public static final String EXTRA_MESSAGE = "message";
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by the
     * UI and the background service.
     *
     * @param context
     *            application's context.
     * @param message
     *            message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        double distance = locationA.distanceTo(locationB);
        return distance;
    }

    public static double getAreaQuadrilateral(ParseGeoPoint A, ParseGeoPoint B, ParseGeoPoint C, ParseGeoPoint D){
        double area = 0;
        if(A == null || B == null || C == null || D == null)
            return area;
        else{
            area = A.getLongitude() * B.getLatitude() - B.getLongitude() * A.getLatitude() +
                    B.getLongitude() * C.getLatitude() - C.getLongitude() * B.getLatitude() +
                    C.getLongitude() * D.getLatitude() - D.getLongitude() * C.getLatitude() +
                    D.getLongitude() * A.getLatitude() - A.getLongitude() * D.getLatitude();
        }
        return Math.abs(0.5 * area);
    }

    public static boolean IsPointInArea(ParseGeoPoint A, ParseGeoPoint B, ParseGeoPoint C, ParseGeoPoint D, ParseGeoPoint currentPoint){
        if(A == null || B == null || C == null || D == null)
            return false;

        boolean IsPointInArea = false;
        double a1 = Math.sqrt(Math.pow((A.getLongitude() - B.getLongitude()), 2) + Math.pow((A.getLatitude() - B.getLatitude()), 2));
        double a2 = Math.sqrt(Math.pow((B.getLongitude() - C.getLongitude()), 2) + Math.pow((B.getLatitude() - C.getLatitude()), 2));
        double a3 = Math.sqrt(Math.pow((C.getLongitude() - D.getLongitude()), 2) + Math.pow((C.getLatitude() - D.getLatitude()), 2));
        double a4 = Math.sqrt(Math.pow((D.getLongitude() - A.getLongitude()), 2) + Math.pow((D.getLatitude() - A.getLatitude()), 2));

        double b1 = Math.sqrt(Math.pow((A.getLongitude() - currentPoint.getLongitude()), 2) + Math.pow((A.getLatitude() - currentPoint.getLatitude()), 2));
        double b2 = Math.sqrt(Math.pow((B.getLongitude() - currentPoint.getLongitude()), 2) + Math.pow((B.getLatitude() - currentPoint.getLatitude()), 2));
        double b3 = Math.sqrt(Math.pow((C.getLongitude() - currentPoint.getLongitude()), 2) + Math.pow((C.getLatitude() - currentPoint.getLatitude()), 2));
        double b4 = Math.sqrt(Math.pow((D.getLongitude() - currentPoint.getLongitude()), 2) + Math.pow((D.getLatitude() - currentPoint.getLatitude()), 2));

        double u1 = (a1 + b1 + b2) / 2;
        double u2 = (a2 + b2 + b3) / 2;
        double u3 = (a3 + b3 + b4) / 2;
        double u4 = (a4 + b4 + b1) / 2;
        
        double area1 = Math.sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2));
        double area2 = Math.sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3));
        double area3 = Math.sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4));
        double area4 = Math.sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1));

        double area = getAreaQuadrilateral(A, B, C, D);

        if(RoundTo2Decimals(area) == RoundTo2Decimals(area1 + area2 + area3 + area4)) {
            IsPointInArea = true;
            if(RoundTo2Decimals(area) == 0)
                IsPointInArea = false;
        }else{
            IsPointInArea = false;    //    if(area > area1 + area2 + area3 + area4)
        }
        return IsPointInArea;
    }

    public static double RoundTo2Decimals(double val){
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(val));
    }
}