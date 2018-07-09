package com.fileSystem;

import com.userManagement.User;
import com.userManagement.UserTypeEnum;

public class FilePrivilege {
    /**
     *
     * @param privilege: 整数形式的权限
     * @return 字符形式的权限
     * @apiNote 完整权限格式：rwvrwvrwv ——(su)读写见(user)读写见(guest)读写见
     */
    public static String privilegeToString(int privilege) {
        String opr = "rwv";
        StringBuilder privilegeString = new StringBuilder();
        int cmp = 1 << 8;
        for (int i = 0; i < 9; i++) {
            if ((privilege & cmp) > 0) {
                privilegeString.append(opr.charAt(i % 3));
            } else {
                privilegeString.append('-');
            }
            cmp >>= 1;
        }
        return privilegeString.toString();
    }

    // 各个权限对应的比特
    private static final int SU_READ =          1 << 8;
    private static final int SU_WRITE =         1 << 7;
    private static final int SU_VISIBLE =       1 << 6;
    private static final int USER_READ =        1 << 5;
    private static final int USER_WRITE =       1 << 4;
    private static final int USER_VISIBLE =     1 << 3;
    private static final int GUEST_READ =       1 << 2;
    private static final int GUEST_WRITE =      1 << 1;
    private static final int GUEST_VISIBLE =         1;

    /**
     *
     * @param opt r, w 或 v，用于指定欲对文件执行的操作
     * @param inode inode实例
     * @param user 当前用户
     * @return 返回参数指定的用户是否可以对参数指定的操作
     */
    public static boolean isOKToDo(char opt, INode inode, User user) {
        int privilege = inode.getPrivilege();
        UserTypeEnum userTypeEnum = user.getUserType();
        switch (opt) {
            case 'r':
            case 'R':
                if (userTypeEnum == UserTypeEnum.OS_SUPERUSER)
                    return (privilege & SU_READ) > 0;
                if (userTypeEnum == UserTypeEnum.OS_USER)
                    return (privilege & USER_READ) > 0;
                if (userTypeEnum == UserTypeEnum.OS_GUEST)
                    return (privilege & GUEST_READ) > 0;
                break;
            case 'w':
            case 'W':
                if (userTypeEnum == UserTypeEnum.OS_SUPERUSER)
                    return (privilege & SU_WRITE) > 0;
                if (userTypeEnum == UserTypeEnum.OS_USER)
                    return (privilege & USER_WRITE) > 0;
                if (userTypeEnum == UserTypeEnum.OS_GUEST)
                    return (privilege & GUEST_WRITE) > 0;
                break;
            case 'v':
            case 'V':
                if (userTypeEnum == UserTypeEnum.OS_SUPERUSER)
                    return (privilege & SU_VISIBLE) > 0;
                if (userTypeEnum == UserTypeEnum.OS_USER)
                    return (privilege & USER_VISIBLE) > 0;
                if (userTypeEnum == UserTypeEnum.OS_GUEST)
                    return (privilege & GUEST_VISIBLE) > 0;
                break;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(privilegeToString(510));
    }
}
