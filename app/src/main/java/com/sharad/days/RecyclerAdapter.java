package com.sharad.days;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharad.days.Event;
import com.sharad.days.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int mSelectedPos;
    private ArrayList<Event> mItemList;
    public RecyclerAdapter() {
        mItemList = new ArrayList<>();
        mSelectedPos = -1;
    }
    public ArrayList<Event> getItemList() { return mItemList; }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        return RecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;
        Event event = mItemList.get(position);
        holder.setTitle(event.get_title());
        holder.setDays("" + event.get_dayCount());
        holder.showButtons(position == mSelectedPos);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    public int getSelectedPos() { return mSelectedPos; }
    public void setSelectedPos(int pos) { mSelectedPos = pos; }
}