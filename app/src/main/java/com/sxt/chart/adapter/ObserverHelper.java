package com.sxt.chart.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mars on 16-3-11.
 */
public class ObserverHelper {

    public static final int ACTION_MESSAGE = 0;
    public static final int ACTION_SMS = 1;
    public static final int ACTION_WIFI = 2;
    public static final int ACTION_REFRESH_TITLE = 3;
    public static final int ACTION_END_DOWNLOAD = 4;
    private static ObserverHelper instance;
    private List<ObserverListener> listeners = new ArrayList<>();

    private ObserverHelper() {
    }

    public synchronized static ObserverHelper getInstance() {
        if (instance == null) {
            instance = new ObserverHelper();
        }
        return instance;
    }

    public void registerObserver(ObserverListener listener) {
        listeners.add(listener);
    }

    public void removeObserver(ObserverListener listener) {
        listeners.remove(listener);
    }

    public void notifyObserver(int type, Object data) {
        for (ObserverListener observer : listeners) {
            observer.update(type, data);
        }
    }

    public interface ObserverListener {
        void update(int type, Object object);
    }
}
