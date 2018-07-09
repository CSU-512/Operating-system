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

public class EntranceForm extends JFrame {
    private JTextField textField1;
    private JPanel panel1;
    private JPasswordField textField2;
    private JButton 确定Button;
    private JButton button2;

    public static void main(String[] args) {
        new EntranceForm();
    }

    public EntranceForm() {
        setTitle("Login");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
        GridBagLayout layout = new GridBagLayout();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(dimension.width / 2 - 300 / 2, dimension.height / 2 - 200 / 2, 300, 200);
        setResizable(false);

        textField1.setText("Input UID or USER NAME to login");
        textField1.setForeground(Color.GRAY);
        textField1.setCaretPosition(0);
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (textField1.getText().equals("Input UID or USER NAME to login")) {
                    textField1.setText("");
                    textField1.setForeground(Color.BLACK);
                } else if (textField1.getText().length() == 0) {
                    textField1.setText("Input UID or USER NAME to login");
                    textField1.setForeground(Color.GRAY);
                    textField1.setCaretPosition(0);
                } else {
                    textField1.setForeground(Color.BLACK);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (textField1.getText().equals("Input UID or USER NAME to login")) {
                    textField1.setCaretPosition(0);
                }
            }
        });
        textField1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (textField1.getText().equals("Input UID or USER NAME to login")) {
                    textField1.setCaretPosition(0);
                }
            }
        });
        确定Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String usernameOrUID = textField1.getText().trim();
                    String password = new String(textField2.getPassword());

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
                            new mainWindow();
                    }else{
                        if(um.userLogin(Integer.valueOf(usernameOrUID), password))
                            new mainWindow();
                    }
//                    entranceFrame.setVisible(false); // 通过身份验证后隐藏登录窗口
                    dispose();

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

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        getRootPane().setDefaultButton(确定Button);
    }
}
