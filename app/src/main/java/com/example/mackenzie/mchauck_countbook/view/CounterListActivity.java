package com.example.mackenzie.mchauck_countbook.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mackenzie.mchauck_countbook.R;
import com.example.mackenzie.mchauck_countbook.controller.Controller;
import com.example.mackenzie.mchauck_countbook.data.Counter;
import com.example.mackenzie.mchauck_countbook.data.CounterTooSmall;
import com.example.mackenzie.mchauck_countbook.data.TestCounterSource;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CounterListActivity extends AppCompatActivity implements CounterViewInterface {
    // follow some example code from: https://www.androidhive.info/2016/01/android-working-with-recycler-view/
    // 2017-09-28

    private List<Counter> counterList = new ArrayList<>();

    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;

    private Controller controller;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.counter_recycler_view);
        layoutInflater = getLayoutInflater();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CounterListActivity.this, CounterAddActivity.class);
                CounterListActivity.this.startActivityForResult(intent, 2);
            }
        });

        controller = new Controller(this, new TestCounterSource());
    }


    @Override
    public void startEditActivity(Counter counter, View viewRoot) {

    }

    @Override
    public void setupAdapterAndView(List<Counter> counters) {
        this.counterList = counters;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new CustomAdapter();
        recyclerView.setAdapter(adapter);

        // disable the animation when the counter changes, to increase responsiveness
        // from: https://stackoverflow.com/questions/31897469/override-animation-for-notifyitemchanged-in-recyclerview-adapter
        // 2017-09-29
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

    }

    @Override
    public void addNewCounterToView(Counter counter) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Gson gson = new Gson();
        if (requestCode == 2) {
            // adding a new counter
            Log.d("INFO", "returned to main activity to add a new counter");
            Counter counter = gson.fromJson(data.getStringExtra("counter"), Counter.class);
            counterList.add(counter);
            adapter.notifyItemInserted(counterList.size() - 1);
            recyclerView.smoothScrollToPosition(counterList.size() - 1);
        } else if (requestCode == 3) {
            // editing an existing counter
            int modifiedCounterPosition = data.getIntExtra("position", -1);
            if (modifiedCounterPosition == -1) {
                // couldn't read position value...
                return;
            }

            Counter counter = gson.fromJson(data.getStringExtra("counter"), Counter.class);
            counterList.set(modifiedCounterPosition, counter);
            adapter.notifyItemChanged(modifiedCounterPosition);
        }
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        @Override
        public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = layoutInflater.inflate(R.layout.counter_list_row, parent, false);
            return new CustomViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CustomAdapter.CustomViewHolder holder, final int position) {
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
                    // example from: https://stackoverflow.com/a/37186975/8001779
                    // 2017-09-29

                    // serialize our counter so we can send it to another activity
                    Gson gson = new Gson();

                    Intent editIntent = new Intent(v.getContext(), CounterAddActivity.class);

                    editIntent.putExtra("counter", gson.toJson(counter));
                    editIntent.putExtra("position", position);
                    startActivityForResult(editIntent, 3);
                }
            });

        }

        @Override
        public int getItemCount() {
            return counterList.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            private TextView name, comment, count, date;
            private Button increment, decrement, edit;

            public CustomViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.counter_name);
                comment = (TextView) itemView.findViewById(R.id.counter_comment);
                count = (TextView) itemView.findViewById(R.id.counter_count);
                date = (TextView) itemView.findViewById(R.id.counter_date);

                increment = (Button) itemView.findViewById(R.id.increment_button);
                decrement = (Button) itemView.findViewById(R.id.decrement_button);
                edit = (Button) itemView.findViewById(R.id.edit_button);
            }
        }
    }
}
