package org.example.View;

import javax.swing.*;
import java.awt.*;

public class FTPConfigView extends JFrame {
    private JButton installButton;

    private JButton installCDButton;
    private JButton statusButton;
    private JButton startButton;
    private JButton restartButton;
    private JButton stopButton;
    private JButton mostrarConf;
    private JLabel statusLabel;
    private JLabel isRunningLabel;
    private JLabel changeStatusLabel;
    private JLabel statusInstallFtp;

    public FTPConfigView() {
        initializeUI();
    }

    private void initializeUI() {
        installButton = new JButton("Instalar FTP");
        installCDButton = new JButton("Instalar FTP desde CD");
        statusButton = new JButton("Verificar Estado");
        startButton = new JButton("Iniciar FTP Online");
        restartButton = new JButton("Reiniciar FTP");
        stopButton = new JButton("Detener FTP");
        mostrarConf = new JButton("Mostrar Configuración");
        statusLabel = new JLabel("Estado del servidor FTP: ");
        isRunningLabel = new JLabel("Estado del servidor FTP: ");
        changeStatusLabel = new JLabel("En espera...");
        statusInstallFtp = new JLabel("Estado de la instalación: ");        

        Color colorCeleste = new Color(173, 216, 230);
        installButton.setBackground(colorCeleste);
        installCDButton.setBackground(colorCeleste);
        statusButton.setBackground(colorCeleste);
        startButton.setBackground(colorCeleste);
        restartButton.setBackground(colorCeleste);
        stopButton.setBackground(colorCeleste);
        mostrarConf.setBackground(colorCeleste);

        JPanel buttonStatusPanel = new JPanel();
        buttonStatusPanel.add(startButton);
        buttonStatusPanel.add(restartButton);
        buttonStatusPanel.add(stopButton);
        buttonStatusPanel.add(changeStatusLabel);
        buttonStatusPanel.add(mostrarConf);

        JPanel buttonInstallPanel = new JPanel();
        buttonInstallPanel.add(installCDButton);
        buttonInstallPanel.add(installButton);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.add(statusButton);
        statusPanel.add(statusLabel);
        statusPanel.add(isRunningLabel);
        statusPanel.add(statusInstallFtp);

        setLayout(new BorderLayout());
        add(buttonInstallPanel, BorderLayout.NORTH);
        add(buttonStatusPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; // Primera columna
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        add(installCDButton, gbc);
        gbc.gridy++;
        add(installButton, gbc);
        gbc.gridy++;
        add(statusInstallFtp, gbc);
        gbc.gridy++;
        add(mostrarConf, gbc);
        gbc.gridy++;
        add(statusButton, gbc);
        gbc.gridy++;
        add(statusLabel, gbc);
        gbc.gridy++;
        add(isRunningLabel, gbc);

        gbc.gridx = 1; // Segunda columna
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(startButton, gbc);
        gbc.gridy++;
        add(restartButton, gbc);
        gbc.gridy++;
        add(stopButton, gbc);
        gbc.gridy++;
        add(changeStatusLabel, gbc);
        gbc.gridy++;

        setVisible(true);
    }

    public JButton getInstallButton() {
        return installButton;
    }

    public JButton getStatusButton() {
        return statusButton;
    }

    public JButton getInstallCDButton() {
        return installCDButton;
    }

    public JLabel getStatusLabel() {
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

    public JLabel getIsRunningLabel() {
        return isRunningLabel;
    }

    public JLabel getChangeStatusLabel() {
        return changeStatusLabel;
    }
    public JButton getMostrarConf(){
        return mostrarConf;
    }

    public JLabel getStatusInstallFtp(){
        return statusInstallFtp;
        }
}