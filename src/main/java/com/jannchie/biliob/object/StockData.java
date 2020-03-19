package com.jannchie.biliob.object;

/**
 * @author Jannchie
 */
public class StockData {
    private int first;
    private int last;
    private int max;
    private int min;
    private String date;

    public StockData(int first, int last, int max, int min, String date) {
        this.first = first;
        this.last = last;
        this.max = max;
        this.min = min;
        this.date = date;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
