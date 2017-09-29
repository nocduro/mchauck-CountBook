package com.example.mackenzie.mchauck_countbook;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mackenzie on 2017-09-28.
 * Example code followed from: https://www.androidhive.info/2016/01/android-working-with-recycler-view/
 *
 * */

public class CounterAdapter extends RecyclerView.Adapter<CounterAdapter.MyViewHolder> {
    private List<Counter> counterList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, comment;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.counter_name);
            comment = (TextView) view.findViewById(R.id.counter_comment);
        }
    }

    public CounterAdapter(List<Counter> counterList) {
        this.counterList = counterList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.counter_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Counter counter = counterList.get(position);
        holder.name.setText(counter.getName());
        holder.comment.setText(counter.getComment());
    }

    @Override
    public int getItemCount() {
        return counterList.size();
    }
}
