package com.window;

import com.exception.OSException;
import com.fileSystem.FilePrivilege;
import com.fileSystem.INode;
import com.userManagement.User;
import com.userManagement.UserTypeEnum;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChangePrivilegeDialog extends JFrame {
    private JButton confirmButton;
    private JButton cancelButton;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JCheckBox checkBox4;
    private JCheckBox checkBox5;
    private JCheckBox checkBox6;
    private JCheckBox checkBox7;
    private JCheckBox checkBox8;
    private JCheckBox checkBox9;
    private JPanel panel1;

    public ChangePrivilegeDialog(INode inode, User performer, int privilege) throws OSException {
        JCheckBox[] jCheckBoxes = {checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7,
                checkBox8, checkBox9};
        for (int i = 0; i < 9; i++) {
            jCheckBoxes[i].setSelected((inode.getPrivilege() & (1 << (8 - i))) > 0);
            if((1<<(8-i)) > performer.getUserType().getUserMaximumFilePrivilege())
                jCheckBoxes[i].setEnabled(false);
//            jCheckBoxes[i].setSelected((FilePrivilege.stringToPrivilege("rwv--v--v") & (1 << (8 - i))) > 0);
//            if((1<<(8-i)) > UserTypeEnum.OS_GUEST.getUserMaximumFilePrivilege())
//                jCheckBoxes[i].setEnabled(false);
        }

        getRootPane().setDefaultButton(confirmButton);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    dispose();
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int privilege = 0;
                for(int i = 0; i < 9; i++) {
                    System.out.println(jCheckBoxes[i].isSelected());
                    if(jCheckBoxes[i].isSelected())
                        privilege |= (1 << (8-i));
                }
                try {
                    inode.setPrivilege(performer, privilege);
                } catch (OSException e1) {
                    e1.printStackTrace();
                }
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setTitle("ChangePrivilegeDialog");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) throws OSException {
//        new ChangePrivilegeDialog();
    }
}
