package com.example.mackenzie.mchauck_countbook.controller;

import com.example.mackenzie.mchauck_countbook.data.Counter;
import com.example.mackenzie.mchauck_countbook.data.CounterDataSource;
import com.example.mackenzie.mchauck_countbook.view.CounterViewInterface;

/**
 * Created by Mackenzie on 2017-09-30.
 */

public class Controller {
    private CounterViewInterface view;
    private CounterDataSource dataSource;

    public Controller(CounterViewInterface view, CounterDataSource dataSource) {
        this.view = view;
        this.dataSource = dataSource;

        getListFromDataSource();
    }

    public void getListFromDataSource() {
        view.setupAdapterAndView(dataSource.getListOfData());
    }
}
