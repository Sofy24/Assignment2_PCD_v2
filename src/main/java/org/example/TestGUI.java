package org.example;

import org.example.Executors.GUI.ControllerExecutors;

public class TestGUI {
    public static void main(String[] args){
        View view = new View();
        ControllerExecutors controller = new ControllerExecutors(view);
        view.addListener(controller);
        view.display();
    }

}
