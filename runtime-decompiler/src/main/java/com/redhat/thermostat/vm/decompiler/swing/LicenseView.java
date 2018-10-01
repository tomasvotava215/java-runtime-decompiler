package com.redhat.thermostat.vm.decompiler.swing;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LicenseView extends JDialog {

    JTextArea licenseTextArea;
    JScrollPane scrollPane;

    LicenseView(MainFrameView mainFrameView){

        licenseTextArea = new JTextArea();
        scrollPane = new JScrollPane(licenseTextArea);

        InputStream in = getClass().getResourceAsStream("/LICENSE");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        reader.lines().forEach(s -> sb.append(s).append('\n'));
        licenseTextArea.setText(sb.toString());
        licenseTextArea.setEditable(false);
        licenseTextArea.setCaretPosition(0);

        this.setTitle("License");
        this.setSize(new Dimension(650,600));
        this.setMinimumSize(new Dimension(250,330));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.setLocationRelativeTo(mainFrameView.getMainFrame());
        this.setVisible(true);
    }

}
