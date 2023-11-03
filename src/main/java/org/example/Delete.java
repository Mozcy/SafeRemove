package org.example;

import java.io.*;
import java.util.UUID;

public class Delete {


    public static boolean delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File it : files) {
                deleteFile(it);
            }
            file.delete();
        } else {
            deleteFile(file);
        }
        return true;
    }

    /**
     * 用于把文件内容清空(复写)并重命名
     * @param file
     */
    private static void deleteFile(File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write("已被安全删除".toString().getBytes());
            bos.flush();
            bos.close();
            String uuid = UUID.randomUUID().toString();
            File rename = new File(file.getParent() + uuid);
            file.renameTo(rename);
            rename.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
