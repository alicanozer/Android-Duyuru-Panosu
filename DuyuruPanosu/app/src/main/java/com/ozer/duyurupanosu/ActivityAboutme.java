package com.ozer.duyurupanosu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.ozer.duyurupanosu.R;

/**
 * Created by Can on 02.02.2015.
 */
public class ActivityAboutme extends ActionBarActivity implements View.OnClickListener {
    Button logo,facebook, twitter, linkedin, github, google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutme);

        logo = (Button) findViewById(R.id.bt_logo);
        facebook = (Button) findViewById(R.id.bt_facebook);
        twitter = (Button) findViewById(R.id.bt_twitter);
        linkedin = (Button) findViewById(R.id.bt_linkedin);
        github = (Button) findViewById(R.id.bt_github);
        google = (Button) findViewById(R.id.bt_google);

        logo.setOnClickListener(this);
        facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);
        linkedin.setOnClickListener(this);
        github.setOnClickListener(this);
        google.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == logo) {
            Uri link = Uri.parse("http://www.gyte.edu.tr");
            Intent tara = new Intent(Intent.ACTION_DEFAULT, link);
            startActivity(tara);
        } else if (v == facebook) {
            Uri link = Uri.parse("https://www.facebook.com/tokatli001");
            Intent tara = new Intent(Intent.ACTION_DEFAULT, link);
            startActivity(tara);

        } else if (v == twitter) {
            Uri link = Uri.parse("https://twitter.com/AlicanOZZER");
            Intent tara = new Intent(Intent.ACTION_DEFAULT, link);
            startActivity(tara);

        } else if (v == linkedin) {
            Uri link = Uri.parse("https://www.linkedin.com/in/alicanozer60");
            Intent tara = new Intent(Intent.ACTION_DEFAULT, link);
            startActivity(tara);

        } else if (v == github) {
            Uri link = Uri.parse("https://github.com/alicanozer");
            Intent tara = new Intent(Intent.ACTION_DEFAULT, link);
            startActivity(tara);

        } else if (v == google) {
            Uri link = Uri.parse("https://plus.google.com/116796527884018084618");
            Intent tara = new Intent(Intent.ACTION_DEFAULT, link);
            startActivity(tara);

        }

    }
}
