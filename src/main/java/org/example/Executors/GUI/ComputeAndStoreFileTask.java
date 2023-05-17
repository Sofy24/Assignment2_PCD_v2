package org.example.Executors.GUI;

import org.example.Utilities.GUI.Flag;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.LongRange;
import org.example.Utilities.Monitor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ComputeAndStoreFileTask implements Runnable {

    private final List<LongRange> ranges;
    private FilePath file;
    private Monitor monitor;
    private Flag blockFlag;

    public ComputeAndStoreFileTask(FilePath file, Monitor monitor, Flag blockFlag, List<LongRange> ranges) {
        this.file = file;
        this.monitor = monitor;
        this.blockFlag = blockFlag;
        this.ranges = ranges;
    }

    @Override
    public void run() {
        long fileLen = 0L;
        //if interrupted does not compute the file
        if (blockFlag.isSet()) {
            return;
        }
        //compute the file and put it in the shared list of computed files
        try {
            fileLen = Files.lines(Paths.get(file.getCompleteFilePath()), StandardCharsets.UTF_8).count();
        } catch (Exception e){
            e.printStackTrace();
        }
        for (LongRange range: ranges) {
            if (range.isInRange(fileLen)) {
                monitor.replaceFileWithComputed(new ComputedFile(file, range.getMin(), fileLen));
            }
        }
    }
}
