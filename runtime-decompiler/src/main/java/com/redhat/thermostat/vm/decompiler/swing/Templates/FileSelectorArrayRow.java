package com.redhat.thermostat.vm.decompiler.swing.Templates;

import javax.swing.*;
import java.awt.*;

public class FileSelectorArrayRow extends JPanel {

    GridBagConstraints gbc;

    private JTextField textField;

    private JButton removeButton;
    private JButton browseButton;

    FileSelectorArrayRow(FileSelectorArrayPanel parent, String url) {
        this.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        textField = new JTextField(url);
        textField.setPreferredSize(new Dimension(0, 32));

        try {
            ImageIcon icon = new ImageIcon(FileSelectorArrayRow.class.getResource("/icons/delete.png"));
            removeButton = new JButton(icon);
        } catch (NullPointerException e) {
            removeButton = new JButton("X");
            System.err.println("File /icons/delete.png not found. Falling back to String version.");
        }
        removeButton.addActionListener(actionEvent -> {
            parent.removeRow(this);
        });
        browseButton = new JButton("Browse");
        browseButton.addActionListener(actionEvent -> {
            JFileChooser chooser = new JFileChooser();
            int returnVar = chooser.showOpenDialog(this);
            if (returnVar == JFileChooser.APPROVE_OPTION) {
                textField.setText(chooser.getSelectedFile().getPath());
            }
        });
        removeButton.setPreferredSize(new Dimension(32, 32));

        gbc.gridx = 0;
        gbc.weighty = 1;
        gbc.weightx = 1;
        this.add(textField, gbc);
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.gridx = 1;
        this.add(removeButton, gbc);
        gbc.gridx = 2;
        this.add(Box.createHorizontalStrut(20), gbc);
        gbc.gridx = 3;
        this.add(browseButton, gbc);
    }


    public JTextField getTextField() {
        return textField;
    }

}
