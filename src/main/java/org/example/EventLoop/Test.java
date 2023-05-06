package org.example.EventLoop;


import io.vertx.core.Vertx;
import org.example.Utilities.FilePath;
import org.example.Utilities.FileSearcher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public class Test {
    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        System.out.println("START");
        String directory = "C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\f1";

        vertx.eventBus().consumer("file", message -> {
            // Handle the received event
            String file = ((String) message.body());
            long len = 0L;
            try {
                len = Files.lines(Paths.get(file), StandardCharsets.UTF_8).count();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(file + ": "+len);
        });

        vertx.eventBus().consumer("dir", message -> {
            // Handle the received event
            String dir = (String)message.body();
            System.out.println("DIR: "+ dir);

            Set<File> dirs = FileSearcher.getSubDirectory(dir);
            if (dirs != null) {
                dirs.forEach(d -> vertx.eventBus().publish("dir", d.getAbsolutePath()));
            }

            Set<String> files = FileSearcher.getJavaSourceFiles(dir);
            if (files != null) {
                files.forEach(file -> vertx.eventBus().publish("file", new FilePath(dir, file).getCompleteFilePath()));
            }
        });

        vertx.eventBus().publish("dir", directory);
    }
}
