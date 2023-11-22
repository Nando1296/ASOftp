package org.example.View;

import javax.swing.*;
import java.awt.*;

public class FTPConfigView extends JFrame{
    private JButton installButton;

    private JButton installCDButton;
    private JButton statusButton;
    private JLabel statusLabel;

    public FTPConfigView(){
        initializeUI();
    }

    private void initializeUI(){
        installButton = new JButton("Instalar FTP");
        installCDButton = new JButton("Instalar FTP desde CD");
        statusButton = new JButton("Verificar Estado");

        statusLabel = new JLabel("Estado del servidor FTP: ");

        JPanel panel = new JPanel();
        panel.add(installCDButton);
        panel.add(installButton);
        panel.add(statusButton);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        add(statusLabel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    public JButton getInstallButton(){
        return installButton;
    }

    public JButton getStatusButton() {
        return statusButton;
    }

    public JLabel getStatusLabel(){
        return statusLabel;
    }
}