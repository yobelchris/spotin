package com.adroitdevs.spotin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom List Adapter untuk menjembatani kesenjangan antara tampilan daftar visual dan datastore Cloudant
 */
class TaskAdapter extends BaseAdapter implements ListAdapter {
    private final Context context;
    private final List<Task> tasks;
    ITaskAdapter mITaskAdapter;
    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

    TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        mITaskAdapter = (ITaskAdapter) context;
    }

    @Override
    public int getCount() {
        return this.tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return this.tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return Adapter.IGNORE_ITEM_VIEW_TYPE;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }

        TextView namaTempat = (TextView) convertView.findViewById(R.id.textViewJudul);
        TextView tarif = (TextView) convertView.findViewById(R.id.textViewHarga);
        TextView lokasi = (TextView) convertView.findViewById(R.id.textViewDeskripsi);
        ImageView gambarLokasi = (ImageView) convertView.findViewById(R.id.imageView);
        ImageButton imageButtonCall = (ImageButton) convertView.findViewById(R.id.buttonCall);
        ImageButton imageButtonMap = (ImageButton) convertView.findViewById(R.id.buttonMap);
        ImageButton imageButtonShare = (ImageButton) convertView.findViewById(R.id.buttonFavorite);

        formatRp.setCurrencySymbol("Rp");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        final Task t = this.tasks.get(position);
        String harga = "";
        String input = String.valueOf(t.getHarga());
        if (TextUtils.isDigitsOnly(input)) {
            if (Integer.parseInt(input) == 0)
                harga = "GRATIS";
            else
                harga = String.valueOf(kursIndonesia.format(Integer.parseInt(t.getHarga())));
        } else {
            harga = "Format data tidak valid";
        }

        namaTempat.setText(t.getJudul());
        tarif.setText(harga);
        lokasi.setText(t.getLokasi());
        Glide.with(context).load(t.getGambar()).into(gambarLokasi);
        final ArrayList<String> dataDetail = new ArrayList<>();
        dataDetail.add(0, t.getJudul());
        dataDetail.add(1, harga);
        dataDetail.add(2, t.getLokasi());
        dataDetail.add(3, t.getDeskripsi());
        dataDetail.add(4, t.getGambar());
        dataDetail.add(5, t.getKoordinat());
        dataDetail.add(6, t.getTelepon());
        dataDetail.add(7, t.getHarga());

        imageButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + dataDetail.get(5));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

        imageButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dataDetail.get(6)));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Tidak ada aplikasi untuk menelfon", Toast.LENGTH_SHORT).show();
                } else {
                    context.startActivity(intent);
                }
            }
        });

        imageButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, dataDetail.get(0));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, dataDetail.get(3));
                context.startActivity(Intent.createChooser(sharingIntent, "Bagikan tempat ini"));
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mITaskAdapter.detail(dataDetail);
            }
        });

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    /**
     * Letakkan Task pada posisi yang ditentukan
     */
    void set(int position, Task t) {
        this.tasks.set(position, t);
        this.notifyDataSetChanged();
    }

    /**
     * Menghapus Task pada posisi tertentu
     */
    void remove(int position) {
        this.tasks.remove(position);
    }

    public interface ITaskAdapter {
        void detail(ArrayList<String> detailData);
    }
}