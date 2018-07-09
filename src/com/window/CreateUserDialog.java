package com.window;

import com.exception.OSException;
import com.userManagement.User;
import com.userManagement.UserManagement;
import com.userManagement.UserTypeEnum;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class CreateUserDialog extends JFrame {
    private JTextField textField1;
    private JPanel panel1;
    private JTextField textField2;
    private JButton confirmButton;
    private JButton cancelButton;
    private JComboBox comboBox1;

    public CreateUserDialog(User performer, UserManagement userManagement) {
        getRootPane().setDefaultButton(confirmButton);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    dispose();
            }
        });
        // 只显示执行者可以建立的用户类型
        for (UserTypeEnum ute : UserTypeEnum.values())
            if (performer.getUserType().getUserMaximumFilePrivilege() >= ute.getUserMaximumFilePrivilege())
                comboBox1.addItem(ute.getUserType());

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField1.getText().trim();
                String password = textField2.getText();
                if (username.length() > 0 && password.length() > 0) {
                    try {
                        userManagement.createNewUser(performer, username, password, 0,
                                UserTypeEnum.getUserTypeByString((String) comboBox1.getSelectedItem())
                        );

                        ByteArrayOutputStream bo = new ByteArrayOutputStream();
                        ObjectOutputStream os = new ObjectOutputStream(bo);
                        os.writeObject(userManagement);
                        byte[] userByte = bo.toByteArray();
                        bo.close();
                        os.close();

                        JSONObject jo = new JSONObject();
                        jo.put("UserManagement", Base64.encodeBase64String(userByte));
                        FileOutputStream fos = new FileOutputStream(new File("UserManagement.json"));
                        fos.write(jo.toString().getBytes());
                        fos.close();
                        
                        dispose();
                    } catch (OSException e1) {
                        e1.printStackTrace();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(panel1, "用户名和密码不能为空");
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    dispose();
            }
        });

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(dimension.width / 2 - 300 / 2, dimension.height / 2 - 200 / 2, 600, 200);

        setTitle("CreateUserDialog");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
//        new CreateUserDialog();
    }
}
