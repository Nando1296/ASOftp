package org.example.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import org.example.View.FTPConfigView;
import org.example.Model.FTPConfigModel;
import org.example.View.PasswordDialog;

public class FTPConfigController {
    private FTPConfigView view;
    private FTPConfigModel model;

    public FTPConfigController(FTPConfigModel model, FTPConfigView view) {
        this.model = model;
        this.view = view;

        view.getInstallButton().addActionListener(new InstallButtonListener());
        view.getStatusButton().addActionListener(new StatusButtonListener());
        view.getInstallCDButton().addActionListener(new InstallCDButtonListener());
        view.getStartButton().addActionListener(new StartButtonListener());
        view.getRestartButton().addActionListener(new RestartButtonListener());
        view.getStopButton().addActionListener(new StopButtonListener());
    }

    private class InstallButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Ventana de entrada de contraseña
                String password = getPasswordFromUser();

                if (password == null || password.isEmpty()) {
                    // El usuario canceló la entrada de la contraseña
                    System.out.println("Operación cancelada por el usuario.");
                    return;
                }

                ProcessBuilder processBuilder = new ProcessBuilder("sudo", "-S", "zypper", "install", "-y", "vsftpd");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                // Escribir la contraseña en el proceso
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                    writer.write(password + "\n");
                    writer.flush();
                }

                java.util.Scanner scanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
                String output = scanner.hasNext() ? scanner.next() : "";

                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    System.out.println("Vsftpd instalado correctamente.");
                } else {
                    System.err.println("Error al instalar Vsftpd. Código de salida: " + exitCode);
                    System.err.println("Salida del proceso: " + output);
                }
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        private String getPasswordFromUser() {
            PasswordDialog passwordDialog = new PasswordDialog(null);
            passwordDialog.setVisible(true);

            return passwordDialog.getPassword();
        }
    }

    private class StatusButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ProcessBuilder infoBuilder = new ProcessBuilder("zypper", "info", "vsftpd");
                Process infoProcess = infoBuilder.start();

                java.util.Scanner scanner = new java.util.Scanner(infoProcess.getInputStream()).useDelimiter("\\A");
                String output = scanner.hasNext() ? scanner.next() : "";

                int exitCode = infoProcess.waitFor();

                if (exitCode == 0 && output.contains("Installed      : Yes")) {
                    view.getStatusLabel().setText("Estado del servidor FTP: Vsftpd está instalado.");

                    ProcessBuilder statusBuilder = new ProcessBuilder("systemctl", "is-active", "--quiet", "vsftpd");
                    Process statusProcess = statusBuilder.start();
                    int statusExitCode = statusProcess.waitFor();

                    if (statusExitCode == 0) {
                        view.getIsRunningLabel().setText("Estado del servidor FTP: Vsftpd está corriendo.");
                    } else {
                        view.getIsRunningLabel().setText("Estado del servidor FTP: Vsftpd no está corriendo.");
                    }

                } else {
                    view.getStatusLabel().setText("Estado del servidor FTP: Vsftpd no está instalado.");
                }

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public class InstallCDButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Solicita la contraseña al usuario
                String password = getPasswordFromUser();

                if (password == null || password.isEmpty()) {
                    // El usuario canceló la entrada de la contraseña
                    System.out.println("Operación cancelada por el usuario.");
                    return;
                }

                // Guarda el directorio de trabajo actual
                String currentDir = System.getProperty("user.dir");

                // Cambia al directorio de montaje
                System.setProperty("user.dir", "/mnt");

                // Ejecuta el comando mount
                ProcessBuilder mountBuilder = new ProcessBuilder("sudo", "-S", "mount", "/dev/cdrom", "/mnt");
                mountBuilder.redirectErrorStream(true);
                Process mountProcess = mountBuilder.start();

                // Escribe la contraseña en el proceso
                try (PrintWriter writer = new PrintWriter(mountProcess.getOutputStream())) {
                    writer.write(password + "\n");
                    writer.flush();
                }

                int mountExitCode = mountProcess.waitFor();

                if (mountExitCode != 0) {
                    System.err.println("Error al montar la imagen ISO. Código de salida: " + mountExitCode);
                    return;
                }

                // Ejecuta el comando rpm -i
                ProcessBuilder installBuilder = new ProcessBuilder("sudo", "rpm", "-i",
                        "vsftpd-3.0.5-150400.3.6.1.x86_64.rpm");
                installBuilder.directory(new File("/mnt/x86_64"));
                Process installProcess = installBuilder.start();
                int installExitCode = installProcess.waitFor();

                if (installExitCode == 0) {
                    System.out.println("Vsftpd instalado correctamente desde el repositorio local.");
                } else {
                    System.err.println(
                            "Error al instalar Vsftpd desde el repositorio local. Código de salida:" + installExitCode);
                }

                // Desmonta la ISO
                ProcessBuilder unmountBuilder = new ProcessBuilder("sudo", "-S", "umount", "/mnt");
                unmountBuilder.redirectErrorStream(true);
                Process unmountProcess = unmountBuilder.start();

                // Escribe la contraseña en el proceso
                try (PrintWriter writer = new PrintWriter(unmountProcess.getOutputStream())) {
                    writer.write(password + "\n");
                    writer.flush();
                }

                int unmountExitCode = unmountProcess.waitFor();

                if (unmountExitCode != 0) {
                    System.err.println("Error al desmontar la imagen ISO. Código de salida: " + unmountExitCode);
                } else {
                    System.out.println("Imagen ISO desmontada correctamente.");
                }

                // Restaura el directorio de trabajo original al finalizar
                System.setProperty("user.dir", currentDir);

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        private String getPasswordFromUser() {
            PasswordDialog passwordDialog = new PasswordDialog(null);
            passwordDialog.setVisible(true);

            return passwordDialog.getPassword();
        }
    }

    public class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Obtén la contraseña del usuario
                String password = getPasswordFromUser();

                if (password == null || password.isEmpty()) {
                    // El usuario canceló la entrada de la contraseña
                    System.out.println("Operación cancelada por el usuario.");
                    return;
                }

                ProcessBuilder startBuilder = new ProcessBuilder("sudo", "-S", "systemctl", "start", "vsftpd");
                startBuilder.redirectErrorStream(true);
                Process startProcess = startBuilder.start();

                // Escribe la contraseña en el proceso
                try (PrintWriter writer = new PrintWriter(startProcess.getOutputStream())) {
                    writer.write(password + "\n");
                    writer.flush();
                }

                int startExitCode = startProcess.waitFor();

                if (startExitCode == 0) {
                    System.out.println("Servicio iniciado con éxito.");
                    view.getChangeStatusLabel().setText("Servicio iniciado con éxito.");
                } else {
                    System.err.println("Error al iniciar el servicio. Código de salida: " + startExitCode);
                }

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        private String getPasswordFromUser() {
            PasswordDialog passwordDialog = new PasswordDialog(null);
            passwordDialog.setVisible(true);

            return passwordDialog.getPassword();
        }
    }

    public class RestartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Obtén la contraseña del usuario
                String password = getPasswordFromUser();

                if (password == null || password.isEmpty()) {
                    // El usuario canceló la entrada de la contraseña
                    System.out.println("Operación cancelada por el usuario.");
                    return;
                }

                ProcessBuilder restartBuilder = new ProcessBuilder("sudo", "-S", "systemctl", "restart", "vsftpd");
                restartBuilder.redirectErrorStream(true);
                Process restartProcess = restartBuilder.start();

                // Escribe la contraseña en el proceso
                try (PrintWriter writer = new PrintWriter(restartProcess.getOutputStream())) {
                    writer.write(password + "\n");
                    writer.flush();
                }

                int restartExitCode = restartProcess.waitFor();

                if (restartExitCode == 0) {
                    System.out.println("Servicio reiniciado con éxito.");
                    view.getChangeStatusLabel().setText("Servicio reiniciado con éxito.");
                } else {
                    System.err.println("Error al reiniciar el servicio. Código de salida: " + restartExitCode);
                }

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        private String getPasswordFromUser() {
            PasswordDialog passwordDialog = new PasswordDialog(null);
            passwordDialog.setVisible(true);

            return passwordDialog.getPassword();
        }
    }

    public class StopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Obtén la contraseña del usuario
                String password = getPasswordFromUser();

                if (password == null || password.isEmpty()) {
                    // El usuario canceló la entrada de la contraseña
                    System.out.println("Operación cancelada por el usuario.");
                    return;
                }

                ProcessBuilder stopBuilder = new ProcessBuilder("sudo", "-S", "systemctl", "stop", "vsftpd");
                stopBuilder.redirectErrorStream(true);
                Process stopProcess = stopBuilder.start();

                // Escribe la contraseña en el proceso
                try (PrintWriter writer = new PrintWriter(stopProcess.getOutputStream())) {
                    writer.write(password + "\n");
                    writer.flush();
                }

                int startExitCode = stopProcess.waitFor();

                if (startExitCode == 0) {
                    System.out.println("Servicio detenido con éxito.");
                    view.getChangeStatusLabel().setText("Servicio detenido con éxito.");
                } else {
                    System.err.println("Error al detener el servicio. Código de salida: " + startExitCode);
                }

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        private String getPasswordFromUser() {
            PasswordDialog passwordDialog = new PasswordDialog(null);
            passwordDialog.setVisible(true);

            return passwordDialog.getPassword();
        }
    }
}