package org.example.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import org.example.View.FTPConfigView;
import org.example.Model.FTPConfigModel;


public class FTPConfigController{
    private FTPConfigView view;
    private FTPConfigModel model;

    public FTPConfigController(FTPConfigModel model, FTPConfigView view){
        this.model = model;
        this.view = view;

        view.getInstallButton().addActionListener(new InstallButtonListener());
        view.getStatusButton().addActionListener(new StatusButtonListener());
    }

    private class InstallButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            try{
                ProcessBuilder processBuilder = new ProcessBuilder("sudo","zypper", "install", "-y", "vsftpd");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

                writer.write("y\n");
                writer.flush();
                java.util.Scanner scanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
                String output = scanner.hasNext() ? scanner.next() : "";

                int exitCode = process.waitFor();

                if(exitCode == 0){
                    System.out.println("Vsftpd instalado correctamente.");
                }else{
                    System.err.println("Error al instalar Vsftpd. Código de salida: "
                            + exitCode);
                    System.err.println("Salida del proceso: " + output);
                }
            } catch(IOException | InterruptedException ex){
                ex.printStackTrace();;
            }

        }
    }

    private class StatusButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e){
            try{
                ProcessBuilder processBuilder = new ProcessBuilder("zypper", "info", "vsftpd");
                Process process = processBuilder.start();

                java.util.Scanner scanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
                String output = scanner.hasNext() ? scanner.next() : "";

                int exitCode = process.waitFor();

                if(exitCode == 0 && output.contains("Installed      : Yes")){
                    view.getStatusLabel().setText("Estado del servidor FTP: Vsftpd está instalado.");
                }else{
                    view.getStatusLabel().setText("Estado del servidor FTP: Vsftpd no está instalado.");
                }

            } catch(IOException | InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

}