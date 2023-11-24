package org.example.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PasswordDialog extends JDialog {
    private JPasswordField passwordField;
    private boolean isConfirmed;

    public PasswordDialog(Frame parent) {
        super(parent, "Ingrese la contraseña", true);

        isConfirmed = false;

        passwordField = new JPasswordField();
        passwordField.setEchoChar('*'); // Configura el carácter de ocultación (en este caso, asterisco)

        JButton okButton = new JButton("Aceptar");
        JButton cancelButton = new JButton("Cancelar");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isConfirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isConfirmed = false;
                dispose();
            }
        });

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Contraseña:"));
        panel.add(passwordField);
        panel.add(okButton);
        panel.add(cancelButton);

        getContentPane().add(panel, BorderLayout.CENTER);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    public String getPassword() {
        return isConfirmed ? new String(passwordField.getPassword()) : null;
    }
}
