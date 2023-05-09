package org.example.ReactiveProgramming;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.example.Utilities.*;

import java.io.File;
import java.lang.foreign.PaddingLayout;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Test2 {

    public static void main(String[] args) throws InterruptedException {
        String directory = "C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\file50";
        var future = new ReactiveProgrammingSourceAnalyser().getReport(directory, 5, 5, 150);

        future.thenAccept(Report::getResults);
        //simulate doing other stuff
        sleep(5000);


    }
}
