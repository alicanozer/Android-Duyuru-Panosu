package com.ozer.duyurupanosu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import com.ozer.duyurupanosu.R;

import java.util.ArrayList;
import java.util.List;


public class ActivityMain extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener {
    /***/
    private static String KULLANICI_TABLO = "kisiler";
    private static String KULLANICIADI = "kullaniciadi";
    private static String ADISOYADI = "title";
    private static String PAROLA = "parola";
    private static String USERNAME = "USER";
    private static String USERID = "ID";

    private static ApplicationPano mApplication;
    private ApigeeDataClient mClient;

    SharedPreferences mSharedPrefs = null;
    SharedPreferences.Editor mPrefsEditor = null;
    // ArrayList<Entity> mEntities = null;
    /***/
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /****/
        mSharedPrefs = getSharedPreferences("xmlFile", MODE_PRIVATE);
        mPrefsEditor = mSharedPrefs.edit();
        /***/
        mContext = this;
        mApplication = (ApplicationPano) getApplication();
        mApplication.initClient(mContext);
        mClient = mApplication.getClient();
        /***/

        txtKullaniciAdi = (EditText) findViewById(R.id.et_kullaniciadi);
        txtParola = (EditText) findViewById(R.id.et_parola);
        //todo
        txtKullaniciAdi.setText(mSharedPrefs.getString(USERID, null));

        login_button = (Button) findViewById(R.id.button_login);
        register_button = (Button) findViewById(R.id.button_register);
        tv_forgot = (TextView) findViewById(R.id.tv_forgot);

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());

        login_button.setOnClickListener(this);
        register_button.setOnClickListener(this);
        tv_forgot.setOnTouchListener(this);

        isConnected();

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
            return true;
        } else if (id == R.id.action_yeniekle) {
            Intent intent = new Intent(mContext, ActivityAboutme.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v == login_button) {
            new LoginTask().execute();

        } else if (v == register_button) {
            Intent i = new Intent(mContext, ActivityRegister.class);
            startActivity(i);
        }
    }

    private void login() {
        UserNick = txtKullaniciAdi.getText().toString();
        UserPassword = txtParola.getText().toString();

        if (UserNick == null || UserPassword == null ||
                UserNick.matches("") || UserPassword.matches("")) {
            Toast.makeText(mContext, "Boş Bırakmayınız", Toast.LENGTH_SHORT).show();
        } else {
            if (mClient != null) {
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
                                    mPrefsEditor.commit();
                                    st = true;

                                    Toast.makeText(getApplicationContext(), "Giriş Yapıldı :D", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext, ActivityIcerikler.class);
                                    startActivity(intent);
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
                        Log.e(getClass().getSimpleName().toString(), "Büyük Sorun Var");
                    }
                });
            }
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
      //  alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public class LoginTask extends AsyncTask<String, Void, Void>
    {

        ProgressDialog pd = null;
        Context ctx=mContext;

        @Override
        protected void onPreExecute(){
          //  super.onPreExecute();

            pd = ProgressDialog.show(mContext, "AsyncTask Example",
                    "Initializing ...", true, true);
        }


        @Override
        protected Void doInBackground(String... params){

                login();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
           // super.onPostExecute(result);
            pd.dismiss();

        }

    }

}
