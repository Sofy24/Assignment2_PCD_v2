package org.example.EventLoop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.example.EventLoop.MyVerticle;
import org.example.Utilities.GUI.Flag;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainVerticle extends AbstractVerticle {

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();

        List<Integer> allNumbers = new ArrayList<>();
        Flag blockFlag = new Flag();

        subscribeToNumbers("Verticle1", vertx.eventBus(), allNumbers);
        subscribeToNumbers("Verticle2", vertx.eventBus(), allNumbers);

        vertx.deployVerticle(new MyVerticle("Verticle1", blockFlag)); //handleVerticleDeployment("Verticle1", vertx));
        vertx.deployVerticle(new MyVerticle("Verticle2", blockFlag)); //handleVerticleDeployment("Verticle2", vertx));

        vertx.eventBus().consumer("completed", (Handler<Message<String>>) message -> {
            System.out.println(message.body() + " completed");
        });
        vertx.eventBus().consumer("interrupted", (Handler<Message<String>>) message -> {
            System.out.println(message.body() + " interrupted");
        });
        vertx.eventBus().consumer("completed", (Handler<Message<String>>) message -> {
            System.out.println(message.body() + " completed2");
        });

    }

    /*private static Handler<AsyncResult<String>> handleVerticleDeployment(String verticleName, Vertx vertx) {
        return deploymentResult -> {
            if (deploymentResult.succeeded()) {
                System.out.println(verticleName + " deployed successfully");
            } else {
                System.out.println(verticleName + " deployment failed: " + deploymentResult.cause());
            }
        };
    }*/

    private static void subscribeToNumbers(String verticleName, EventBus eventBus, List<Integer> allNumbers) {
        eventBus.consumer(verticleName, (Handler<Message<Integer>>) message -> {
            Integer number = message.body();
            allNumbers.add(number);
            System.out.println("Received number " + number + " from " + verticleName);
        });
    }
}