package org.example.Controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.example.View.FTPConfigView;
import org.example.Model.FTPConfigModel;
import org.example.View.PasswordDialog;

import javax.swing.*;

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
        view.getMostrarConf().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarFormulario();
            }
        });
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
                    SwingUtilities.invokeLater(() -> view.getStatusInstallFtp().setText("Estado: Vsftpd instalado correctamente."));
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

    private static void mostrarFormulario() {
        try{
            JFrame formulario = new JFrame("Formulario de Configuración");
            formulario.setSize(800, 600);
            formulario.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            File archivo = new File("/etc/vsftpd.conf");

            if (!archivo.exists() || archivo.length() == 0) {
                System.out.println("El archivo no se encontró o está vacío.");

                return;
            }

            JPanel panelPrincipal = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);

            Map<Integer, JLabel> labelsMap = new HashMap<>();
            Map<Integer, JComponent> componentMap = new HashMap<>();

            // Números de línea que deseas recuperar
            int[] lineNumbers = {19, 23, 27, 53, 80, 88, 118, 150, 171, 179, 184, 189, 194, 199, 200};

            for (int lineNumber : lineNumbers) {
                String linea = obtenerLineaPorNumero( "/etc/vsftpd.conf", lineNumber);
                String variable = obtenerNombreVariable(linea);
                String valor = obtenerValorVariable(linea);

                JLabel label = new JLabel(variable );
                JComponent componente;

                if ("YES".equals(valor) || "NO".equals(valor)) {
                    JComboBox<String> comboBox = new JComboBox<>(new String[]{"YES", "NO"});
                    comboBox.setSelectedItem(valor);
                    comboBox.setPreferredSize(new Dimension(150, 25));
                    componente = comboBox;
                } else {
                    JTextField textField = new JTextField(valor);
                    textField.setPreferredSize(new Dimension(150, 25));
                    componente = textField;
                }

                panelPrincipal.add(label, gbc);
                gbc.gridx++;
                panelPrincipal.add(componente, gbc);

                labelsMap.put(lineNumber, label);
                componentMap.put(lineNumber, componente);

                gbc.gridx = 0;
                gbc.gridy++;
            }

            // Botones de Guardar y Cancelar
            JButton guardarButton = new JButton("Guardar");
            guardarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Guarda los cambios en el archivo
                    guardarCambios(labelsMap, componentMap);
                    formulario.dispose(); // Cierra la ventana del formulario
                }
            });

            JButton cancelarButton = new JButton("Cancelar");
            cancelarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    formulario.dispose(); // Cierra la ventana del formulario
                }
            });

            // Agregar componentes a los paneles
            JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            botonesPanel.add(guardarButton);
            botonesPanel.add(cancelarButton);

            formulario.add(panelPrincipal, BorderLayout.CENTER);
            formulario.add(botonesPanel, BorderLayout.SOUTH);

            formulario.setVisible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void guardarCambios(Map<Integer, JLabel> labelsMap, Map<Integer, JComponent> componentMap) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sudo","sed", "-i");

            // Modifica las líneas según los cambios en el formulario
            for (Map.Entry<Integer, JLabel> entry : labelsMap.entrySet()) {
                int lineNumber = entry.getKey();
                JLabel label = entry.getValue();
                JComponent component = componentMap.get(lineNumber);

                String nuevaLinea = label.getText() + "=";

                if (component instanceof JTextField) {
                    nuevaLinea += ((JTextField) component).getText();
                } else if (component instanceof JComboBox) {
                    nuevaLinea += ((JComboBox<?>) component).getSelectedItem().toString();
                }

                // Agrega el número de línea al comando sed
                processBuilder.command().add("-e");
                processBuilder.command().add(lineNumber + "s/.*/" + nuevaLinea + "/");

            }

            // Agrega el nombre del archivo al comando sed
            processBuilder.command().add( "/etc/vsftpd.conf");

            // Ejecuta el comando
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error al ejecutar sed. Código de salida: " + exitCode);
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            } else {
                System.out.println("Cambios aplicados correctamente.");
            }
            reiniciarServicioFTP();
            System.out.print("linea nueva: " + processBuilder.command());


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    private static void reiniciarServicioFTP() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sudo", "service", "vsftpd","restart" );
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.err.println("Error al reiniciar el servicio. Código de salida: " + exitCode);
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            } else {
                System.out.println("Servicio reiniciado correctamente.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }


    private static String obtenerLineaPorNumero(String rutaArchivo, int numeroLinea) {
        try {
            Process process = Runtime.getRuntime().exec("sudo sed -n " + numeroLinea + "p " + rutaArchivo);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String obtenerNombreVariable(String linea) {
        if (linea != null && linea.contains("=")) {
            return linea.split("=")[0];
        }
        return "";
    }

    private static String obtenerValorVariable(String linea) {
        if (linea != null && linea.contains("=")) {
            return linea.split("=")[1];
        }
        return "";
    }
}