package org.example;

import org.example.Utilities.ComputedFile;
import org.example.Utilities.Pair;

import java.util.ArrayList;
import java.util.List;

public class CurrentState {

    private static CurrentState instance = null;
    private static final List<ComputedFile> currentFiles = new ArrayList<>();

    public static CurrentState getInstance()
    {
        if (instance == null){
            instance = new CurrentState();
        }

        return instance;
    }

    public static void addFile(ComputedFile file){
        currentFiles.add(file);
    }

    public static List<ComputedFile> getFiles(){
        return currentFiles;
    }
}
