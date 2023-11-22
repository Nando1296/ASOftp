package org.example.View;

import javax.swing.*;
import java.awt.*;

public class FTPConfigView extends JFrame{
    private JButton installButton;

    private JButton installCDButton;
    private JButton statusButton;
    private JButton startButton;
    private JButton restartButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JLabel isRunningLabel;

    public FTPConfigView(){
        initializeUI();
    }

    private void initializeUI(){
        installButton = new JButton("Instalar FTP");
        installCDButton = new JButton("Instalar FTP desde CD");
        statusButton = new JButton("Verificar Estado");
        startButton = new JButton("Iniciar FTP");
        restartButton = new JButton("Reiniciar FTP");
        stopButton = new JButton("Detener FTP");
        statusLabel = new JLabel("Estado del servidor FTP: ");
        isRunningLabel = new JLabel("Estado del servidor FTP: ");

        JPanel buttonStatusPanel = new JPanel();
        buttonStatusPanel.add(statusButton);
        buttonStatusPanel.add(startButton);
        buttonStatusPanel.add(restartButton);
        buttonStatusPanel.add(stopButton);

        JPanel buttonInstallPanel = new JPanel();
        buttonInstallPanel.add(installCDButton);
        buttonInstallPanel.add(installButton);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.add(statusLabel);
        statusPanel.add(isRunningLabel);
        
        setLayout(new BorderLayout());
        add(buttonInstallPanel, BorderLayout.NORTH);
        add(buttonStatusPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

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

    public JButton getInstallCDButton() {
        return installCDButton;
    }
    public JLabel getStatusLabel(){
        return statusLabel;
    }
    public JButton getStartButton() {
        return startButton;
    }

    public JButton getRestartButton() {
        return restartButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JLabel getIsRunningLabel(){
        return isRunningLabel;
    }
}