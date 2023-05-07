package org.example.Utilities;

public class FilePath {

    private final String path;
    private final String fileName;

    public FilePath(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public FilePath(String fullPath) {
        this.path = fullPath.substring(0, fullPath.lastIndexOf(System.getProperty("file.separator")));
        this.fileName = fullPath.substring(fullPath.lastIndexOf(System.getProperty("file.separator")) + 1);
    }

    public String getCompleteFilePath() {
        return path + System.getProperty("file.separator") + fileName;
    }

    @Override
    public String toString() {
        return "FilePath{" +
                "path='" + path + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
