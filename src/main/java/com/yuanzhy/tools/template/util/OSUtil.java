package com.yuanzhy.tools.template.util;

import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

/**
 * OSUtil
 * @author yuanzhy
 */
public class OSUtil {
    private static final PosixFilePermission[] decodeMap;
    
    private static final String osName = System.getProperty("os.name").toLowerCase();

    static {
        decodeMap = new PosixFilePermission[] { PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_WRITE,
                PosixFilePermission.OTHERS_READ, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_WRITE,
                PosixFilePermission.GROUP_READ, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_READ };
    }

    public static boolean isWindows() {
        return osName.contains("windows");
    }
    
    public static boolean isLinux() {
        return osName.contains("linux");
    }
    
    public static boolean isMacOS() {
        return osName.contains("mac");
    }
    
    public static boolean isUnix() {
        return osName.contains("unix")
                || osName.contains("solaris")
                || osName.contains("sunos")
                || osName.contains("hp-ux")
                || osName.contains("aix")
                || osName.contains("freebsd");
    }
    
    public static String getVersion() {
        return System.getProperty("os.version");
    }

    public static Set<PosixFilePermission> posixFilePermissions(int mode) {
        int mask = 1;
        EnumSet<PosixFilePermission> perms = EnumSet.noneOf(PosixFilePermission.class);
        PosixFilePermission[] arg5 = decodeMap;
        int arg4 = decodeMap.length;

        for (int arg3 = 0; arg3 < arg4; ++arg3) {
            PosixFilePermission flag = arg5[arg3];
            if (flag != null && (mask & mode) != 0) {
                perms.add(flag);
            }

            mask <<= 1;
        }

        return perms;
    }
}
