package com.example.mackenzie.mchauck_countbook;

import com.example.mackenzie.mchauck_countbook.data.Counter;
import com.example.mackenzie.mchauck_countbook.data.CounterDataSource;
import com.example.mackenzie.mchauck_countbook.data.CounterTooSmall;
import com.example.mackenzie.mchauck_countbook.view.CounterView;

/**
 * Class to dispatch work to our data (model) and view when an event happens
 */
public class Controller {
    private CounterView view;
    private CounterDataSource dataSource;

    public Controller(CounterView view, CounterDataSource dataSource) {
        this.view = view;
        this.dataSource = dataSource;
        view.setupView(dataSource.loadCounters());
    }

    public void addCounter(Counter counter) {
        dataSource.addCounter(counter);
        view.addCounter(counter);
    }

    public void deleteCounterAt(int position) {
        dataSource.deleteCounterAt(position);
        view.deleteCounterAt(position);
    }

    public void modifyCounterAt(int position, Counter newCounter) {
        dataSource.modifyCounterAt(position, newCounter);
        view.updateCounterAt(position);
    }

    public void incrementCounterAt(int position) {
        dataSource.incrementCounterAt(position);
        view.updateCounterAt(position);
    }

    public void decrementCounterAt(int position) throws CounterTooSmall {
        dataSource.decrementCounterAt(position);
        view.updateCounterAt(position);
    }

    public void resetCounterAt(int position) {
        dataSource.resetCounterAt(position);
        view.updateCounterAt(position);
    }
}
