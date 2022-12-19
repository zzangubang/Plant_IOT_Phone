package com.example.plant_iot_phone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

public class PlantItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperListener listener;

    public PlantItemTouchHelperCallback(ItemTouchHelperListener listener) {
        this.listener = listener;
    }

    public interface ItemTouchHelperListener {
        boolean onItemMove(int from_position, int to_position) throws JSONException;

        void onItemSwipe(int position) throws JSONException;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int drag_flags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipe_flags = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(drag_flags, swipe_flags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        try {
            return listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        try {
            listener.onItemSwipe(viewHolder.getAdapterPosition());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
