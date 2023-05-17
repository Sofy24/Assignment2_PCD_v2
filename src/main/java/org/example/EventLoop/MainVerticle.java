package org.example.EventLoop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import org.example.EventLoop.sera.AnalyzeVerticle;
import org.example.Utilities.*;
import org.example.Utilities.GUI.Flag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class MainVerticle extends AbstractVerticle {

    private final static int BATCH_SIZE = 5000;
    public static void main(String[] args) throws InterruptedException {

        //args
        Flag blockFlag = new Flag();
        String directory = "C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\f1";

        List<LongRange> ranges = CreateRange.generateRanges(200, 5);
        Monitor monitor = new Monitor();

        Vertx vertx = Vertx.vertx();
        List<FilePath> files;
        if (monitor.getComputedFileList().isEmpty()) {
             files = FileSearcher.getAllFilesWithPaths(directory);
            monitor.addUnprocessedFiles(files);
        } else {
            monitor.removeAllFileComputed();
            files = new ArrayList<>(monitor.getUnprocessedFiles());
        }
        if (files != null) {
            int numBatches = (int) Math.ceil((double) files.size() / BATCH_SIZE);
            AtomicInteger completed = new AtomicInteger(numBatches);
            vertx.setPeriodic(200, timerId -> {
                System.out.println("UPDATE GRAFICA DI FILE: " + monitor.getComputedFileList().size());
            });
            vertx.eventBus().consumer("completed", (Handler<Message<String>>) message -> {
                completed.getAndDecrement();
                if (completed.get() == 0) {
                    System.out.println("completed");
                }

            });

            for (int i = 0; i < numBatches; i++) {
                int startIndex = i * BATCH_SIZE;
                int endIndex = Math.min(startIndex + BATCH_SIZE, files.size());
                //List<FilePath> batchFiles = files.subList(startIndex, endIndex);
                int finalI = i;
                vertx.deployVerticle(new AnalyzeVerticle("analyze"+i, blockFlag, ranges, monitor),
                        deploymentResult -> {
                            if (deploymentResult.succeeded()) {
                                vertx.eventBus().publish("compute"+ finalI, startIndex + "-" + endIndex);
                            } else {
                                System.out.println("deployment failed: " + deploymentResult.cause());
                            }
                        });

            }
        }
    }

    private static Handler<AsyncResult<String>> handleVerticleDeployment(String verticleName) {
        return deploymentResult -> {
            if (deploymentResult.succeeded()) {
                System.out.println(verticleName + " deployed successfully");
            } else {
                System.out.println(verticleName + " deployment failed: " + deploymentResult.cause());
            }
        };
    }


        /*
        subscribeToNumbers("Verticle1", vertx.eventBus(), allNumbers);
        subscribeToNumbers("Verticle2", vertx.eventBus(), allNumbers);

        vertx.deployVerticle(new AnalyzeVerticle("Verticle1", blockFlag, batchFiles)); //handleVerticleDeployment("Verticle1", vertx));
        vertx.deployVerticle(new AnalyzeVerticle("Verticle2", blockFlag, batchFiles)); //handleVerticleDeployment("Verticle2", vertx));


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
    /*
    private static void subscribeToNumbers(String verticleName, EventBus eventBus, List<Integer> allNumbers) {
        eventBus.consumer(verticleName, (Handler<Message<Integer>>) message -> {
            Integer number = message.body();
            allNumbers.add(number);
            System.out.println("Received number " + number + " from " + verticleName);
        });
    }*/
}