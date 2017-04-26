package com.adroitdevs.spotin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TaskAdapter.ITaskAdapter {

    public TaskAdapter mTaskAdapter;
    public String tipe = "";
    public int budget = 0;
    int tarifuang = 0;
    TextView budgetView;
    int angkabudget = 0;
    int sisauang = 0;
    TextView sisa;
    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
    private TasksModel sTasks;
    private ListView listView;
    private String judulShare = "";
    private String kontenShare = "";

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
        if (sTasks == null)
            sTasks = new TasksModel(this.getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ArrayList<String> dataDetail = getIntent().getStringArrayListExtra("detail");
        setTitle(dataDetail.get(0));
        judulShare = dataDetail.get(0);
        ImageView imageView = (ImageView) findViewById(R.id.imageFoto);
        Glide.with(this).load(dataDetail.get(4)).into(imageView);
        TextView deskripsi = (TextView) findViewById(R.id.deskripsi);
        deskripsi.setText(dataDetail.get(3) + "\n\n" + "[All resource is originally by google, blog, and website.]");
        kontenShare = dataDetail.get(3);
        TextView tarif = (TextView) findViewById(R.id.tarif);
        tarif.setText(dataDetail.get(1));
        TextView lokasi = (TextView) findViewById(R.id.lokasi);
        lokasi.setText(dataDetail.get(2));
        TextView kontak = (TextView) findViewById(R.id.call);
        kontak.setText(dataDetail.get(6));
        budgetView = (TextView) findViewById(R.id.budget);
        budgetView.setText(kursIndonesia.format(Integer.parseInt(dataDetail.get(9))));
        sisauang = Integer.parseInt(dataDetail.get(9)) - Integer.parseInt(dataDetail.get(7));
        sisa = (TextView) findViewById(R.id.sisa);
        sisa.setText(kursIndonesia.format(sisauang));
        listView = (ListView) findViewById(R.id.listRec);
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
        tarifuang = Integer.parseInt(dataDetail.get(7));
        budget = Integer.parseInt(dataDetail.get(9));
        if (budget == 0)
            reloadTasksFromModel(tarifuang, dataDetail.get(0));
        else
            reloadTasksFromModel(sisauang, dataDetail.get(0));
        tarif.setFocusableInTouchMode(true);
        tarif.setFocusable(true);
        tarif.requestFocus();
        budgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBudget();
            }
        });
        kontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dataDetail.get(6)));
                if (ActivityCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DetailActivity.this, "Tidak ada aplikasi untuk menelfon", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(intent);
                }
            }
        });
    }

    private void showDialogBudget() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        final View view = this.getLayoutInflater().inflate(R.layout.dialog_search, null);
        final Spinner kota = (Spinner) view.findViewById(R.id.spinnerKota);
        final RadioGroup rgSearch = (RadioGroup) view.findViewById(R.id.butGroup);
        final EditText budget = (EditText) view.findViewById(R.id.textHarga);
        formatRp.setCurrencySymbol("Rp");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        budget.setHint("Masukkan budget Anda");
        budget.setInputType(InputType.TYPE_CLASS_NUMBER);
        kota.setVisibility(View.GONE);
        rgSearch.setVisibility(View.GONE);
        budget.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        builder.setView(view);
        builder.setTitle("Masukkan budget Anda");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                angkabudget = Integer.parseInt(budget.getText().toString());
                if (angkabudget != 0) {
                    setBudget(angkabudget);
                    budgetView.setText(kursIndonesia.format(angkabudget));
                    sisauang = angkabudget - tarifuang;
                    sisa.setText(kursIndonesia.format(sisauang));
                    Toast.makeText(DetailActivity.this, "Budget Anda " + String.valueOf(kursIndonesia.format(angkabudget)), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(DetailActivity.this, "Harap masukkan angka lebih dari nol", Toast.LENGTH_SHORT).show();
            }
        });
        final AlertDialog alt = builder.create();
        alt.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alt.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setEnabled(budget.getText().length() > 0 && budget.getText().length() <= 7);
                budget.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Button b = alt.getButton(DialogInterface.BUTTON_POSITIVE);
                        b.setEnabled(budget.getText().length() > 0 && budget.getText().length() <= 7);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
        alt.show();
    }

    void setBudget(int duik) {
        this.budget = duik;
    }

    private void reloadTasksFromModel(int sisBudg, String judul) {
        List<Task> tasks = sTasks.allTasks();
        int indexMin = 0;
        ArrayList<Integer> harg = new ArrayList<>();
        ArrayList<Integer> id = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            harg.add(Integer.parseInt(task.getHarga()));
        }
        for (int i = 0; i < harg.size(); i++) {
            indexMin = i;
            for (int j = i + 1; j < harg.size(); j++) {
                if (harg.get(j) > harg.get(indexMin)) {
                    indexMin = j;
                }
            }
            int temp = harg.get(i);
            id.add(indexMin);
            harg.set(i, harg.get(indexMin));
            harg.set(indexMin, temp);
        }
        int cek = 0;
        if (sisBudg > 0) {
            List<Task> task1 = new ArrayList<>();
            for (int idsen : id) {
                Task task = tasks.get(idsen);
                if (Integer.parseInt(task.getHarga()) <= sisBudg && cek < 5 && !task.getJudul().equals(judul)) {
                    task1.add(task);
                    cek++;
                }
            }
            /*for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if (Integer.parseInt(task.getHarga()) <= sisBudg && cek < 5 && !(task.getJudul().equals(judul))) {
                    task1.add(task);
                    cek++;
                }
            }*/
            this.mTaskAdapter = new TaskAdapter(this, task1);
            Toast.makeText(this, "Data berhasil di load", Toast.LENGTH_SHORT).show();
        } else {
            this.mTaskAdapter = new TaskAdapter(this, tasks);
            Toast.makeText(this, "Data gagal di load", Toast.LENGTH_SHORT).show();
        }
        listView.setAdapter(this.mTaskAdapter);

        // Urutkan list untuk menunjukkan urutan abjad, dari atas ke bawah.200
        /*Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task1.getJudul().compareToIgnoreCase(task2.getJudul());
            }
        });*/
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

    @Override
    public void detail(ArrayList<String> detailData) {
        detailData.add(8, tipe);
        detailData.add(9, String.valueOf(budget));
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("detail", detailData);
        this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bagi) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, judulShare);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, kontenShare);
            startActivity(Intent.createChooser(sharingIntent, "Bagikan tempat ini"));
        }

        return super.onOptionsItemSelected(item);
    }
}
