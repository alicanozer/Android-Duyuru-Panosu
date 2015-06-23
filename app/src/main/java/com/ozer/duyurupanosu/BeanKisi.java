package com.ozer.duyurupanosu;

/**
 * Created by Can on 28.01.2015.
 */
public class BeanKisi {
    private String mUserId;
    private String mAdisoyadi;
    private String mKullaniciadi;
    private String mParola;
    private String mMail;
    private String mSoru;
    private String mCevap;

    public BeanKisi(String id, String nick, String name, String pass) {
        this.mUserId = id;
        this.mAdisoyadi = name;
        this.mParola = pass;
        this.mKullaniciadi = nick;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getmAdisoyadi() {
        return mAdisoyadi;
    }

    public void setmAdisoyadi(String mAdisoyadi) {
        this.mAdisoyadi = mAdisoyadi;
    }

    public String getmKullaniciadi() {
        return mKullaniciadi;
    }

    public void setmKullaniciadi(String mKullaniciadi) {
        this.mKullaniciadi = mKullaniciadi;
    }

    public String getmParola() {
        return mParola;
    }

    public void setmParola(String mParola) {
        this.mParola = mParola;
    }

    public String getmMail() {
        return mMail;
    }

    public void setmMail(String mMail) {
        this.mMail = mMail;
    }

    public String getmSoru() {
        return mSoru;
    }

    public void setmSoru(String mSoru) {
        this.mSoru = mSoru;
    }

    public String getmCevap() {
        return mCevap;
    }

    public void setmCevap(String mCevap) {
        this.mCevap = mCevap;
    }
}

