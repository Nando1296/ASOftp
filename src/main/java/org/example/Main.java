package org.example;

import javax.swing.*;
import org.example.View.FTPConfigView;
import org.example.Model.FTPConfigModel;
import org.example.Controller.FTPConfigController;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            FTPConfigModel model = new FTPConfigModel();
            FTPConfigView view = new FTPConfigView();
            FTPConfigController controller = new FTPConfigController(model, view);

            view.setVisible(true);
        });
    }
}