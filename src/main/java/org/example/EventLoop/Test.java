package org.example.EventLoop;


import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.example.Utilities.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Future<Report> futureReport = new EventLoopSourceAnalyzer().getReport("C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\file50", 5, 5, 200);
        futureReport.onComplete(report ->  report.result().getResults());
    }
}
