package com.worker;

import com.crack.MaFengWo;
import com.github.unidbg.worker.Worker;

import java.io.IOException;
import java.util.Map;

public class MFWWorker implements Worker {
    private final MaFengWo mafengwo ;

    public MFWWorker() {
        mafengwo = new MaFengWo();
//        System.out.println("Create: " + mafengwo);
    }
    
    @Override
    public void close() throws IOException {
//        System.out.println("MFWWorker close()");
        mafengwo.destroy();
    }

    public Map<String, String> worker(String... args) {
//        System.out.println("MFWWorker worker: " + Thread.currentThread().getName() + Thread.currentThread().getId());
        String url = args[0];
        return mafengwo.xPreAuthencode(url);
    }
}