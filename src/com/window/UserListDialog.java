package com.window;

import com.userManagement.User;
import com.userManagement.UserManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class UserListDialog extends JFrame {
    private JPanel panel1;
    private JScrollPane scroolPane;
    private JTextArea textArea1;

    public UserListDialog(ArrayList<User> userList) {
        setTitle("UserListDialog");
        for (User user : userList) {
            textArea1.setText(textArea1.getText() +
                    user.getUID() +
                    " " +
                    user.getUserName() +
                    " " +
                    user.getUserType().getUserType() +
                    "\n"
            );
        }
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(dimension.width / 2 - 600 / 2, dimension.height / 2 - 300 / 2, 600, 200);
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    dispose();
            }
        });
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
//        new UserListDialog();
    }
}
