package com.multiplies.multiring.ringtoneItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.multiplies.multiring.activity.RingtonesActivity;
import com.multiplies.multiring.R;

import java.util.ArrayList;
import java.util.List;

public class RingtoneItemAdabter extends RecyclerView.Adapter<RingtoneItemAdabter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<RingtoneItem> list;
    private ActionMode.Callback callback;
    private ActionMode actionMode;
    private List<RingtoneItem> selectedItems = new ArrayList<>();
    private TypedValue typedValue = new TypedValue();
    private RingtoneItem currentPlaying = null;
    private ImageView currentPlayingImage = null;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Switch aSwitch;
        public TextView textView;
        public ConstraintLayout constraintLayout;
        public View view;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            aSwitch = (Switch) view.findViewById(R.id.ringtone_state_switch);
            textView = (TextView) view.findViewById(R.id.ringtone_title_textview);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.item_constraint_layout);
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.play_button);
        }
    }

    public RingtoneItemAdabter(final Context context, final Activity activity, List<RingtoneItem> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
        callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.actionmode_menu, menu);

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
                        deleteSelectedItems();
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                selectedItems.clear();
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RingtoneItem item = list.get(position);

        holder.textView.setText(item.getTitle());
        holder.aSwitch.setChecked(item.isActive());
        holder.textView.setTextColor(item.isActive() ? Color.BLACK : Color.GRAY);
        holder.constraintLayout.setBackgroundResource(typedValue.resourceId);
        holder.view.setBackgroundColor(Color.WHITE);
        holder.aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Switch switchAction = (Switch) RingtonesActivity.switchItem.getActionView();
            if (isChecked) {
                holder.textView.setTextColor(Color.BLACK);
                item.setActive(true);
                switchAction.setChecked(true);
            } else {
                holder.textView.setTextColor(Color.GRAY);
                item.setActive(false);
                if (!checkActive()) {
                    switchAction.setChecked(false);
                }
            }
            RingtonesActivity.writeToFile(context);
        });
        holder.constraintLayout.setOnLongClickListener(v -> {
            if (actionMode != null) {
                return false;
            } else {
                actionMode = activity.startActionMode(callback);
                holder.view.setBackgroundColor(Color.rgb(77, 182, 172));
                holder.constraintLayout.setBackgroundColor(Color.rgb(224, 224, 224));
                selectedItems.add(item);
                actionMode.setTitle(String.valueOf(selectedItems.size()));
                return true;
            }
        });
        holder.constraintLayout.setOnClickListener(v -> {
            if (actionMode != null) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    holder.view.setBackgroundColor(Color.WHITE);
                    holder.constraintLayout.setBackgroundResource(typedValue.resourceId);
                    actionMode.setTitle(String.valueOf(selectedItems.size()));
                    if (selectedItems.size() == 0) {
                        actionMode.finish();
                    }
                } else {
                    holder.view.setBackgroundColor(Color.rgb(77, 182, 172));
                    holder.constraintLayout.setBackgroundColor(Color.rgb(224, 224, 224));
                    selectedItems.add(item);
                    actionMode.setTitle(String.valueOf(selectedItems.size()));
                }
            } else {
                holder.aSwitch.setChecked(!holder.aSwitch.isChecked());
            }
        });
        holder.imageView.setOnClickListener(v -> {
            if (item.getRingtone().isPlaying()) {
                item.getRingtone().stop();
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.play_button));
            } else {
                if(currentPlaying != null && currentPlaying.getRingtone().isPlaying()){
                    currentPlaying.getRingtone().stop();
                    currentPlaying = null;
                    currentPlayingImage.setImageDrawable(context.getResources().getDrawable(R.drawable.play_button));
                    currentPlayingImage = null;
                }
                item.getRingtone().play();
                currentPlaying = item;
                currentPlayingImage = holder.imageView;
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.stop_button));
            }
        });
    }

    private boolean checkActive() {
        for (RingtoneItem ringtoneItem : list) {
            if (ringtoneItem.isActive()) {
                return true;
            }
        }
        return false;
    }

    private void deleteSelectedItems() {
        for (RingtoneItem ringtoneItem : selectedItems) {
            int index = list.indexOf(ringtoneItem);
            list.remove(ringtoneItem);
            RingtonesActivity.list.remove(ringtoneItem);
            notifyItemRemoved(index);
        }
        RingtonesActivity.writeToFile(context);
        selectedItems.clear();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private boolean isPlaying(){
        for (RingtoneItem ringtoneItem: list) {
            if (ringtoneItem.getRingtone().isPlaying()){
                return true;
            }
        }
        return false;
    }


}
