package com.window;

import javax.swing.*;
import java.awt.event.*;

public class ExternalAndInternalStorageStatusDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel isFreeLabel;
    private JLabel isSizeLabel;
    private JLabel esFreeLabel;
    private JLabel esSizeLabel;

    public ExternalAndInternalStorageStatusDialog(int esSize, int esInUse, int isSize, int isInUse) {
        esSizeLabel.setText(String.format("%.2f",esSize * 1.0 / 1024) + "kB");
        esFreeLabel.setText(String.format("%.2f",(esSize - esInUse) * 1.0 / 1024) + "kB");
        isSizeLabel.setText(String.format("%.2f",isSize * 1.0 / 1024) + "kB");
        isFreeLabel.setText(String.format("%.2f",(isSize - isInUse) * 1.0 / 1024) + "kB");
        setTitle("External and internal storage status");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        ExternalAndInternalStorageStatusDialog dialog =
                new ExternalAndInternalStorageStatusDialog(1024, 42, 1024, 12);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
