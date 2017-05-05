package id.sch.smktelkom_mlg.project2.xirpl109103235.spotin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Menunjukkan proses sinkronisasi Cloudant; Download dan Upload dialog.
 */
public class ProgressDialogFragment extends DialogFragment {

    /**
     * Konstruktor ProgressDialogFragment.
     *
     * @param title   Title of the Alert, yang diacu oleh id sumbernya.
     * @param message Isi pesan pada dialog Alert.
     * @return ProgressDialogFragment yang dibangun.
     */
    public static ProgressDialogFragment newInstance(int title, String message) {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        Bundle args = new Bundle();

        args.putInt("title", title);
        args.putString("message", message);

        frag.setArguments(args);
        return frag;
    }

    // Buat dialog proses dengan sifat dan interaksi yang sesuai; Upload vs Download
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int title = getArguments().getInt("title");
        String message = getArguments().getString("message");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message);

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_loading, null);

        // Buat listener onclick untuk tombol cancel agar berhenti melakukan replikasi.
        DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) getActivity()).stopReplication();
            }
        };

        // Buat keylistener untuk menunjukkan replikasi masih berjalan saat tombol back ditekan.
        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Toast.makeText(getActivity(),
                            R.string.replication_running, Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        };

        // Mengatur listener diatas ke dialog yang sedang dibuat.
        builder.setView(view).setNegativeButton("Stop", negativeClick).setOnKeyListener(keyListener);

        return builder.create();
    }
}