package org.example.ReactiveProgramming.GUI;


import org.example.Utilities.GUI.View;

public class TestGUI {

    public static void main(String[] args){
        View view = new View();
        ControllerReactive controller = new ControllerReactive(view);
        view.addListener(controller);
        view.display();
    }

}
