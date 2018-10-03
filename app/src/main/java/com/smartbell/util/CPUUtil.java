package com.smartbell.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * CPUUtil
 */
@SuppressWarnings("UnusedDeclaration")
public class CPUUtil {
    private static int sCpuCoreNum = -1;
    public static int getNumCores() {
        if(sCpuCoreNum != -1) {
            return sCpuCoreNum;
        }

        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }
        }

        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());

            if (files.length > 0) {
                sCpuCoreNum = files.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sCpuCoreNum;
    }
}
