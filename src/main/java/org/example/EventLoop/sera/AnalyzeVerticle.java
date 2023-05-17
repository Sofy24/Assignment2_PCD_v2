package org.example.EventLoop.sera;

import io.vertx.core.AbstractVerticle;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.GUI.Flag;
import org.example.Utilities.LongRange;
import org.example.Utilities.Monitor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AnalyzeVerticle extends AbstractVerticle {

    private final String verticleName;
    private final Flag blockFlag;
    private final List<LongRange> ranges;
    private final Monitor monitor;
    private final String address;

    public AnalyzeVerticle(String verticleName, Flag blockFlag, List<LongRange> ranges, Monitor monitor) {
        this.verticleName = verticleName;
        this.blockFlag = blockFlag;
        this.ranges = ranges;
        this.monitor = monitor;
        this.address = verticleName.replace("analyze","");
    }

    @Override
    public void start() {
        //System.out.println("CREATED"+address);
        vertx.eventBus().<String>consumer("compute"+address, message -> {
            //System.out.println("RECEV" +address);
            String subRange = message.body();
            int startIndex = Integer.parseInt(subRange.split("-")[0]);
            int endIndex = Integer.parseInt(subRange.split("-")[1]);
            //System.out.println(verticleName+" "+monitor.getUnprocessedFiles().size()+" "+startIndex+" "+endIndex);
            List<FilePath> files = monitor.getUnprocessedFiles().subList(startIndex, endIndex);
            //System.out.println(verticleName + files.get(0).getCompleteFilePath());
            for (FilePath file : files) {
                if (blockFlag.isSet()) {
                  break;
                }
                ComputedFile current = compute(file);
                if (current != null) {
                    monitor.addComputedFile(current);
                }
            }
            vertx.eventBus().publish("completed", verticleName);
        });
    }


    private ComputedFile compute(FilePath filePath) {
        //process a file into a ComputedFile
        long fileLen = 0L;
        try {
            fileLen = Files.lines(Paths.get(filePath.getCompleteFilePath()), StandardCharsets.UTF_8).count();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        for (LongRange range : ranges) {
            if (range.isInRange(fileLen)) {
                return new ComputedFile(filePath, range.getMin(), fileLen);
            }
        }
        return null;
    }
}