package com.ozer.duyurupanosu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apigee.sdk.apm.android.Log;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.entities.Entity;
import com.apigee.sdk.data.client.response.ApiResponse;

import java.util.List;

/**
 * Created by Can on 03.02.2015.
 */
public class ActivityForgot extends Activity implements View.OnClickListener {

    private static String KULLANICI_TABLO = "kisiler";
    private static String KULLANICIADI = "kullaniciadi";
    private static String PAROLA = "parola";
    private static String MAIL = "mail";
    private static String GIZLISORU = "gizlisoru";
    private static String GIZLICEVAP = "gizlicevap";

    private static ApplicationPano mApplication;
    private ApigeeDataClient mClient;
    private Context mContext;

    private EditText txtMail;
    private EditText txtCevap;
    private TextView tv_soru;
    private Button bt_sorugetir;
    private Button bt_cevapgoster;

    private String gercekMail, girilenMail, gercekKullaniciAdi;
    private String girilenCevap,gercekParola, gercekSoru, gercekCevap;
    private int yanlisGiris;
    InputMethodManager imm;

    /***/

    /***/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        mContext = this;
        yanlisGiris = 0;
        mApplication = (ApplicationPano) getApplication();
        mApplication.initClient(mContext);
        mClient = mApplication.getClient();

        txtMail = (EditText) findViewById(R.id.forgot_mail);
        tv_soru = (TextView) findViewById((R.id.forgot_soru));

        txtCevap = (EditText) findViewById(R.id.forgot_cevap);
        txtCevap.setVisibility(View.INVISIBLE);
        txtCevap.setText("");

        bt_sorugetir = (Button) findViewById(R.id.forgot_sorugetir);
        bt_sorugetir.setOnClickListener(this);

        bt_cevapgoster = (Button) findViewById(R.id.forgot_goster);
        bt_cevapgoster.setClickable(false);
        bt_cevapgoster.setVisibility(View.INVISIBLE);
        bt_cevapgoster.setOnClickListener(this);

        /****/
    }

    private void soruGoster() {
        bt_cevapgoster.setVisibility(View.INVISIBLE);
        txtCevap.setVisibility(View.INVISIBLE);
        girilenMail = txtMail.getText().toString();
        if (girilenMail == null || girilenMail.matches("")){
            tv_soru.setText("Mail giriniz");
        }
        else if (mClient != null) {
            mClient.getEntitiesAsync(KULLANICI_TABLO, "", new ApiResponseCallback() {
                @Override
                public void onResponse(ApiResponse apiResponse) {
                    List<Entity> mEntities = apiResponse.getEntities();
                    int sayac = mEntities.size();
                    for (int i = 0; i < sayac; i++) {
                        Entity entity = mEntities.get(i);
                        gercekMail = entity.getStringProperty(MAIL);
                        gercekKullaniciAdi = entity.getStringProperty(KULLANICIADI);
                        gercekParola = entity.getStringProperty(PAROLA);
                        gercekSoru = entity.getStringProperty(GIZLISORU);
                        gercekCevap = entity.getStringProperty(GIZLICEVAP);


                        if (gercekMail.matches(girilenMail)) {
                            Toast.makeText(mContext,"Cevap Giriniz",Toast.LENGTH_SHORT).show();

                            tv_soru.setText("Soru:" + gercekSoru);
                            txtCevap.setVisibility(ViewGroup.VISIBLE);
                            bt_cevapgoster.setVisibility(View.VISIBLE);
                            bt_cevapgoster.setClickable(true);
                            return;
                        }if(i == sayac-1){
                            tv_soru.setText("Kayıt Bulunamadı!!!");
                        }
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
    public void onClick(View view) {
        if (view == bt_sorugetir) {
            tv_soru.setText("");
            bt_cevapgoster.setVisibility(View.INVISIBLE);
            soruGoster();
        } else if (view == bt_cevapgoster) {
            cevapGoster();
        }
    }

    private void cevapGoster() {
        girilenCevap = txtCevap.getText().toString();
        if (girilenCevap == null || girilenCevap.matches("")) {
            Toast.makeText(mContext,"Lütfen cevap giriniz",Toast.LENGTH_SHORT).show();
            txtCevap.setHint("Cevap:");
        }else if (yanlisGiris > 3) {
            Toast.makeText(mContext, "Daha sonra deneyiniz", Toast.LENGTH_SHORT).show();
            finish();
        }else if(girilenCevap != null && (girilenCevap.matches(gercekCevap))){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Giriş Bilgileri");
            alertDialog.setMessage("Kullanıcı Adı: " + gercekKullaniciAdi +
                    "\nParola: " + gercekParola);
            alertDialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = getParentActivityIntent();
                    startActivity(i);
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }else{
            Toast.makeText(mContext, "Yanlış Cevap!!!", Toast.LENGTH_SHORT).show();
            yanlisGiris++;
        }
    }

}
