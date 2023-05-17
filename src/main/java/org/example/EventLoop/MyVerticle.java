package org.example.EventLoop;

import io.vertx.core.AbstractVerticle;
import org.example.Utilities.GUI.Flag;

import java.util.concurrent.atomic.AtomicInteger;

public class MyVerticle extends AbstractVerticle {

    private final String verticleName;
    private final Flag blockFlag;

    public MyVerticle(String verticleName, Flag blockFlag) {
        this.verticleName = verticleName;
        this.blockFlag = blockFlag;
    }

    @Override
    public void start() {
        AtomicInteger i = new AtomicInteger();
        vertx.setPeriodic(10, timerId -> {
            if (blockFlag.isSet()) {
                vertx.eventBus().publish("interrupted", verticleName);
                vertx.cancelTimer(timerId);
            }
            System.out.println(verticleName +": "+ i);
            vertx.eventBus().publish(verticleName, i.get());
            i.getAndIncrement();
            if (i.get() > 1000) {
                vertx.eventBus().publish("completed", verticleName);
                vertx.cancelTimer(timerId);
            }
        });
    }
}