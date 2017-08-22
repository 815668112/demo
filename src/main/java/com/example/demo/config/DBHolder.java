package com.example.demo.config;

/**
 * Created by Administrator on 2017/8/22.
 */
public final class DBHolder {
    private final static ThreadLocal<String> local = new ThreadLocal<String>();

    public final static void setDb(String db) {
        local.set(db);
    }

    public final static String getDb() {
        return local.get();
    }

    public final static void clear(){
        local.remove();
    }
}
