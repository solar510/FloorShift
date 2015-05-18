package com.floor.shift;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.floor.shift.entity.User;
import com.floor.shift.utils.AppConstants;
import com.floor.shift.utils.SimpleSSLSocketFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class FloorShiftApp extends Application {
	private boolean BIND_STATE = false;
	private SharedPreferences prvPref;
	private SharedPreferences.Editor prevEditor;
	private MediaPlayer backgroundMusic;
	private static FloorShiftApp mInstance = null;

    private User user;
    public static DisplayImageOptions thumbnailOptions;
    public static DisplayImageOptions avatarOptions;
    public static DisplayImageOptions largeImageOptions;

	@Override
	public void onCreate() {
		super.onCreate();
		Crashlytics.start(this);

        Parse.initialize(this, AppConstants.PARSE_APP_ID, AppConstants.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        PushService.setDefaultPushCallback(this, MainAct.class);
//        ParseUser.enableAutomaticUser();
//        ParseACL defaultACL = new ParseACL();
//        ParseACL.setDefaultACL(defaultACL, true);

        ParsePush.subscribeInBackground("testchannel", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        prvPref = PreferenceManager.getDefaultSharedPreferences(this);
		prevEditor = prvPref.edit();
		mInstance = this;

        user = new User();

        /*universal Image loader*/
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false).cacheOnDisc(true)
                .displayer(new FadeInBitmapDisplayer(500, true, true, false))
                .build();

        ImageLoaderConfiguration imageLoaderConfig = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(imageLoaderConfig);
	}

    public static FloorShiftApp getInstance() {
        return mInstance;
    }

    /*set and get user Info*/
    public void setCurrentUser(User user) {
        this.user = user;
    }

    public User getCurrentUser() {
        return user;
    }

	// get and set Twitter accessSecret
	public String getAccessSecret() {
		return prvPref.getString("ACCESS_SECRET", null);
	}

	public void setAccessSecret(String str) {
		prevEditor.putString("ACCESS_SECRET", str).commit();
	}

	public long getAccessExpires() {
		return prvPref.getLong("ACCESS_EXPIRES", 0);
	}

	public void setAccesExpires(Long tmp) {
		prevEditor.putLong("ACCESS_EXPIRES", tmp);
		prevEditor.commit();
	}

	// get and set LoginState
	public boolean getLoginState() {
		return prvPref.getBoolean("LOGIN_STATE", false);
	}

	public void setLoginState(boolean state) {
		prevEditor.putBoolean("LOGIN_STATE", state).commit();
	}

    /*get and set for rdio skip playback*/
    public boolean getSkipState(){
        return prvPref.getBoolean("SKIP", false);
    }

    public void setSkipState(boolean state){
        prevEditor.putBoolean("SKIP", state).commit();
    }

	/* get and set logined USER name */
	public String getUserName() {
		return prvPref.getString("USER_NAME", null);
	}

	public void setUserName(String url) {
		prevEditor.putString("USER_NAME", url).commit();
	}

	/* get and set logined service name */
	public String getLoginedProfileImgURL() {
		return prvPref.getString("PROFILE_IMG_URL", "");
	}

	public void setLoginedProfileImgURL(String url) {
		prevEditor.putString("PROFILE_IMG_URL", url);
		prevEditor.commit();
	}

	/* get and set social auth id for logined user */
	public String getLoginedUserSocialAuthID() {
		return prvPref.getString("USER_SOCIALAUTH_ID", "null");
	}

	public void setLoginedUserSocialAuthID(String id) {
		prevEditor.putString("USER_SOCIALAUTH_ID", id);
		prevEditor.commit();
	}

	/* get and set logined user email */
	public String getUserEmail() {
		return prvPref.getString("USER_EMAIL", "");
	}

	public void setUserEmail(String email) {
		prevEditor.putString("USER_EMAIL", email).commit();
	}

    /* get and set logined user passwd */
    public String getUserPasswd() {
        return prvPref.getString("USER_PASSWD", "");
    }

    public void setUserPasswd(String passwd) {
        prevEditor.putString("USER_PASSWD", passwd).commit();
    }

    /* get and set logined user id */
    public int getUserID() {
        return prvPref.getInt("USER_ID", 0);
    }

    public void setUserID(int id) {
        prevEditor.putInt("USER_ID", id).commit();
    }

	/* get and set floorMapID */
	public String getFloorMapId() {
		return prvPref.getString("FLOORMAP_ID", null);
	}

	public void setFloorMapId(String id) {
		prevEditor.putString("FLOORMAP_ID", id);
		prevEditor.commit();
	}

	/* get and set gender */
	public String getCurrentGenres() {
		return prvPref.getString("GENRES", "");
	}

	public void setCurrentGenres(String genres) {
		prevEditor.putString("GENRES", genres);
		prevEditor.commit();
	}
	
	/* get Http Client */
	public DefaultHttpClient getDefaultHttpClient() {
		DefaultHttpClient httpClient = null;
		try {
			SSLSocketFactory sslFactory = new SimpleSSLSocketFactory(null);
			sslFactory
					.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams param = new BasicHttpParams();
			HttpProtocolParams.setVersion(param, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(param, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sslFactory, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					param, registry);
			httpClient = new DefaultHttpClient(ccm, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpClient;
	}

	public void clearUserInfo() {
		prevEditor.clear();
		prevEditor.commit();
	}

    static {
        largeImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false).cacheOnDisc(true)
                .displayer(new FadeInBitmapDisplayer(500, true, false, false))
                .showImageOnLoading(R.drawable.placeholder) // resource or // drawable
                .showImageForEmptyUri(R.drawable.placeholder) // resource or // drawable
                .showImageOnFail(R.drawable.placeholder) // resource or drawable
                .resetViewBeforeLoading(true) // default
                .build();
        thumbnailOptions = new DisplayImageOptions.Builder()
                .displayer(new FadeInBitmapDisplayer(500, true, false, false))
                .cacheInMemory(true).cacheOnDisc(true)
                .resetViewBeforeLoading(true).build();
        avatarOptions = new DisplayImageOptions.Builder()
                .displayer(new FadeInBitmapDisplayer(500, true, false, false))
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageOnLoading(R.drawable.placeholder) // resource or // drawable
                .showImageForEmptyUri(R.drawable.placeholder) // resource or // drawable
                .showImageOnFail(R.drawable.placeholder) // resource or drawable
                .resetViewBeforeLoading(true).build();
    }
}
