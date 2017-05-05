package id.sch.smktelkom_mlg.project2.xirpl109103235.spotin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Menangani pembuatan fragmen dialog untuk interaksi Task (Create dan Update).
 */
public class TaskDialogFragment extends DialogFragment {

    /**
     * Konstruktor TaskDialogFragment.
     *
     * @param title        TJudul Pemberitahuan, yang diacu oleh id sumbernya.
     * @param taskPosition Memungkinkan dialog mengetahui tugas mana yang perlu diperbarui. Akan menjadi -1 saat membuat tugas baru.
     * @return TaskDialogFragment yang dibangun.
     */
    public static TaskDialogFragment newInstance(int title, int taskPosition) {
        TaskDialogFragment frag = new TaskDialogFragment();
        Bundle args = new Bundle();

        args.putInt("title", title);
        args.putInt("taskPosition", taskPosition);

        frag.setArguments(args);
        return frag;
    }

    // Buat dialog Task dengan sifat dan interaksi yang sesuai; Update vs Create.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        final int taskPosition = getArguments().getInt("taskPosition");

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_task, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title);
        final EditText description = (EditText) v.findViewById(R.id.new_task_desc);

        if (taskPosition >= 0) {
            description.setHint(((Jurnal) ((JurnalActivity) getActivity()).mTaskAdapter.getItem(taskPosition)).getDescription());

            // Cek apakah ada deskripsi, jika ada tambahkan tugas, jika tidak menunjukkan kesalahan.
            DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if (description.getText().length() > 0) {
                        ((JurnalActivity) getActivity()).updateTaskAt(taskPosition, description.getText().toString());
                        description.getText().clear();
                    } else {
                        Toast.makeText(getActivity(),
                                R.string.task_not_updated,
                                Toast.LENGTH_LONG).show();
                    }
                }
            };

            builder.setView(v).setPositiveButton(R.string.update, positiveClick);
        } else {
            DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if (description.getText().length() > 0) {
                        ((JurnalActivity) getActivity()).createNewTask(description.getText().toString());
                        description.getText().clear();
                    } else {
                        Toast.makeText(getActivity(),
                                R.string.task_not_created,
                                Toast.LENGTH_LONG).show();
                    }
                }
            };

            builder.setView(v).setPositiveButton(R.string.create, positiveClick);
        }

        // Buat listener onClick untuk tombol batal.
        DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        };
        builder.setNegativeButton(R.string.cancel, negativeClick);

        final AlertDialog taskDialog = builder.create();

        // Aktifkan tombol "Create" / "Update" bila deskripsi memiliki beberapa karakter.
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Button b = taskDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setEnabled(description.getText().length() > 0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        // Mengatur listener untuk tombol create yang dikonfigurasi di atas.
        taskDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = taskDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setEnabled(description.getText().length() > 0);
                description.addTextChangedListener(textWatcher);
            }
        });

        return taskDialog;
    }
}