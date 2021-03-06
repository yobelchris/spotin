package id.sch.smktelkom_mlg.project2.xirpl109103235.spotin;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DatastoreNotCreatedException;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.event.Subscribe;
import com.cloudant.sync.notifications.ReplicationCompleted;
import com.cloudant.sync.notifications.ReplicationErrored;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * Menangani pembuatan datastore Cloudant (s) dan replikasi jarak jauh.
 * Di sinilah sebagian besar kode Cloudant store tinggal.
 * Menunjukkan cara membuat, mereplikasi, dan melakukan semua operasi CRUD di berbagai Cloudant datastores remote dan lokal.
 */
class TasksModel {

    private static final String LOG_TAG = "TasksModel";

    private static final String DATASTORE_MANGER_DIR = "data";
    private static final String TASKS_DATASTORE_NAME = "tasks";
    private final Context mContext;
    private final Handler mHandler;
    // Datastore lokal untuk tugas CRUD dan update dengan datastore Bluemix remote.
    private Datastore mDatastore;
    // Replikator digunakan untuk Push dan / atau Pull data ke dan / atau dari datastore remote pada Bluemix.
    private Replicator mPushReplicator;
    private Replicator mPullReplicator;
    private MainActivity mListener;

    TasksModel(Context context) {

        this.mContext = context;

        // Mengatur tugas datastore Cloudant lokal kita di dalam foldernya sendiri di direktori data aplikasi
        File path = this.mContext.getApplicationContext().getDir(
                DATASTORE_MANGER_DIR,
                Context.MODE_PRIVATE
        );
        DatastoreManager manager = DatastoreManager.getInstance(path.getAbsolutePath());
        try {
            this.mDatastore = manager.openDatastore(TASKS_DATASTORE_NAME);
        } catch (DatastoreNotCreatedException dnce) {
            Log.e(LOG_TAG, "Unable to open Datastore", dnce);
        }

        Log.d(LOG_TAG, "Set up database at " + path.getAbsolutePath());

        // Set up objek replikator dari setelan aplikasi.
        try {
            this.loadReplicationSettings();
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "Unable to construct remote URI from configuration", e);
        }

        // Mengizinkan kita untuk beralih kode yang disebut oleh ReplicationListener ke
        // thread utama sehingga UI dapat mengupdate dengan aman.
        this.mHandler = new Handler(Looper.getMainLooper());

        Log.d(LOG_TAG, "TasksModel set up " + path.getAbsolutePath());
    }

    /**
     * Mengatur listener untuk replikasi callback sebagai referensi lemah.
     *
     * @param listener {@link MainActivity} untuk menerima callback.
     */
    void setReplicationListener(MainActivity listener) {
        this.mListener = listener;
    }

    List<Task> allTasks() {
        int nDocs = this.mDatastore.getDocumentCount();
        List<DocumentRevision> all = this.mDatastore.getAllDocuments(0, nDocs, true);
        List<Task> tasks = new ArrayList<>();

        // Filter semua dokumen sampai ke tipe Task.
        for (DocumentRevision rev : all) {
            Task t = Task.fromRevision(rev);
            if (t != null) {
                tasks.add(t);
            }
        }

        return tasks;
    }

    /**
     * Berhenti menjalankan replikasi.
     *
     * Metode stop() menghentikan replikasi secara asinkron,
     * lihat dokumentasi replikator untuk informasi lebih lanjut.
     */
    void stopAllReplications() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.stop();
        }
        if (this.mPushReplicator != null) {
            this.mPushReplicator.stop();
        }
    }

    /**
     * Memulai replikasi push yang dikonfigurasi ke datastore jarak jauh Cloudant di Bluemix.
     */
    void startPushReplication() {
        if (this.mPushReplicator != null) {
            this.mPushReplicator.start();
        } else {
            throw new RuntimeException("Push replication not set up correctly");
        }
    }

    /**
     * Mulai replikasi pull yang dikonfigurasi dari datastore jarak jauh Cloudant di Bluemix.
     */
    void startPullReplication() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.start();
        } else {
            throw new RuntimeException("Push replication not set up correctly");
        }
    }

    /**
     * Memuat pengaturan replikasi dari cloudant_credentials.xml.
     */
    private void loadReplicationSettings() throws URISyntaxException {

        // Menyiapkan objek replikator.
        URI uri = this.createServerURI();

        mPullReplicator = ReplicatorBuilder.pull().to(mDatastore).from(uri).build();
        mPushReplicator = ReplicatorBuilder.push().from(mDatastore).to(uri).build();

        // Mendaftarkan kelas ini untuk mendengarkan events replikasi, fungsi lengkap dan error di bawah ini.
        mPushReplicator.getEventBus().register(this);
        mPullReplicator.getEventBus().register(this);

        Log.d(LOG_TAG, "Set up replicators for URI:" + uri.toString());
    }

    /**
     * Mengembalikan URI untuk basis data remote,
     * berdasarkan konfigurasi aplikasi.
     * <p>
     * URI dibuat berdasarkan kredensial pengguna Cloudant di cloudant_credentials.xml.
     *
     * @return URI database remote.
     * @throws URISyntaxException jika pengaturannya memberikan URI yang tidak valid.
     */
    private URI createServerURI() throws URISyntaxException {
        // Kami menyimpannya dalam teks biasa untuk keperluan demonstrasi sederhana,
        // Anda mungkin ingin menggunakan sesuatu yang lebih aman. Perlu mengenkripsi informasi ini.
        String username = mContext.getResources().getString(R.string.cloudant_username);
        String dbName = mContext.getResources().getString(R.string.cloudant_dbname);
        String apiKey = mContext.getResources().getString(R.string.cloudant_api_key);
        String apiSecret = mContext.getResources().getString(R.string.cloudant_api_password);
        String host = username + ".cloudant.com";

        // Sebaiknya kita selalu menggunakan HTTPS ketika berkoneksi dengan Cloudant.
        return new URI("https", apiKey + ":" + apiSecret, host, 443, "/" + dbName, null, null);
    }

    /**
     * Memanggil metode Replikasi MainActivityComplete pada thread utama,
     * karena callback yang complete() mungkin berasal dari thread replicator worker.
     */
    @Subscribe
    public void complete(ReplicationCompleted rc) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.replicationComplete();
                }
            }
        });
    }

    /**
     * Memanggil metode Replikasi MainActivityComplete pada thread utama,
     * karena error() callback mungkin akan berasal dari thread replicator worker.
     */
    @Subscribe
    public void error(ReplicationErrored re) {
        Log.e(LOG_TAG, "Replication error:", re.errorInfo.getException());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.replicationError();
                }
            }
        });
    }
}