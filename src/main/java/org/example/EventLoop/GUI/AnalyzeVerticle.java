package org.example.EventLoop.GUI;

import io.vertx.core.AbstractVerticle;
import org.example.Utilities.*;
import org.example.Utilities.GUI.Flag;

import java.util.List;

import static org.example.Utilities.ComputeFile.computeFile;

public class AnalyzeVerticle extends AbstractVerticle {

    private final String verticleName;
    private final Flag blockFlag;
    private final List<LongRange> ranges;
    private final Monitor monitor;
    private final String address;
    private final String separator = "-";

    public AnalyzeVerticle(String verticleName, Flag blockFlag, List<LongRange> ranges, Monitor monitor) {
        this.verticleName = verticleName;
        this.blockFlag = blockFlag;
        this.ranges = ranges;
        this.monitor = monitor;
        this.address = verticleName.replace("analyze","");
    }

    @Override
    public void start() {
        //consumer for processing files;
        vertx.eventBus().<String>consumer("compute"+address, message -> {
            //process the message
            String subRange = message.body();
            int startIndex = Integer.parseInt(subRange.split(separator)[0]);
            int endIndex = Integer.parseInt(subRange.split(separator)[1]);
            List<FilePath> files = monitor.getUnprocessedFiles().subList(startIndex, endIndex);
            //for every file process it into a ComputedFile
            for (FilePath file : files) {
                //if interrupted
                if (blockFlag.isSet()) {
                  break;
                }
                ComputedFile current = ComputeFile.computeFile(file, ranges);
                if (current != null) {
                    monitor.addComputedFile(current);
                }
            }
            //when all files are processed or interrupted
            vertx.eventBus().publish("completed", verticleName);
        });
    }
}