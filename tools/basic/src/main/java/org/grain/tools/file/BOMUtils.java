package org.grain.tools.file;

import java.io.File;
import java.io.IOException;

/**
 * @author wulifu
 */
public class BOMUtils {

    public static final String java = "java";

    public static void removeBom(String filepath) {
        try {
            if (!filepath.contains(java)) {
                return;
            }
            File file = new File(filepath);
            if (!file.isFile()) {
                return;
            }
            byte[] fileBytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
            if (fileBytes[0] == -17 && fileBytes[1] == -69 && fileBytes[2] == -65) {
                byte[] nbs = new byte[fileBytes.length - 3];
                System.arraycopy(fileBytes, 3, nbs, 0, nbs.length);
                org.apache.commons.io.FileUtils.writeByteArrayToFile(file, nbs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
