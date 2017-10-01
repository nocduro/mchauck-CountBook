package com.example.mackenzie.mchauck_countbook.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mackenzie on 2017-09-30.
 */

public class TestCounterSource implements CounterDataSource {
    @Override
    public List<Counter> getListOfData() {
        ArrayList<Counter> counterList = new ArrayList<>();

        counterList.add(new Counter("test1", 0, "this is only a test..."));
        counterList.add(new Counter("Hello!", 3));
        counterList.add(new Counter("This counter has a long comment", 1000, "this is a very long comment to see what will happen when we run into the date..."));

        return counterList;
    }

    @Override
    public void saveData(List<Counter> counters) {

    }
}
