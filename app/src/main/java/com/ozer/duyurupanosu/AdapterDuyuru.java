package com.ozer.duyurupanosu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Can on 28.01.2015.
 */
public class AdapterDuyuru extends ArrayAdapter<BeanDuyuru> {

    Context context;
    int layoutResourceId;
    ArrayList<BeanDuyuru> Duyurular = null;

    public AdapterDuyuru(Context context, int resource, ArrayList<BeanDuyuru> duyuru) {
        super(context, resource, duyuru);
        this.context = context;
        this.layoutResourceId = resource;
        this.Duyurular = duyuru;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        duyuruViewHolder holder = null;

        if (row == null) {

            LayoutInflater inflater = LayoutInflater.from(context);

            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new duyuruViewHolder();

            //  holder.img = (ImageView) row.findViewById(R.id.imageView1);
            holder.baslik = (TextView) row.findViewById(R.id.tv_baslik);
            holder.detay = (TextView) row.findViewById(R.id.tv_icerik);
            holder.tarih = (TextView) row.findViewById(R.id.tv_tarih);

            row.setTag(holder);
        } else {
            holder = (duyuruViewHolder) row.getTag();
        }

        BeanDuyuru dBilgi = Duyurular.get(position);
        //  holder.img.setImageResource(R.drawable.abc_ic_menu_paste_mtrl_am_alpha);
        String icerik = dBilgi.getBaslik();
        if (icerik != null && !icerik.matches("")) {
            if (icerik.length() > 25)
                holder.baslik.setText(icerik.substring(0, 25));
            else holder.baslik.setText(icerik);
        }

        holder.detay.setText(dBilgi.getSahipId());
        holder.tarih.setText(dBilgi.getZamani());
        return row;
    }

    static class duyuruViewHolder {
        ImageView img;
        TextView baslik;
        TextView detay;
        TextView tarih;
    }
}

