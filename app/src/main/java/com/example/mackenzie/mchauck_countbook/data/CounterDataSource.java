package com.example.mackenzie.mchauck_countbook.data;

import java.util.List;

/**
 * Created by Mackenzie on 2017-09-30.
 */

public interface CounterDataSource {
    List<Counter> getListOfData();
    void saveData(List<Counter> counters);
}
