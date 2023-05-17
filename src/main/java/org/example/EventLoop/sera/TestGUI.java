package org.example.EventLoop.sera;

import org.example.Utilities.GUI.View;
import org.example.VirtualThread.GUI.ControllerVT;

public class TestGUI {
    public static void main(String[] args){
        View view = new View();
        ControllerEventLoop controller = new ControllerEventLoop(view);
        view.addListener(controller);
        view.display();
    }

}
