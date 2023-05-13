package org.example.Executors.CommandLine;


import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.LongRange;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ComputeFileTask extends RecursiveTask<ComputedFile> {

    private final FilePath filePath;
    private final List<LongRange> ranges;

    public ComputeFileTask(FilePath filePath, List<LongRange> ranges) {
        this.filePath = filePath;
        this.ranges = ranges;
    }

    @Override
    protected ComputedFile compute() {
        //process a file into a ComputedFile
        long fileLen = 0L;
        try {
            fileLen = Files.lines(Paths.get(filePath.getCompleteFilePath()), StandardCharsets.UTF_8).count();
        } catch (Exception e){
            e.printStackTrace();
        }
        for (LongRange range: ranges) {
            if (range.isInRange(fileLen)) {
                return new ComputedFile(filePath, range.getMin(), fileLen);
            }
        }
        return null;
    }

}
