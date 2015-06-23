package com.ozer.duyurupanosu;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apigee.sdk.apm.android.Log;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import java.util.List;

import static com.ozer.duyurupanosu.Util.DISPLAY_MESSAGE_ACTION;
import static com.ozer.duyurupanosu.Util.EXTRA_MESSAGE;
import static com.ozer.duyurupanosu.Util.TAG;

public class ActivityMain extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener {
    private static String KULLANICIADI = "kullaniciadi";
    private static String PAROLA = "parola";
    private static String USERID = "ID";

    private ApigeeDataClient mClient;

    SharedPreferences mSharedPrefs = null;
    SharedPreferences.Editor mPrefsEditor = null;

    Button login_button;
    Button register_button;
    String UserNick, UserPassword;

    private EditText txtKullaniciAdi;
    private EditText txtParola;
    private TextView tv_forgot;
    private Context mContext;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    private AlertDialogManager alert = new AlertDialogManager();


    private StartAppAd startAppAd = new StartAppAd(this);
    AdView adView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;

        /*reklam*/
        StartAppSDK.init(this, "102540640", "202286358", true);
        /***/

        // this is a hack to force AsyncTask to be initialized on main thread. Without this things
        // won't work correctly on older versions of Android (2.2, apilevel=8)

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());

        isConnected();

        try {
            Class.forName("android.os.AsyncTask");
        } catch (Exception ignored) {}

        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        initUI();

        AppServices.loginAndRegisterForPush(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }
    @Override
    public void onBackPressed() {
        startAppAd.onBackPressed();
        super.onBackPressed();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
/*REKLAMLAR*/
        StartAppAd.showSlider(this);
        //Burda AdView objesini oluşturuyoruz ve anasayfa.xml de oluşturduğumuz adView e bağlıyoruz
        AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest); //adView i yüklüyoruz
/**/
        /****/
        mSharedPrefs = getSharedPreferences("xmlFile", MODE_PRIVATE);
        mPrefsEditor = mSharedPrefs.edit();
        /***/
        mContext = ActivityMain.this;

        mClient = AppServices.getClient(mContext);
        /***/

        txtKullaniciAdi = (EditText) findViewById(R.id.et_kullaniciadi);
        txtParola = (EditText) findViewById(R.id.et_parola);

        txtKullaniciAdi.setText(mSharedPrefs.getString(USERID, null));
        if (!mSharedPrefs.getString(PAROLA,"").matches("")) {
            txtParola.setText(mSharedPrefs.getString(PAROLA, ""));
            Toast.makeText(getApplicationContext(), "Giriş Yapıldı :D", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(mContext, ActivityIcerikler.class);
            startActivity(intent);
            startAppAd.showAd(); // show the ad
            startAppAd.loadAd(); // load the next ad

        }

        login_button = (Button) findViewById(R.id.button_login);
        register_button = (Button) findViewById(R.id.button_register);
        tv_forgot = (TextView) findViewById(R.id.tv_forgot);

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());

        login_button.setOnClickListener(this);
        register_button.setOnClickListener(this);
        tv_forgot.setOnTouchListener(this);

        registerReceiver(notificationReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(mContext, ActivitySettings.class);
            startActivity(intent);

        } else if (id == R.id.action_hakkimda) {
            Intent intent = new Intent(mContext, ActivityAboutme.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v == login_button) {
            login();
          //  login_button.setClickable(false);

        } else if (v == register_button) {
            Intent i = new Intent(mContext, ActivityRegister.class);
            startActivity(i);
            startAppAd.showAd(); // show the ad
            startAppAd.loadAd(); // load the next ad
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == tv_forgot) {
            Intent forgot = new Intent(mContext, ActivityForgot.class);
            startActivity(forgot);
        }
        return true;
    }

    public void isConnected() {
        // get Internet status
        isInternetPresent=cd.isConnectingToInternet();

        // check for Internet status
        if(!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(mContext, "No Internet Connection",
                    "You don't have internet connection.", false);
        }

    }
    void login() {
        UserNick = txtKullaniciAdi.getText().toString();
        UserPassword = txtParola.getText().toString();

        if (UserNick == null || UserPassword == null ||
                UserNick.matches("") || UserPassword.matches("")) {
            Toast.makeText(mContext, "Boş Bırakmayınız", Toast.LENGTH_SHORT).show();

        } else {
            if (mClient != null) {
                String KULLANICI_TABLO = "kisiler";
                mClient.getEntitiesAsync(KULLANICI_TABLO, "", new ApiResponseCallback() {
                    @Override
                    public void onResponse(ApiResponse apiResponse) {
                        List<Entity> mEntities = apiResponse.getEntities();
                        int sayac = mEntities.size();
                        boolean st = false;
                        for (int i = 0; i < sayac; i++) {
                            Entity entity = mEntities.get(i);
                            String uid = entity.getStringProperty(KULLANICIADI);
                            String upass = entity.getStringProperty(PAROLA);

                            if (uid != null && upass != null) {
                                if (UserNick.matches(uid) && UserPassword.matches(upass)) {
                                    mPrefsEditor.putString(USERID, UserNick);
                                    mPrefsEditor.putString(PAROLA, UserPassword);
                                    mPrefsEditor.commit();
                                    st = true;

                                    Toast.makeText(getApplicationContext(), "Giriş Yapıldı :D", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(mContext, ActivityIcerikler.class);
                                    startActivity(intent);
                                    startAppAd.showAd(); // show the ad
                                    startAppAd.loadAd(); // load the next ad
                                }
                            }
                        }
                        if (!st) {
                            Toast.makeText(mContext, "Giriş Başarısız!!!", Toast.LENGTH_SHORT).show();
                            txtParola.setText("");

                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e(getClass().getSimpleName(), "Büyük Sorun Var");
                    }
                });
            }
        }
    }
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        if(!status)
            alertDialog.setIcon(R.drawable.error);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Receives push Notifications
     * */
    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take some action upon receiving a push notification here!
             **/
            String message = intent.getExtras().getString(EXTRA_MESSAGE);
            if (message == null) { message = "Empty Message"; }

            android.util.Log.i(TAG, message);
            //messageTextView.append("\n" + message);

            alert.showAlertDialog(context, getString(R.string.gcm_alert_title), message);
            Toast.makeText(getApplicationContext(), getString(R.string.gcm_message, message), Toast.LENGTH_LONG).show();

            WakeLocker.release();
        }
    };

    // this will be called when the screen rotates instead of onCreate()
    // due to manifest setting, see: android:configChanges
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
    }

}
