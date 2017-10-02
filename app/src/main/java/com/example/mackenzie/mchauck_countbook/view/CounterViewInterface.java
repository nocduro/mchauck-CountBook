package com.example.mackenzie.mchauck_countbook.view;

import com.example.mackenzie.mchauck_countbook.data.Counter;

import java.util.List;

/**
 * Created by Mackenzie on 2017-10-01.
 */

public interface CounterViewInterface {
    void updateCounterAt(int position);
    void deleteCounterAt(int position);
    void addCounter(Counter counter);

    void setupView(List<Counter> counters);
}
