package com.window;

import javax.swing.*;

public class InodeInfoDialog extends JFrame{
    private JLabel sizeLabel;
    private JLabel UIDLabel;
    private JLabel typeLabel;
    private JLabel privilegeLabel;
    private JLabel size;
    private JLabel UID;
    private JLabel type;
    private JLabel privilege;
    private JPanel panel1;
    private JLabel accessLabel1;
    private JLabel modifyLabel;
    private JLabel changeLabel;
    private JLabel access1;
    private JLabel modify;
    private JLabel change;

    public InodeInfoDialog() {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("InodeInfoDialog");
        frame.setContentPane(new InodeInfoDialog().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
