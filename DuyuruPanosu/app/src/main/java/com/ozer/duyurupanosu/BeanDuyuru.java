package com.ozer.duyurupanosu;

/**
 * Created by Can on 28.01.2015.
 */
public class BeanDuyuru {
    private String sahipId;
    private String icerik;
    private String zamani;
    private String baslik;
    private String uuid;

    public BeanDuyuru(String id, String baslik, String ic, String sahipid, String zaman) {
        this.baslik = baslik;
        this.icerik = ic;
        this.sahipId = sahipid;
        this.zamani = zaman;
        this.uuid = id;
    }

    public BeanDuyuru() {

    }

    public String getSahipId() {
        return sahipId;
    }

    public void setSahipId(String sahipId) {
        this.sahipId = sahipId;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public String getZamani() {
        return zamani;
    }

    public void setZamani(String zamani) {
        this.zamani = zamani;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

