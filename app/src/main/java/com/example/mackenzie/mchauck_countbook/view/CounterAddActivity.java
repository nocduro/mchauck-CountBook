package com.example.mackenzie.mchauck_countbook.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mackenzie.mchauck_countbook.R;
import com.example.mackenzie.mchauck_countbook.data.Counter;
import com.example.mackenzie.mchauck_countbook.data.CounterTooSmall;
import com.google.gson.Gson;

/**
 * Used to both add new Counters and edit them.
 *
 * Communicates with the list activity by serializing Counter objects.
 * In the case of editing, we also pass the position back so that the list can update
 */
public class CounterAddActivity extends AppCompatActivity {
    Counter counter;
    Intent returnIntent;
    Gson gson;

    EditText name, comment, initialValue, currentValue;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.name);
        comment = (EditText) findViewById(R.id.comment);
        initialValue = (EditText) findViewById(R.id.initial_value);
        currentValue = (EditText) findViewById(R.id.current_value);

        addButton = (Button) findViewById(R.id.add_button);

        Intent inputIntent = getIntent();
        gson = new Gson();
        returnIntent = new Intent();

        if (inputIntent.hasExtra("counter")) {
            // editing a counter
            setTitle("Edit Counter");
            addButton.setText("Save");
            counter = gson.fromJson(inputIntent.getStringExtra("counter"), Counter.class);

            name.setText(counter.getName());
            comment.setText(counter.getComment());
            initialValue.setText(String.valueOf(counter.getInitialValue()));
            currentValue.setText(String.valueOf(counter.getCurrentValue()));

            // also include our position as an extra, so that we can call
            // notifyItemChanged(position);
            returnIntent.putExtra("position", inputIntent.getIntExtra("position", -1));
        } else {
            // adding a new counter
            counter = new Counter("temp", 0);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    counter.setName(name.getText().toString());
                    counter.setComment(comment.getText().toString());

                    String currentValString = currentValue.getText().toString();
                    try {
                        counter.setInitialValue(Integer.parseInt(initialValue.getText().toString()));

                        // if we get input for currentValue, then set it, otherwise reset to initialValue
                        if (currentValString.length() != 0) {
                            counter.setCurrentValue(Integer.parseInt(currentValString));
                        } else {
                            counter.reset();
                        }
                    } catch (CounterTooSmall e) {
                        // already validated data before...
                    }

                    returnIntent.putExtra("counter", gson.toJson(counter));
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    /**
     * Make sure the data input by the user in the forms is correct.
     * Must have at least a Name, and an InitialValue.
     * InitialValue, and CurrentValue must be positive integers.
     *
     * Will also set a visual error message for the fields that need to be fixed
     *
     * @return true if all of the fields are valid
     */
    private boolean validateData() {
        boolean valid = true;

        if (name.getText().toString().length() == 0) {
            name.setError("Name is required");
            valid = false;
        }

        // initialValue
        try {
            if (Integer.parseInt(initialValue.getText().toString()) < 0) {
                initialValue.setError("Initial Value must be greater than 0");
                valid = false;
            }
        } catch (NumberFormatException e) {
            initialValue.setError("Initial Value must be a number");
            valid = false;
        }

        // currentValue is optional, so only check if it is valid if something is there
        if (currentValue.getText().toString().length() > 0) {
            try {
                if (Integer.parseInt(currentValue.getText().toString()) < 0) {
                    currentValue.setError("Current Value must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                currentValue.setError("Current Value must be a number");
                valid = false;
            }
        }
        return valid;
    }

}
