package com.window;

import com.exception.OSException;
import com.userManagement.UserManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Entrance {
    private JFrame entranceFrame;
    private JTextField identifierField;
    private JPasswordField passwordField;
    private JButton confirmButton;
    private JButton cancelButton;
    private JLabel identifierLabel;
    private JLabel passwordLabel;

    private Entrance() {
        GridBagLayout layout = new GridBagLayout();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        entranceFrame = new JFrame("login");
        entranceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        entranceFrame.setBounds(
                dimension.width / 2 - 350 / 2, dimension.height / 2 - 230 / 2, 350, 230
        );  // 窗口在屏幕上居中显示
        entranceFrame.setResizable(false);
        entranceFrame.setLayout(layout);

        identifierLabel = new JLabel("ID");
        entranceFrame.add(identifierLabel);

        identifierField = new JTextField("Input UID or USER NAME to login");
        identifierField.setForeground(Color.GRAY);
        entranceFrame.add(identifierField);
        identifierField.setCaretPosition(0);
        identifierField.addKeyListener(new KeyAdapter() {
            @Override
            // 对ID框进行检查，如果内容为空，就显示占位符
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char newChar = e.getKeyChar();
                if (identifierField.getText().equals("Input UID or USER NAME to login")) {
                    identifierField.setText("");
                    identifierField.setForeground(Color.BLACK);
                } else if (identifierField.getText().length() == 0) {
                    identifierField.setText("Input UID or USER NAME to login");
                    identifierField.setForeground(Color.GRAY);
                    identifierField.setCaretPosition(0);
                } else {
                    identifierField.setForeground(Color.BLACK);
                }

            }

            @Override
            // 在ID框显示占位符时，总会将错位的光标移回最左侧，确保占位符的效果
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (identifierField.getText().equals("Input UID or USER NAME to login")) {
                    identifierField.setCaretPosition(0);
                }
            }
        });
        identifierField.addMouseListener(new MouseAdapter() {
            @Override
            // 作用同上
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (identifierField.getText().equals("Input UID or USER NAME to login")) {
                    identifierField.setCaretPosition(0);
                }
            }
        });

        passwordLabel = new JLabel("密码");
        entranceFrame.add(passwordLabel);

        passwordField = new JPasswordField();
        entranceFrame.add(passwordField);

        confirmButton = new JButton("登录");
        entranceFrame.add(confirmButton);
        // 确认按钮被触发后的操作
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String usernameOrUID = identifierField.getText().trim();
                    String password = new String(passwordField.getPassword());

                    // 正则表达式匹配确认是否有非数字字符
                    String pattern = "[\\D]";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(usernameOrUID);
                    int count = 0;
                    while(m.find()) count++;
                    boolean identifierIsUsername = count > 0;   // 若存在非数字字符，则认为输入的为用户名，否则认为是UID

                    // 构造UserManagement实例，进行用户身份验证
                    UserManagement um = new UserManagement(new File("UserManagement.json"));
                    if(identifierIsUsername){
                        if(um.userLogin(usernameOrUID, password))
                            new mainWindow(um, um.findUser(usernameOrUID));
                    }else{
                        if(um.userLogin(Integer.valueOf(usernameOrUID), password))
                            new mainWindow(um, um.findUser(Integer.valueOf(usernameOrUID)));
                    }
//                    entranceFrame.setVisible(false); // 通过身份验证后隐藏登录窗口
                    entranceFrame.dispose();

                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (OSException e1) {
                    e1.printStackTrace();
                }
            }
        });

        cancelButton = new JButton("取消");
        entranceFrame.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // 设置各个组件的位置
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        setGridBagConstraints(gridBagConstraints, 1, 2, 0, 0, 0);
        layout.setConstraints(identifierLabel, gridBagConstraints);

        setGridBagConstraints(gridBagConstraints, 0, 1, 0, 3, 0);
        layout.setConstraints(identifierField, gridBagConstraints);

        setGridBagConstraints(gridBagConstraints, 1, 2, 0, 0, 1);
        layout.setConstraints(passwordLabel, gridBagConstraints);

        setGridBagConstraints(gridBagConstraints, 0, 1, 0, 3, 1);
        layout.setConstraints(passwordField, gridBagConstraints);

        setGridBagConstraints(gridBagConstraints, 1, 3, 0, 0, 2);
        layout.setConstraints(confirmButton, gridBagConstraints);

        setGridBagConstraints(gridBagConstraints, 0, 3, 0, 5, 2);
        layout.setConstraints(cancelButton, gridBagConstraints);


        entranceFrame.setVisible(true);
        entranceFrame.getRootPane().setDefaultButton(confirmButton);
    }

    private void setGridBagConstraints(GridBagConstraints gridBagConstraints, int gridwidth, double weightx,
                                       double weighty, int gridx, int gridy) {
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridwidth = gridwidth;
        gridBagConstraints.weightx = weightx;
        gridBagConstraints.weighty = weighty;
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
    }

//    public static void main(String[] args) {
//        new Entrance();
//    }
}
