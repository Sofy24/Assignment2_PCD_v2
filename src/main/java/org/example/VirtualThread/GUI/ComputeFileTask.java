package org.example.VirtualThread.GUI;

import org.example.Utilities.GUI.Flag;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.LongRange;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

public class ComputeFileTask implements Callable<ComputedFile> {

    private final FilePath filePath;
    private final Flag blockFlag;
    private final List<LongRange> ranges;

    public ComputeFileTask(FilePath filePath, Flag blockFlag, List<LongRange> ranges) {
        super();
        this.blockFlag = blockFlag;
        this.filePath = filePath;
        this.ranges = ranges;
    }

    @Override
    public ComputedFile call() throws Exception {
        long fileLen = 0L;
        try {
            fileLen = Files.lines(Paths.get(filePath.getCompleteFilePath()), StandardCharsets.UTF_8).count();
        } catch (Exception e){
            e.printStackTrace();
        }
        for (LongRange range: ranges) {
            if (range.isInRange(fileLen)) {
                if (blockFlag.isSet()) {
                    throw new RuntimeException("Interrupted");
                }
                return new ComputedFile(filePath, range.getMin(), fileLen);
            }
        }
        return null;
    }
}
