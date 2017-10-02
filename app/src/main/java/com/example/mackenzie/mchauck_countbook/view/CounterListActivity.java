package com.example.mackenzie.mchauck_countbook.view;

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

import com.example.mackenzie.mchauck_countbook.Controller;
import com.example.mackenzie.mchauck_countbook.R;
import com.example.mackenzie.mchauck_countbook.data.Counter;
import com.example.mackenzie.mchauck_countbook.data.CounterDataSourceInterface;
import com.example.mackenzie.mchauck_countbook.data.CounterTooSmall;
import com.example.mackenzie.mchauck_countbook.data.GsonCounterSource;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Activity that is run when the app starts.
 *
 * Implements the 'view' part of the app
 *
 * Tutorial partially followed from: https://github.com/BracketCove/RecyclerViewTutorial2017
 * and https://www.androidhive.info/2016/01/android-working-with-recycler-view/
 * 2017-09-29
 *
 */
public class CounterListActivity extends AppCompatActivity implements CounterViewInterface {

    private final int EDIT_COUNTER_REQUEST_CODE = 1;
    private final int ADD_COUNTER_REQUEST_CODE = 2;

    private List<Counter> counterList;
    Controller controller;
    CounterDataSourceInterface dataSource;

    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private CounterAdapter adapter;

    private Gson gson;

    /**
     * App entry point.
     *
     * Setup our data source, and controller. When the controller starts
     * it initializes the view by loading data from the data source
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gson = new Gson();

        recyclerView = (RecyclerView) findViewById(R.id.counter_recycler_view);
        layoutInflater = getLayoutInflater();

        // the floating button is used to add a new counter
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CounterListActivity.this, CounterAddActivity.class);
                startActivityForResult(intent, ADD_COUNTER_REQUEST_CODE);
            }
        });

        // our data will live in a json file so it persists between uses
        dataSource = new GsonCounterSource("counters.json", this);
        // create the controller with this class as the view
        controller = new Controller(this, dataSource);
    }

    /**
     * Called when CounterAddActivity activity is finished.
     *
     * Two possible outcomes of AddActivity:
     *      * we edited a counter
     *      * we added a new counter
     *
     * @param requestCode a code representing if we are editing or adding a counter
     * @param resultCode result of the activity
     * @param data contains a serialized version of a Counter
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_COUNTER_REQUEST_CODE) {
            // adding a new counter
            Counter counter = gson.fromJson(data.getStringExtra("counter"), Counter.class);
            controller.addCounter(counter);
        } else if (requestCode == EDIT_COUNTER_REQUEST_CODE) {
            // editing an existing counter
            int modifiedCounterPosition = data.getIntExtra("position", -1);
            if (modifiedCounterPosition == -1) {
                // couldn't read position value...
                return;
            }

            Counter counter = gson.fromJson(data.getStringExtra("counter"), Counter.class);
            controller.modifyCounterAt(modifiedCounterPosition, counter);
        }
    }

    /**
     * Get the updated counter from our data source and display it
     * @param position the location of the updated Counter
     */
    @Override
    public void updateCounterAt(int position) {
        Log.d("INFO", "updating counter at " + position);
        counterList.set(position, dataSource.getCounterAt(position));
        adapter.notifyItemChanged(position);
    }

    /**
     * Delete a counter from the view
     * @param position the position of the counter in the RecyclerView
     */
    @Override
    public void deleteCounterAt(int position) {
        Log.d("INFO", counterList.toString());
        Log.d("INFO", "deleting counter at " + position);
        counterList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    /**
     * Add a counter
     * @param counter the counter to be added
     */
    @Override
    public void addCounter(Counter counter) {
        Log.d("INFO", "adding counter");
        counterList.add(counter);
        adapter.notifyItemInserted(counterList.size() - 1);
        recyclerView.smoothScrollToPosition(counterList.size() - 1);
    }

    /**
     * Initialize our views internal representation of the Counters,
     * and initialize the RecyclerView, and adapter
     * @param counters a List of counters to be displayed in the activity
     */
    @Override
    public void setupView(List<Counter> counters) {
        counterList = counters;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CounterAdapter();
        recyclerView.setAdapter(adapter);

        // disable the animation when the counter changes, to increase responsiveness
        // from: https://stackoverflow.com/questions/31897469/override-animation-for-notifyitemchanged-in-recyclerview-adapter
        // 2017-09-29
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    /**
     * The adapter for the RecyclerView.
     *
     * This class defines what to put into each Counter in the RecyclerView
     * as well as what the button presses do.
     */
    private class CounterAdapter extends RecyclerView.Adapter<CounterAdapter.CustomViewHolder> {

        @Override
        public CounterAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = layoutInflater.inflate(R.layout.counter_list_row, parent, false);
            return new CustomViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final CounterAdapter.CustomViewHolder holder, int position) {
            Counter counter = counterList.get(position);
            holder.name.setText(counter.getName());
            holder.comment.setText(counter.getComment());
            holder.count.setText(String.valueOf(counter.getCurrentValue()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            holder.date.setText(sdf.format(counter.getDate()));

            holder.increment.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    controller.incrementCounterAt(holder.getAdapterPosition());
                }
            });

            holder.decrement.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    try {
                        controller.decrementCounterAt(holder.getAdapterPosition());
                    } catch (CounterTooSmall e) {
                        Snackbar.make(v, "Counter can not be less than zero",
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            });

            // setup the options menu for the counter (reset, edit, delete) actions
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
                                    controller.resetCounterAt(holder.getAdapterPosition());
                                    break;
                                case R.id.delete:
                                    controller.deleteCounterAt(holder.getAdapterPosition());
                                    break;
                                case R.id.edit:
                                    Intent editIntent = new Intent(view.getContext(),
                                            CounterAddActivity.class);

                                    editIntent.putExtra("counter", gson.toJson(
                                            dataSource.getCounterAt(holder.getAdapterPosition())));
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

        /**
         * Bind to the layout for the Counter
         */
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
