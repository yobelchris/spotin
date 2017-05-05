package id.sch.smktelkom_mlg.project2.xirpl109103235.spotin;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhe on 24/04/2017.
 */

public class JurnalAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private final List<Jurnal> tasks;

    JurnalAdapter(Context context, List<Jurnal> tasks) {
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
            convertView = inflater.inflate(R.layout.task_item, parent, false);
        }

        TextView desc = (TextView) convertView.findViewById(R.id.task_description);
        CheckBox completed = (CheckBox) convertView.findViewById(R.id.checkbox_completed);

        Jurnal t = this.tasks.get(position);
        desc.setText(t.getDescription());
        completed.setChecked(t.isCompleted());
        completed.setId(position);

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
    void set(int position, Jurnal t) {
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
