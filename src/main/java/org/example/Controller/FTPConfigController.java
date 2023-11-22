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
        view.getInstallCDButton().addActionListener(new InstallCDButtonListener());
        view.getStartButton().addActionListener(new StartButtonListener());
        view.getRestartButton().addActionListener(new RestartButtonListener());
        view.getStopButton().addActionListener(new StopButtonListener());
    }

    private class InstallButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            try{
                ProcessBuilder processBuilder = new ProcessBuilder("sudo", "-S", "zypper", "install", "-y", "vsftpd");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

                writer.write("root\n");
                writer.flush();


                BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

                writer1.write("y\n");
                writer1.flush();
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
                ProcessBuilder infoBuilder = new ProcessBuilder("zypper", "info", "vsftpd");
                Process infoProcess = infoBuilder.start();

                java.util.Scanner scanner = new java.util.Scanner(infoProcess.getInputStream()).useDelimiter("\\A");
                String output = scanner.hasNext() ? scanner.next() : "";

                int exitCode = infoProcess.waitFor();

                if(exitCode == 0 && output.contains("Installed      : Yes")){
                    view.getStatusLabel().setText("Estado del servidor FTP: Vsftpd está instalado.");

                    ProcessBuilder statusBuilder = new ProcessBuilder("systemctl", "is-active", "--quiet", "vsftpd");
                    Process statusProcess = statusBuilder.start();
                    int statusExitCode = statusProcess.waitFor();

                    if(statusExitCode == 0){
                        view.getIsRunningLabel().setText("Estado del servidor FTP: Vsftpd está corriendo.");
                    }else{
                        view.getIsRunningLabel().setText("Estado del servidor FTP: Vsftpd no está corriendo.");
                    }

                }else{
                    view.getStatusLabel().setText("Estado del servidor FTP: Vsftpd no está instalado.");
                }

            } catch(IOException | InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    private class InstallCDButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Guarda el directorio de trabajo actual
                String currentDir = System.getProperty("user.dir");

                // Cambia al directorio de montaje
                System.setProperty("user.dir", "/mnt/dvd");

                // Ejecuta el comando mount
                ProcessBuilder mountBuilder = new ProcessBuilder("sudo", "-S", "mount", "/dev/cdrom", "/mnt/dvd");
                mountBuilder.redirectErrorStream(true);
                Process mountProcess = mountBuilder.start();

                PrintWriter writer = new PrintWriter(mountProcess.getOutputStream());
                writer.write("root\n");
                writer.flush();

                int mountExitCode = mountProcess.waitFor();

                if (mountExitCode != 0) {
                    System.err.println("Error al montar la imagen ISO. Código de salida: " + mountExitCode);
                    return;
                }

                // Cambia al directorio de instalación
                System.setProperty("user.dir", "/mnt/dvd/x86_64");

                // Ejecuta el comando rpm -i
                ProcessBuilder installBuilder = new ProcessBuilder("sudo", "rpm", "-i", "vsftpd-3.0.5-150400.3.6.1.x86_64.rpm");
                installBuilder.directory(new File("/mnt/dvd/x86_64"));
                Process installProcess = installBuilder.start();
                int installExitCode = installProcess.waitFor();

                if (installExitCode == 0) {
                    System.out.println("Vsftpd instalado correctamente desde el repositorio local.");
                } else {
                    System.err.println("Error al instalar Vsftpd desde el repositorio local. Código de salida:" + installExitCode);
                }

                // Restaura el directorio de trabajo original al finalizar
                System.setProperty("user.dir", currentDir);

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class StartButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e){
            try{
                ProcessBuilder startBuilder = new ProcessBuilder("sudo", "-S", "systemctl",
                        "start", "vsftpd");
                startBuilder.redirectErrorStream(true);
                Process startProcess = startBuilder.start();

                PrintWriter writer = new PrintWriter(startProcess.getOutputStream());
                writer.write("root\n");
                writer.flush();

                int startExitCode = startProcess.waitFor();

                if(startExitCode == 0){
                    System.out.println("Servicio iniciado con éxito.");
                }else{
                    System.err.println("Error al inicar el servicio. Código de salida: " + startExitCode);
                }

            }catch(IOException | InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    private class RestartButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e){
            try{
                ProcessBuilder restartBuilder = new ProcessBuilder("sudo", "-S", "systemctl",
                        "restart", "vsftpd");
                restartBuilder.redirectErrorStream(true);
                Process restartProcess = restartBuilder.start();

                PrintWriter writer = new PrintWriter(restartProcess.getOutputStream());
                writer.write("root\n");
                writer.flush();

                int restartExitCode = restartProcess.waitFor();

                if(restartExitCode == 0){
                    System.out.println("Servicio reiniciado con éxito.");
                }else{
                    System.err.println("Error al reiniciar el servicio. Código de salida: " + restartExitCode);
                }
            }catch(IOException | InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    private class StopButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e){
            try{
                ProcessBuilder stopBuilder = new ProcessBuilder("sudo", "-S", "systemctl",
                        "stop", "vsftpd");
                stopBuilder.redirectErrorStream(true);
                Process stopProcess = stopBuilder.start();

                PrintWriter writer = new PrintWriter(stopProcess.getOutputStream());
                writer.write("root\n");
                writer.flush();

                int stopExitCode = stopProcess.waitFor();

                if(stopExitCode == 0){
                    System.out.println("Servicio detenido con éxito.");
                }else{
                    System.err.println("Error al detener el servicio. Código de salida: " + stopExitCode);
                }
            }catch(IOException | InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
}