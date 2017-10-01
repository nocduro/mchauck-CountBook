package com.example.mackenzie.mchauck_countbook.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mackenzie.mchauck_countbook.R;
import com.example.mackenzie.mchauck_countbook.data.Counter;
import com.example.mackenzie.mchauck_countbook.data.CounterTooSmall;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Tutorial partially followed from: https://github.com/BracketCove/RecyclerViewTutorial2017
 *
 */
public class CounterListActivity extends AppCompatActivity {
    // follow some example code from: https://www.androidhive.info/2016/01/android-working-with-recycler-view/
    // 2017-09-28

    private final String FILENAME = "counters.json";
    private final int EDIT_COUNTER_REQUEST_CODE = 1;
    private final int ADD_COUNTER_REQUEST_CODE = 2;

    private List<Counter> counterList = new ArrayList<>();

    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;

    private Gson gson;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gson = new Gson();

        recyclerView = (RecyclerView) findViewById(R.id.counter_recycler_view);
        layoutInflater = getLayoutInflater();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CounterListActivity.this, CounterAddActivity.class);
                startActivityForResult(intent, ADD_COUNTER_REQUEST_CODE);
            }
        });

        counterList = loadFromFile();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter();
        recyclerView.setAdapter(adapter);

        // disable the animation when the counter changes, to increase responsiveness
        // from: https://stackoverflow.com/questions/31897469/override-animation-for-notifyitemchanged-in-recyclerview-adapter
        // 2017-09-29
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private List<Counter> loadFromFile() {
        ArrayList<Counter> list;

        Type listType = new TypeToken<ArrayList<Counter>>(){}.getType();
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            list = gson.fromJson(in, listType);
        } catch (FileNotFoundException e) {
            list = new ArrayList<>();
        } catch (JsonIOException e) {
            list = new ArrayList<>();
        }

        return list;
    }

    private void saveToFile(List<Counter> counters) {
        Log.d("INFO", "saving to file");
        this.counterList = counters;

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            gson.toJson(counters, out);
            out.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_COUNTER_REQUEST_CODE) {
            // adding a new counter
            Log.d("INFO", "returned to main activity to add a new counter");
            Counter counter = gson.fromJson(data.getStringExtra("counter"), Counter.class);
            counterList.add(counter);
            adapter.notifyItemInserted(counterList.size() - 1);
            recyclerView.smoothScrollToPosition(counterList.size() - 1);
        } else if (requestCode == EDIT_COUNTER_REQUEST_CODE) {
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
        saveToFile(counterList);
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        @Override
        public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = layoutInflater.inflate(R.layout.counter_list_row, parent, false);
            return new CustomViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final CustomAdapter.CustomViewHolder holder, final int position) {
            final Counter counter = counterList.get(position);
            holder.name.setText(counter.getName());
            holder.comment.setText(counter.getComment());
            holder.count.setText(String.valueOf(counter.getCurrentValue()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            holder.date.setText(sdf.format(counter.getDate()));

            holder.increment.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("INFO", "increment btn");
                    counter.increment();
                    notifyItemChanged(position);
                    saveToFile(counterList);
                }
            });

            holder.decrement.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("INFO", "decrement btn");
                    try {
                        counter.decrement();
                        notifyItemChanged(position);
                        saveToFile(counterList);
                    } catch (CounterTooSmall e) {
                        Snackbar.make(v, "Counter can not be less than zero", Snackbar.LENGTH_LONG).show();
                    }
                }
            });

            holder.options.setOnClickListener(new View.OnClickListener() {
                // code from: https://stackoverflow.com/questions/37601346/create-options-menu-for-recyclerview-item
                // 2017-09-30

                @Override
                public void onClick(final View view) {
                    PopupMenu popup = new PopupMenu(view.getContext(), holder.options);
                    popup.inflate(R.menu.counter);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int pos = holder.getAdapterPosition();
                            switch (item.getItemId()) {
                                case R.id.reset:
                                    counter.reset();
                                    notifyItemChanged(pos);
                                    saveToFile(counterList);
                                    break;
                                case R.id.delete:
                                    counterList.remove(pos);
                                    notifyItemRemoved(pos);
                                    saveToFile(counterList);
                                    break;
                                case R.id.edit:
                                    Gson gson = new Gson();
                                    Intent editIntent = new Intent(view.getContext(), CounterAddActivity.class);

                                    editIntent.putExtra("counter", gson.toJson(counter));
                                    editIntent.putExtra("position", pos);
                                    startActivityForResult(editIntent, EDIT_COUNTER_REQUEST_CODE);
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return counterList.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            private TextView name, comment, count, date;
            private Button increment, decrement;
            private TextView options;

            private CustomViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.counter_name);
                comment = (TextView) itemView.findViewById(R.id.counter_comment);
                count = (TextView) itemView.findViewById(R.id.counter_count);
                date = (TextView) itemView.findViewById(R.id.counter_date);

                increment = (Button) itemView.findViewById(R.id.increment_button);
                decrement = (Button) itemView.findViewById(R.id.decrement_button);
                options = (TextView) itemView.findViewById(R.id.counter_options);
            }
        }
    }
}
