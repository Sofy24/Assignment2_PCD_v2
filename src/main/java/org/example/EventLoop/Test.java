package org.example.EventLoop;


import io.vertx.core.Vertx;
import org.example.Utilities.FilePath;
import org.example.Utilities.FileSearcher;
import org.example.Utilities.Report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

public class Test {
    public static void main(String[] args) {
        CompletableFuture<Report> r = new EventLoopSourceAnalyzer().getReport("C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\f1", 5, 5, 200);
        r.join();
    }
}
