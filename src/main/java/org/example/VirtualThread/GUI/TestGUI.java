package org.example.VirtualThread.GUI;

import org.example.Utilities.GUI.View;

public class TestGUI {
    public static void main(String[] args){
        View view = new View();
        ControllerVT controller = new ControllerVT(view);
        view.addListener(controller);
        view.display();
    }

}
