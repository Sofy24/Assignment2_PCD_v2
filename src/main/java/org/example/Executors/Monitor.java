package org.example.Executors;

import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;

import java.util.ArrayList;
import java.util.List;

public class Monitor {

    private List<FilePath> unprocessedFiles = new ArrayList<>();
    private List<ComputedFile> computedFileList = new ArrayList<>();

    public List<FilePath> getUnprocessedFiles() {
        return unprocessedFiles;
    }

    public void addUnprocessedFiles(List<FilePath> files) {
        unprocessedFiles = new ArrayList<>(files);
    }

    public synchronized void addComputedFile(ComputedFile computedFile) {
        unprocessedFiles.remove(computedFile.getFilePath());
        computedFileList.add(computedFile);
    }
}
