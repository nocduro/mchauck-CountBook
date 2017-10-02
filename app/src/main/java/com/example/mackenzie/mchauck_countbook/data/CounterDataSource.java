package com.example.mackenzie.mchauck_countbook.data;

import java.util.List;

/**
 * Created by Mackenzie on 2017-10-01.
 */

public interface CounterDataSource {
    List<Counter> loadCounters();
    void addCounter(Counter counter);
    void modifyCounterAt(int position, Counter modified);
    void deleteCounterAt(int position);

    void incrementCounterAt(int position);
    void decrementCounterAt(int position) throws CounterTooSmall;
    void resetCounterAt(int position);

    Counter getCounterAt(int position);
}
