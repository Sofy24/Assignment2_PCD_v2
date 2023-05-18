package org.example.VirtualThread.CommandLine;

import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.LongRange;

import java.util.List;
import java.util.concurrent.Callable;

import static org.example.Utilities.ComputeFile.computeFile;

public class ComputeFileTask implements Callable<ComputedFile> {

    private final FilePath filePath;
    private final List<LongRange> ranges;

    public ComputeFileTask(FilePath filePath, List<LongRange> ranges) {
        super();
        this.filePath = filePath;
        this.ranges = ranges;
    }

    @Override
    public ComputedFile call() {
        return computeFile(filePath, ranges);
    }
}
