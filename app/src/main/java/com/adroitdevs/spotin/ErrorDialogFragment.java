package com.adroitdevs.spotin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Buat dan tampilkan pesan kesalahan kepada pengguna melalui Dialog. Termasuk opsi untuk
 * menyelesaikan Activity jika terjadi kesalahan pada aplikasi yang perlu dibangun kembali agar berfungsi.
 */
public class ErrorDialogFragment extends DialogFragment {

    /**
     * Konstruktor ErrorDialogFragment.
     *
     * @param title       Judul Pemberitahuan, yang diacu oleh id sumbernya.
     * @param message     Isi pesan pada dialog Alert.
     * @param canContinue Apakah aplikasi bisa berlanjut tanpa perlu dibangun kembali.
     * @return ErrorDialogFragment yang dibangun.
     */
    public static ErrorDialogFragment newInstance(int title, String message, boolean canContinue) {
        ErrorDialogFragment frag = new ErrorDialogFragment();
        Bundle args = new Bundle();

        args.putInt("title", title);
        args.putString("message", message);
        args.putBoolean("canContinue", canContinue);

        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        String message = getArguments().getString("message");
        boolean canContinue = getArguments().getBoolean("canContinue");

        // Jika aplikasi masih memiliki beberapa fungsi, biarkan pengguna mengabaikan dialog.
        if (canContinue) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Tidak melakukan apapun dan mengabaikan dialognya.
                                }
                            }
                    )
                    .create();
        } else {
            // Jika aplikasi harus dibangun kembali untuk pekerjaan apa pun, kita akan menyelesaikan Aktivitas.
            AlertDialog errorDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setNeutralButton(R.string.alert_dialog_close,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    getActivity().finish();
                                }
                            }
                    )
                    .create();
            errorDialog.setCanceledOnTouchOutside(false);
            return errorDialog;
        }
    }
}