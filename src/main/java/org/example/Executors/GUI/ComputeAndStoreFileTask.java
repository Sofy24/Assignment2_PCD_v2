package org.example.Executors.GUI;

import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.GUI.Flag;
import org.example.Utilities.LongRange;
import org.example.Utilities.Monitor;

import java.util.List;

import static org.example.Utilities.ComputeFile.computeFile;

public class ComputeAndStoreFileTask implements Runnable {

    private final List<LongRange> ranges;
    private final FilePath file;
    private final Monitor monitor;
    private final Flag blockFlag;

    public ComputeAndStoreFileTask(FilePath file, Monitor monitor, Flag blockFlag, List<LongRange> ranges) {
        this.file = file;
        this.monitor = monitor;
        this.blockFlag = blockFlag;
        this.ranges = ranges;
    }

    @Override
    public void run() {
        //if interrupted does not compute the file
        if (blockFlag.isSet()) {
            return;
        }
        //compute the file and put it in the shared list of computed files
        ComputedFile computedFile = computeFile(file, ranges);
        if (computedFile != null) {
            monitor.replaceFileWithComputed(computedFile);
        }
    }
}
