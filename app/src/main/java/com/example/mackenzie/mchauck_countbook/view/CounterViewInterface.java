package com.example.mackenzie.mchauck_countbook.view;

import android.view.View;

import com.example.mackenzie.mchauck_countbook.data.Counter;

import java.util.List;

/**
 * Created by Mackenzie on 2017-09-30.
 */

public interface CounterViewInterface {

    void startEditActivity(Counter counter, View viewRoot);
    //void startAddActivity(View viewRoot);
    void setupAdapterAndView(List<Counter> counters);
    void addNewCounterToView(Counter counter);
    //void deleteCounterAt(int position);
}
