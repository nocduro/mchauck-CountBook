package com.example.mackenzie.mchauck_countbook.data;

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
 * Created by Mackenzie on 2017-09-30.
 */

public class GsonCounterSource implements CounterDataSource {
    private final String filename;
    private final Gson gson;

    public GsonCounterSource(String filename) {
        this.filename = filename;
        this.gson = new Gson();
    }

    @Override
    public List<Counter> getListOfData() {
        Type listType = new TypeToken<ArrayList<Counter>>(){}.getType();
        try {
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            return gson.fromJson(in, listType);
        } catch (FileNotFoundException e) {
            return new ArrayList<Counter>();
        } catch (JsonIOException e) {
            return new ArrayList<Counter>();
        }
    }

    @Override
    public void saveData(List<Counter> counters) {
        try {
            FileOutputStream fos = new FileOutputStream(filename, false);
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
}
