package org.example.EventLoop.GUI;

import org.example.Utilities.GUI.View;

public class TestGUI {
    public static void main(String[] args){
        View view = new View();
        ControllerEventLoop controller = new ControllerEventLoop(view);
        view.addListener(controller);
        view.display();
    }

}
