package com.adroitdevs.spotin;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

/**
 * Custom List Adapter untuk menjembatani kesenjangan antara tampilan daftar visual dan datastore Cloudant
 */
class TaskAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private final List<Task> tasks;
    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

    TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }

        TextView namaTempat = (TextView) convertView.findViewById(R.id.textViewJudul);
        TextView tarif = (TextView) convertView.findViewById(R.id.textViewHarga);
        TextView lokasi = (TextView) convertView.findViewById(R.id.textViewDeskripsi);
        ImageView gambarLokasi = (ImageView) convertView.findViewById(R.id.imageView);

        /*TextView desc = (TextView) convertView.findViewById(R.id.task_description);
        CheckBox completed = (CheckBox) convertView.findViewById(R.id.checkbox_completed);*/
        formatRp.setCurrencySymbol("Rp");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        Task t = this.tasks.get(position);
        String harga = "";
        if (Integer.parseInt(String.valueOf(t.getHarga())) == 0)
            harga = "GRATIS";
        else
            harga = String.valueOf(kursIndonesia.format(Integer.parseInt(t.getHarga()))) + ",00";
        namaTempat.setText(t.getJudul());
        tarif.setText(harga);
        lokasi.setText(t.getLokasi());
        Glide.with(context).load(t.getGambar()).into(gambarLokasi);
        /*desc.setText(t.getJudul());
        completed.setChecked(t.isCompleted());
        completed.setId(position);*/

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
}