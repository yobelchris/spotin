package com.adroitdevs.spotin;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cloudant.sync.datastore.ConflictException;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JurnalActivity extends AppCompatActivity {

    static final String LOG_TAG = "JurnalActivity";
    public JurnalAdapter mTaskAdapter;
    ActionMode mActionMode = null; // Holder untuk interaksi action bar ketika di klik.
    // Main data model objek.
    private JurnalModel sTasks;
    private ListView listView;
    // Action mode menangani interaksi action bar (tombol update / delete) yang dibuat saat list item ditekan.
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteTaskAt(listView.getCheckedItemPosition());
                    mode.finish();
                    return true;
                case R.id.action_update:
                    showTaskDialog(R.string.update_task, listView.getCheckedItemPosition());
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            listView.setItemChecked(listView.getCheckedItemPosition(), false);
            mActionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jurnal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Melindungi penciptaan statis TasksModel tunggal.
        if (sTasks == null) {
            // Model harus tetap ada selama masa pakai aplikasi.
            sTasks = new JurnalModel(this.getApplicationContext());
        }

        // Daftarkan kegiatan ini sebagai pendengar untuk update replikasi ketika aktif.
        sTasks.setReplicationListener(this);

        // Membuat tombol untuk membuat tugas baru.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fabHome = (FloatingActionButton) findViewById(R.id.fabHome);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTaskDialog(R.string.new_task, -1);
                }
            });
        }
        if (fabHome != null) {
            fabHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(JurnalActivity.this, MainActivity.class);
                    intent.putExtra("back", true);
                    startActivity(intent);
                }
            });
        }

        listView = (ListView) findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long lon) {

                if (mActionMode != null) {
                    mActionMode.finish();
                }

                // Buat item yang baru diklik yang saat ini dipilih.
                listView.setItemChecked(position, true);
                mActionMode = listView.startActionMode(mActionModeCallback);
            }
        });

        // Core SDK harus diinisialisasi untuk berinteraksi dengan layanan Bluemix Mobile.
        BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_US_SOUTH);

        // Muat Cloudant task dari model.
        this.reloadTasksFromModel();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();

        // Hapus referensi kita sebagai pendengar.
        sTasks.setReplicationListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        /*showProgressDialog(R.string.action_download, "Syncing with Cloudant");*/
        /*sTasks.startPullReplication();*/
    }

    // Membuat menu replikasi / pengaturan default di title bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Mengembangkan menu; Ini menambahkan item ke action bar jika ada.
        getMenuInflater().inflate(R.menu.todo, menu);
        return true;
    }

    // Menangani interaksi dengan menu dropdown di pojok.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Menangani item yang dipilih di dropdown di pojok
        switch (item.getItemId()) {
            case R.id.action_download:
                showProgressDialog(R.string.action_download, "Pulling changes from Cloudant");
                sTasks.startPullReplication();
                return true;
            case R.id.action_upload:
                showProgressDialog(R.string.action_upload, "Pushing Changes to Cloudant");
                sTasks.startPushReplication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Buat task baru dan tambahkan ke daftar datastore Cloudant dan daftar adaptor lokal.
    public void createNewTask(String desc) {
        Jurnal t = new Jurnal(desc);
        sTasks.createDocument(t);
        reloadTasksFromModel();
    }

    // Mengkopikan Local Cloudant Store ke dalam Array List untuk adaptor untuk menerjemahkan Tasks ke List View.
    private void reloadTasksFromModel() {

        List<Jurnal> tasks = sTasks.allTasks();

        // Urutkan list untuk menunjukkan urutan abjad, dari atas ke bawah.
        Collections.sort(tasks, new Comparator<Jurnal>() {
            @Override
            public int compare(Jurnal task1, Jurnal task2) {
                return task1.getDescription().compareToIgnoreCase(task2.getDescription());
            }
        });

        this.mTaskAdapter = new JurnalAdapter(this, tasks);
        listView.setAdapter(this.mTaskAdapter);
    }

    // Update task di lokasi yang diberikan saat update dikonfirmasi.
    public void updateTaskAt(int position, String description) {
        try {
            Jurnal t = (Jurnal) mTaskAdapter.getItem(position);
            t.setDescription(description);
            sTasks.updateDocument(t);
            reloadTasksFromModel();
            Toast.makeText(JurnalActivity.this,
                    "Updated item : " + t.getDescription(),
                    Toast.LENGTH_SHORT).show();
        } catch (ConflictException e) {
            throw new RuntimeException(e);
        }
    }

    // Onclick fungsi kotak centang, mirip dengan updateTaskAt tapi tanpa panggilan full reloadTaskFromModel
    public void onCompleteCheckboxClicked(View view) {
        try {
            Jurnal t = (Jurnal) mTaskAdapter.getItem(view.getId());
            t.setCompleted(!t.isCompleted());
            t = sTasks.updateDocument(t);
            mTaskAdapter.set(view.getId(), t);
        } catch (ConflictException e) {
            throw new RuntimeException(e);
        }
    }

    // Hapus task di lokasi yang diberikan saat hapus ditekan.
    private void deleteTaskAt(int position) {
        try {
            Jurnal t = (Jurnal) mTaskAdapter.getItem(position);
            sTasks.deleteDocument(t);
            mTaskAdapter.remove(position);
            Toast.makeText(JurnalActivity.this,
                    "Deleted item : " + t.getDescription(),
                    Toast.LENGTH_SHORT).show();
        } catch (ConflictException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Menampilkan ErrorDialog dengan rincian kesalahan dan resolusi.
     * <p>
     *  * @param errorTitle Error Judul dari nilai / strings.xml.
     *  * @param errorMessage Error Message baik dari nilai / strings.xml atau respon dari server.
     *  * @param canContinue Apakah aplikasi bisa berlanjut tanpa perlu dibangun kembali.
     */
    public void showErrorDialog(int errorTitle, String errorMessage, boolean canContinue) {
        DialogFragment newErrorFragment = ErrorDialogFragment.newInstance(errorTitle, errorMessage, canContinue);
        newErrorFragment.show(getFragmentManager(), "error");
    }

    /**
     * Menampilkan NewTaskDialog dengan opsi untuk mengatur / mengedit deskripsi string task.
     *
     * @param title        Dialog judul banner.
     * @param taskPosition Digunakan untuk mencari dan memperbarui tugas yang ada, akan menjadi -1 untuk membuat.
     */
    public void showTaskDialog(int title, int taskPosition) {
        DialogFragment newTaskFragment = TaskDialogFragment.newInstance(title, taskPosition);
        newTaskFragment.show(getFragmentManager(), "task");
    }

    /**
     * Menampilkan progress spinner saat menyinkronkan datastores lokal dan remote.
     *
     * @param title   Dialog judul banner (Upload/Download).
     * @param message Menginformasian pengguna apa yang terjadi (Mengirim/Mengambil data).
     */
    public void showProgressDialog(int title, String message) {
        DialogFragment newProgressFragment = ProgressDialogFragment.newInstance(title, message);
        newProgressFragment.show(getFragmentManager(), "progress");
    }

    /**
     * Tutup dialog setelah proses selesai.
     */
    public void dismissDialog() {
        ((DialogFragment) getFragmentManager().findFragmentByTag("progress")).dismiss();
    }

    // Dipanggil saat pengguna memutuskan untuk menghentikan push / pull pada saat replikasi.
    void stopReplication() {
        sTasks.stopAllReplications();
        dismissDialog();
        mTaskAdapter.notifyDataSetChanged();
    }

    /**
     * Dipanggil oleh TasksModel saat menerima replikasi callback lengkap.
     * TasksModel menangani panggilan ini di thread utama.
     */
    void replicationComplete() {
        reloadTasksFromModel();
        Toast.makeText(getApplicationContext(),
                R.string.replication_completed,
                Toast.LENGTH_LONG).show();
        dismissDialog();
    }

    /**
     * Dipanggil oleh TasksModel saat menerima replikasi error callback.
     * TasksModel menangani panggilan ini di thread utama.
     */
    void replicationError() {
        Log.i(LOG_TAG, getString(R.string.replication_error));
        reloadTasksFromModel();
        dismissDialog();
        showErrorDialog(R.string.replication_failed, getString(R.string.replication_error), false);
    }
}
