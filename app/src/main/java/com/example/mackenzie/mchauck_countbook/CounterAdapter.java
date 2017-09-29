package com.example.mackenzie.mchauck_countbook;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        public TextView name, comment, count, date;
        public Button increment, decrement, edit;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.counter_name);
            comment = (TextView) view.findViewById(R.id.counter_comment);
            count = (TextView) view.findViewById(R.id.counter_count);
            date = (TextView) view.findViewById(R.id.counter_date);

            increment = (Button) view.findViewById(R.id.increment_button);
            decrement = (Button) view.findViewById(R.id.decrement_button);
            edit = (Button) view.findViewById(R.id.edit_button);
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Counter counter = counterList.get(position);
        holder.name.setText(counter.getName());
        holder.comment.setText(counter.getComment());
        holder.count.setText(String.valueOf(counter.getCurrentValue()));
        holder.date.setText(counter.getDate().toString());

        holder.increment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("INFO", "increment btn");
                counter.increment();
                notifyItemChanged(position);
            }
        });

        holder.decrement.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("INFO", "decrement btn");
                try {
                    counter.decrement();
                    notifyItemChanged(position);
                } catch (CounterTooSmall e) {
                    Snackbar.make(v, "Counter can not be less than zero", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // go to the edit action...
                //Intent i = new Intent(CounterListActivity.class, CounterEditActivity.class);

            }
        });


    }

    @Override
    public int getItemCount() {
        return counterList.size();
    }
}
