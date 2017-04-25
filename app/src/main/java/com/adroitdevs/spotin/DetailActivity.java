package com.adroitdevs.spotin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Task task = (Task) getIntent().getSerializableExtra("detail");*/
        ArrayList<String> dataDetail = getIntent().getStringArrayListExtra("detail");
        setTitle(dataDetail.get(0));
        ImageView imageView = (ImageView) findViewById(R.id.imageFoto);
        Glide.with(this).load(dataDetail.get(4)).into(imageView);
        TextView deskripsi = (TextView) findViewById(R.id.deskripsi);
        deskripsi.setText(dataDetail.get(3));
        TextView tarif = (TextView) findViewById(R.id.tarif);
        tarif.setText(dataDetail.get(1));
        TextView lokasi = (TextView) findViewById(R.id.lokasi);
        lokasi.setText(dataDetail.get(2));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
