package com.ozer.duyurupanosu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gcm.GCMRegistrar;

/**
 * Created by Can on 22.02.2015.
 */
public class ActivitySettings extends Activity{
    CheckBox cb_bildirim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cb_bildirim=(CheckBox) findViewById(R.id.checkBox_bildirim);
        if (GCMRegistrar.isRegistered(ActivitySettings.this)){
            cb_bildirim.setChecked(true);
        }else cb_bildirim.setChecked(false);
        cb_bildirim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb_bildirim.isChecked()){
                    AppServices.loginAndRegisterForPush(ActivitySettings.this);
                }else{
                    GCMRegistrar.unregister(ActivitySettings.this);
                }
            }
        });
    }
}
