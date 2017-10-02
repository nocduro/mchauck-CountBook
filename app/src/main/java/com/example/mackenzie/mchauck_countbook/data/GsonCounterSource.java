package com.example.mackenzie.mchauck_countbook.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to store a list of Counter objects as a .json file
 */
public class GsonCounterSource implements CounterDataSource {
    private ArrayList<Counter> counters;
    private String filename;
    private Gson gson;
    private Context context;

    public GsonCounterSource(String filename, Context context) {
        counters = new ArrayList<>();
        this.filename = filename;
        this.context = context;
        this.gson = new Gson();
    }

    @Override
    public List<Counter> loadCounters() {
        Type listType = new TypeToken<ArrayList<Counter>>(){}.getType();
        try {
            FileInputStream fis = context.openFileInput(this.filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            counters = gson.fromJson(in, listType);
        } catch (FileNotFoundException e) {
            counters = new ArrayList<>();
        } catch (JsonIOException e) {
            counters = new ArrayList<>();
        }

        // return a copy of the list so that the caller does not interfere
        // with our internal representation
        return new ArrayList(counters);
    }

    private void saveToFile() {
        Log.d("INFO", "saving to file");

        try {
            FileOutputStream fos = context.openFileOutput(this.filename, Context.MODE_PRIVATE);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            gson.toJson(this.counters, out);
            out.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCounter(Counter counter) {
        counters.add(counter);
        saveToFile();
    }

    @Override
    public void modifyCounterAt(int position, Counter modified) {
        counters.set(position, modified);
        saveToFile();
    }

    @Override
    public void deleteCounterAt(int position) {
        counters.remove(position);
        saveToFile();
    }

    @Override
    public void incrementCounterAt(int position) {
        counters.get(position).increment();
        saveToFile();
    }

    @Override
    public void decrementCounterAt(int position) throws CounterTooSmall {
        counters.get(position).decrement();
        saveToFile();
    }

    @Override
    public void resetCounterAt(int position) {
        counters.get(position).reset();
        saveToFile();
    }

    @Override
    public Counter getCounterAt(int position) {
        return counters.get(position);
    }
}
