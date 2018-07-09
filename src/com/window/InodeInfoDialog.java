package com.window;

import com.fileSystem.FilePrivilege;
import com.fileSystem.FileTypeEnum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

public class InodeInfoDialog extends JFrame {
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

    /**
     *
     * @param size      文件大小
     * @param fileType  文件类型
     * @param UID       用户UID
     * @param privilege 文件权限
     * @param atime     上次打开时间
     * @param mtime     内容上次变动时间
     * @param ctime     inode上次变动时间
     */
    public InodeInfoDialog(int size, FileTypeEnum fileType, int UID,
                           int privilege, Date atime, Date mtime, Date ctime) {
        this.size.setText("" + size);
        this.type.setText(fileType.getFileTypeDesc());
        this.UID.setText("" + UID);
        this.privilege.setText(FilePrivilege.privilegeToString(privilege));
        access1.setText(atime.toString());
        modify.setText(mtime.toString());
        change.setText(ctime.toString());
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                    dispose();
            }
        });

        setTitle("InodeInfoDialog");
        setContentPane(panel1);
        setResizable(false);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(dimension.width / 2 - 400 / 2, dimension.height / 2 - 200 / 2, 400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new InodeInfoDialog(4096, FileTypeEnum.INODE_IS_REGULAR_FILE, 99, 308, new Date(), new Date(), new Date());
    }
}
