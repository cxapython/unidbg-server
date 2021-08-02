package com.spider.unidbgserver.controller;

import com.crack.DouyinSign;
import com.github.unidbg.worker.Worker;

import java.io.IOException;
import java.util.Map;

class DYWorker implements Worker {
    private final DouyinSign douyinSign;

    public DYWorker() {
        douyinSign = new DouyinSign();
        System.out.println("Create: " + douyinSign);
    }

    public void close() throws IOException {
        System.out.println("DYWorker close()");
        douyinSign.destroy();
    }


    public Map<String, String> worker(String... args) {
        System.out.println("DYWorker worker: " + Thread.currentThread().getName() + Thread.currentThread().getId());
        String url = args[0];
        return douyinSign.crack(url);
    }
}