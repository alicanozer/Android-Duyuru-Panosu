package com.ozer.duyurupanosu;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apigee.sdk.apm.android.Log;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Can on 28.01.2015.
 */
public class ActivityIcerikler extends ListActivity implements View.OnClickListener {
    private static String ADMIN="admin";
    private static String USERID = "ID";
    private static String DUYURULAR_TABLO = "duyurular";
    private static String BASLIK = "name";
    private static String ICERIK = "icerik";
    private static String SAHIPID = "kullaniciadi";
    private static String ZAMANI = "zamani";
    private static String UUID="uuid";
    private static String GIZLISORU="gizlisoru";
    private static String GIZLICEVAP="gizlicevap";
    private static ApplicationPano mApplication;
    private String currentUserId = null;
    /***/
    private SharedPreferences mSharedPrefs = null;
    private SharedPreferences.Editor mPrefsEditor = null;
    private Context mContext;
    private ApigeeDataClient mClient;
    /***/

    private TextView txtSayac;
    private TextView txtNick;

    private Button ekle_button;
    private Button logout_button;
    private Button refresh_button;

    private ArrayList<BeanDuyuru> alDuyurular = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icerikler);

        txtNick = (TextView) findViewById(R.id.tv_usernick);
        txtSayac = (TextView) findViewById(R.id.tv_count);
        ekle_button = (Button) findViewById(R.id.btn_ekle);
        ekle_button.setOnClickListener(this);

        logout_button = (Button) findViewById(R.id.btn_logout);
        logout_button.setOnClickListener(this);

        refresh_button = (Button) findViewById(R.id.btn_refresh);
        refresh_button.setOnClickListener(this);

        mContext = this;
        try {
            mApplication = (ApplicationPano) getApplication();
            mApplication.initClient(mContext);
            mClient = mApplication.getClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSharedPrefs = getSharedPreferences("xmlFile", MODE_PRIVATE);
        mPrefsEditor = mSharedPrefs.edit();
        currentUserId = mSharedPrefs.getString(USERID, null);
        txtNick.setText(currentUserId);

        listele();
    }

    private void listele() {
        alDuyurular=new ArrayList<BeanDuyuru>();
        alDuyurular.clear();
        mClient.getEntitiesAsync(DUYURULAR_TABLO, "", new ApiResponseCallback() {
            @Override
            public void onResponse(ApiResponse apiResponse) {
                List<Entity> mEntities = apiResponse.getEntities();
                int sayac = mEntities.size();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                String strDate = formatter.format(c.getTime());
                txtSayac.setText(String.valueOf(sayac)+" Duyuru");

                for (int i = 0; i < sayac; i++) {
                    Entity entity = mEntities.get(i);
                    BeanDuyuru duyurum = new BeanDuyuru();
                    duyurum.setBaslik(entity.getStringProperty(BASLIK));
                    duyurum.setSahipId(entity.getStringProperty(SAHIPID));
                    duyurum.setIcerik(entity.getStringProperty(ICERIK));
                    /***************/
                    if(entity.getStringProperty(ZAMANI).substring(0,7).matches(strDate.substring(0,7))){
                        duyurum.setZamani("Bugün->" + entity.getStringProperty(ZAMANI).substring(9));
                    }else{
                        duyurum.setZamani(entity.getStringProperty(ZAMANI));
                    }
                    duyurum.setUuid(entity.getStringProperty(UUID));
                    alDuyurular.add(duyurum);
                }
            }
            @Override
            public void onException(Exception e) {
                Log.i("hata", "hata var");
            }
        });
        AdapterDuyuru mAdapter = new AdapterDuyuru(mContext, R.layout.listview_satir, alDuyurular);
        setListAdapter(mAdapter);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == ekle_button) {
            Intent i = new Intent(mContext, ActivityDuyuruEkle.class);
            startActivity(i);
            listele();

        } else if (v == logout_button) {
            Toast.makeText(this, "Oturum Kapandı", Toast.LENGTH_SHORT).show();
            mPrefsEditor.clear();
            finish();

        } else if (v == refresh_button) {
            listele();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final BeanDuyuru secilenMesaj = alDuyurular.get(position);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityIcerikler.this);
        alertDialog.setTitle(secilenMesaj.getBaslik());
        alertDialog.setMessage(secilenMesaj.getIcerik());
        alertDialog.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                duyuruSil(secilenMesaj);
            }
        });
        alertDialog.setNegativeButton("iptal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
        /***/
        listele();
    }

    private void duyuruSil(BeanDuyuru secilenMesaj) {
        boolean status=false;
        if (secilenMesaj.getSahipId().matches(currentUserId) || currentUserId.matches(ADMIN)){
            mClient.removeEntityAsync(DUYURULAR_TABLO,secilenMesaj.getUuid(),new ApiResponseCallback() {
                @Override
                public void onResponse(ApiResponse apiResponse) {

                    if (apiResponse != null){
                        Toast.makeText(mContext,"Silindi",Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onException(Exception e) {
                    Toast.makeText(mContext,"Bir hata oluştu(duyuruSil)",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(mContext,"Duyuruyu siz yapmadınızki",Toast.LENGTH_SHORT).show();
        }
        listele();
    }

}
