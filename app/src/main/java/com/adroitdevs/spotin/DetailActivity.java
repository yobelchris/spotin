package com.adroitdevs.spotin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    public String tipe = "";
    public int budget = 0;
    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        formatRp.setCurrencySymbol("Rp");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ArrayList<String> dataDetail = getIntent().getStringArrayListExtra("detail");
        setTitle(dataDetail.get(0));
        ImageView imageView = (ImageView) findViewById(R.id.imageFoto);
        Glide.with(this).load(dataDetail.get(4)).into(imageView);
        TextView deskripsi = (TextView) findViewById(R.id.deskripsi);
        deskripsi.setText(dataDetail.get(3));
        TextView tarif = (TextView) findViewById(R.id.tarif);
        tarif.setText(dataDetail.get(1) + " " + dataDetail.get(8));
        TextView lokasi = (TextView) findViewById(R.id.lokasi);
        lokasi.setText(dataDetail.get(2));
        TextView kontak = (TextView) findViewById(R.id.call);
        kontak.setText(dataDetail.get(6));
        TextView budgetView = (TextView) findViewById(R.id.budget);
        budgetView.setText(kursIndonesia.format(Integer.parseInt(dataDetail.get(9))));
        TextView sisa = (TextView) findViewById(R.id.sisa);
        sisa.setText(kursIndonesia.format(Integer.parseInt(dataDetail.get(9)) - Integer.parseInt(dataDetail.get(7))));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataDetail.get(5) != "") {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + dataDetail.get(5));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } else
                    Toast.makeText(DetailActivity.this, "Lokasi tidak ada di google map", Toast.LENGTH_SHORT).show();
            }
        });
        tipe = dataDetail.get(8);
        budget = Integer.parseInt(dataDetail.get(9));
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("tipe", tipe);
        intent.putExtra("back", true);
        intent.putExtra("budget", budget);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("tipe", tipe);
        intent.putExtra("back", true);
        intent.putExtra("budget", budget);
        startActivity(intent);
    }
}
