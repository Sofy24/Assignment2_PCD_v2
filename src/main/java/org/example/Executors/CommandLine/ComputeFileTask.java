package org.example.Executors.CommandLine;


import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.LongRange;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import static org.example.Utilities.ComputeFile.computeFile;

public class ComputeFileTask extends RecursiveTask<ComputedFile> {

    private final FilePath filePath;
    private final List<LongRange> ranges;

    public ComputeFileTask(FilePath filePath, List<LongRange> ranges) {
        this.filePath = filePath;
        this.ranges = ranges;
    }

    @Override
    protected ComputedFile compute() {
        return computeFile(filePath, ranges);
    }

}
