package com.ozer.duyurupanosu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import java.util.List;


/**
 * Created by Can on 28.01.2015.
 */
public class ActivityRegister extends Activity implements View.OnClickListener {
    /*usergrid değişkenleri*/
    private static String KULLANICI_TABLO = "kisiler";
    private static String ADISOYADI = "name";
    private static String KULLANICIADI = "kullaniciadi";
    private static String PAROLA = "parola";
    private static String MAIL = "mail";
    private static String GIZLISORU="gizlisoru";
    private static String GIZLICEVAP="gizlicevap";

    /*baglanti icin gerekli degiskenler*/
    private static ApplicationPano mApplication;
    /**
     * *
     */
    Button registerUser;
    private ApigeeDataClient mClient;
    private EditText txtAdiSoyadi;
    private EditText txtKullaniciAdi;
    private EditText txtParola;
    private EditText txtMail;
    private EditText txtSoru;
    private EditText txtCevap;
    private TextView tv_uyarı;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;
        mApplication = (ApplicationPano) getApplication();
        mApplication.initClient(mContext);
        mClient = mApplication.getClient();

        txtAdiSoyadi = (EditText) findViewById(R.id.register_adisoyadi);
        txtKullaniciAdi = (EditText) findViewById(R.id.register_kullaniciadi);
        txtParola = (EditText) findViewById(R.id.register_parola);
        txtMail = (EditText) findViewById(R.id.register_mail);
        txtSoru = (EditText) findViewById(R.id.register_soru);
        txtCevap = (EditText) findViewById(R.id.register_cevap);
        tv_uyarı=(TextView) findViewById(R.id.tv_uyarı);
        registerUser = (Button) findViewById(R.id.button_register);
        registerUser.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == registerUser) {
            controlUserExist();
        }
    }

    private void KisiEkle() {
        if (mClient != null) {
            Entity kisiEntity = new Entity(mClient, KULLANICI_TABLO);

            String adisoyadi = txtAdiSoyadi.getText().toString();
            String kullaniciadi = txtKullaniciAdi.getText().toString();
            String parola = txtParola.getText().toString();
            String mail = txtMail.getText().toString();
            String soru = txtSoru.getText().toString();
            String cevap = txtCevap.getText().toString();

            kisiEntity.setProperty(ADISOYADI, adisoyadi);
            kisiEntity.setProperty(KULLANICIADI, kullaniciadi);
            kisiEntity.setProperty(PAROLA, parola);
            kisiEntity.setProperty(MAIL,mail);
            kisiEntity.setProperty(GIZLISORU,soru);
            kisiEntity.setProperty(GIZLICEVAP,cevap);

            if (adisoyadi.matches("") || kullaniciadi.matches("") || parola.matches("") ||
                    mail.matches("") || soru.matches("") || cevap.matches("")) {
                tv_uyarı.setText("Lütfen tüm alanları doldurunuz...");
            }else {
                mClient.createEntityAsync(kisiEntity, new ApiResponseCallback() {
                    @Override
                    public void onResponse(ApiResponse apiResponse) {
                        int sonuc = apiResponse.getEntityCount();
                        if (sonuc > 0) {
                            Toast.makeText(mContext, "Kişi Eklendi", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(mContext, "Kişi Eklenemedi!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onException(Exception e) {
                        Log.e(getClass().getSimpleName().toString(), "Büyük Bir Sorun Var");
                        Toast.makeText(mContext, "Büyük Bir Sorun Var", Toast.LENGTH_LONG);
                    }
                });
            }
        } else Toast.makeText(mContext, "mClient null geldi!!", Toast.LENGTH_LONG);
    }

    void controlUserExist(){
        if (mClient != null) {
            mClient.getEntitiesAsync(KULLANICI_TABLO, "", new ApiResponseCallback() {
                @Override
                public void onResponse(ApiResponse apiResponse) {
                    List<Entity> mEntities = apiResponse.getEntities();
                    int sayac = mEntities.size();
                    boolean status = false;
                    for (int i = 0; i < sayac; i++) {

                        Entity entity = mEntities.get(i);
                        String uid = entity.getStringProperty(KULLANICIADI);
                        String mail = entity.getStringProperty(MAIL);

                        if (uid != null && !uid.matches("") && uid.matches(txtKullaniciAdi.getText().toString())) {
                                Toast.makeText(mContext, "Kullanıcı adı kullanımda !!!", Toast.LENGTH_SHORT).show();
                                txtKullaniciAdi.setText("");
                            status = true;
                            }
                        if (mail != null && !mail.matches("") && mail.matches(txtMail.getText().toString())) {
                                Toast.makeText(mContext, "Mail adresi kullanımda !!!", Toast.LENGTH_SHORT).show();
                                txtMail.setText("");
                            status = true;
                            }
                    }
                    if (!status) {
                        KisiEkle();
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

