package com.example.mackenzie.mchauck_countbook.data;

import java.util.Date;

/**
 * Created by Mackenzie on 2017-09-28.
 */

public class Counter {
    private String name;
    private Date date;

    private int currentValue;
    private int initialValue;
    private String comment;

    public Counter(String name, int initialValue) {
        this.name = name;
        this.initialValue = initialValue;
        this.currentValue = initialValue;
        this.date = new Date();
        this.comment = "";
    }

    public Counter(String name, int initialValue, String comment) {
        this(name, initialValue);
        this.comment = comment;
    }

    public void reset() {
        this.currentValue = this.initialValue;
        this.date = new Date();
    }

    public void increment() {
        this.currentValue++;
        this.date = new Date();
    }

    public void decrement() throws CounterTooSmall {
        if (this.currentValue == 0) {
            throw new CounterTooSmall();
        }
        this.currentValue--;
        this.date = new Date();
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public String getComment() {
        return comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentValue(int currentValue) throws CounterTooSmall {
        if (currentValue < 0) {
            throw new CounterTooSmall();
        }
        this.currentValue = currentValue;
    }

    public void setInitialValue(int initialValue) throws CounterTooSmall {
        if (initialValue < 0) {
            throw new CounterTooSmall();
        }
        this.initialValue = initialValue;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

