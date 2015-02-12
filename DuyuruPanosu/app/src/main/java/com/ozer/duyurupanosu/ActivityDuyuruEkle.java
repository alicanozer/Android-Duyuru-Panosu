package com.ozer.duyurupanosu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apigee.sdk.apm.android.Log;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.ozer.duyurupanosu.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Can on 30.01.2015.
 */
public class ActivityDuyuruEkle extends ActionBarActivity implements View.OnClickListener {
    private static String DUYURULAR = "duyurular";
    private static String BASLIK = "name";
    private static String ICERIK = "icerik";
    private static String SAHIPID = "kullaniciadi";
    private static String ZAMANI = "zamani";
    private static String USERID = "ID";
    /*baglanti icin gerekli degiskenler*/
    private static ApplicationPano mApplication;
    /**
     * *
     */
    Button kaydet_butonu;
    private String currentUserId = null;
    private String currentUserName = null;
    private SharedPreferences mSharedPrefs = null;
    private SharedPreferences.Editor mPrefsEditor = null;
    private ApigeeDataClient mClient;
    private EditText txtDuyuruBaslik;
    private EditText txtDuyuruIcerik;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duyuruekle);
        mContext = this;
        mApplication = (ApplicationPano) getApplication();
        mApplication.initClient(mContext);
        mClient = mApplication.getClient();

        mSharedPrefs = getSharedPreferences("xmlFile", MODE_PRIVATE);
        mPrefsEditor = mSharedPrefs.edit();
        currentUserId = mSharedPrefs.getString(USERID, null);

        kaydet_butonu = (Button) findViewById(R.id.button_kaydet);
        txtDuyuruBaslik = (EditText) findViewById(R.id.et_baslik);
        txtDuyuruIcerik = (EditText) findViewById(R.id.et_icerik);

        kaydet_butonu.setOnClickListener(this);
    }


    private void duyuruKaydet() {
        if (mClient != null) {

            Entity duyuruEntity = new Entity(mClient, DUYURULAR);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            String strDate = format.format(c.getTime());

            duyuruEntity.setProperty(BASLIK, txtDuyuruBaslik.getText().toString());
            duyuruEntity.setProperty(ICERIK, txtDuyuruIcerik.getText().toString());
            duyuruEntity.setProperty(SAHIPID, currentUserId);
            duyuruEntity.setProperty(ZAMANI, strDate);

            mClient.createEntityAsync(duyuruEntity, new ApiResponseCallback() {
                @Override
                public void onResponse(ApiResponse apiResponse) {
                    int sonuc = apiResponse.getEntityCount();
                    if (sonuc > 0) {
                        //eklediyse
                        Toast.makeText(mContext, "Eklendi", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(mContext, "Eklenemedi!!!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onException(Exception e) {
                    Log.e(getClass().getSimpleName().toString(), "Büyük Sorun Var");
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v == kaydet_butonu) {
          //  new DuyuruEkle().execute();
            duyuruKaydet();

        }
    }
    //TODO duyuruekleyi asenkron yap

    public class DuyuruEkle extends AsyncTask<String, Void, Void> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mContext);
            pd.setMessage("Duyuruluyor...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            duyuruKaydet();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pd.dismiss();
        }
    }
}
