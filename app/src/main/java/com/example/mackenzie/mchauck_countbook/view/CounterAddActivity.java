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
            Log.d("INFO", "editing counter...");
            counter = gson.fromJson(inputIntent.getStringExtra("counter"), Counter.class);

            name.setText(counter.getName());
            comment.setText(counter.getComment());
            initialValue.setText(String.valueOf(counter.getInitialValue()));
            currentValue.setText(String.valueOf(counter.getCurrentValue()));

            returnIntent.putExtra("position", inputIntent.getIntExtra("position", -1));

        } else {
            // adding counter
            Log.d("INFO", "adding new counter...");
            counter = new Counter("tempName", 0);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    counter.setName(name.getText().toString());
                    counter.setComment(comment.getText().toString());
                    try {
                        counter.setInitialValue(Integer.parseInt(initialValue.getText().toString()));
                        counter.setCurrentValue(Integer.parseInt(currentValue.getText().toString()));
                    } catch (CounterTooSmall counterTooSmall) {
                        // already validated data before...
                    }

                    returnIntent.putExtra("counter", gson.toJson(counter));
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

            }
        });
    }

    private boolean validateData() {
        if (name.getText().toString().length() == 0) {
            name.setError("Name is required");
            return false;
        }

        // initialValue
        try {
            if (Integer.parseInt(initialValue.getText().toString()) < 0) {
                initialValue.setError("Initial Value must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            initialValue.setError("Initial Value must be a number");
            return false;
        }

        // currentValue
        try {
            if (Integer.parseInt(currentValue.getText().toString()) < 0) {
                currentValue.setError("Current Value must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            currentValue.setError("Current Value must be a number");
            return false;
        }
        return true;
    }

}
