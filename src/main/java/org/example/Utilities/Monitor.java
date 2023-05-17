package org.example.Utilities;

import java.util.ArrayList;
import java.util.List;

public class Monitor {

    private List<FilePath> unprocessedFiles = new ArrayList<>();
    private final List<ComputedFile> computedFileList = new ArrayList<>();

    public List<FilePath> getUnprocessedFiles() {
        return unprocessedFiles;
    }

    public void addUnprocessedFiles(List<FilePath> files) {
        unprocessedFiles = new ArrayList<>(files);
    }

    public synchronized void replaceFileWithComputed(ComputedFile computedFile) {
        unprocessedFiles.remove(computedFile.getFilePath());
        computedFileList.add(computedFile);
    }

    public synchronized void addComputedFile(ComputedFile computedFile) {
        computedFileList.add(computedFile);
    }

    public synchronized void removeAllFileComputed() {
        unprocessedFiles.removeAll(computedFileList.stream().map(ComputedFile::getFilePath).toList());
    }

    public List<ComputedFile> getComputedFileList(){
        return new ArrayList<>(this.computedFileList);
    }
}
