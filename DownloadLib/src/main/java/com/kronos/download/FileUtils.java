package com.kronos.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
public class FileUtils {

    /**
     * 从一个输入流里写文件
     *
     * @param destFilePath 要创建的文件的路径
     * @param in           要读取的输入流
     * @return 写入成功返回true, 写入失败返回false
     */
    public static boolean writeFile(String destFilePath, InputStream in) {
        try {
            if (!createFile(destFilePath)) {
                return false;
            }
            FileOutputStream fos = new FileOutputStream(destFilePath);
            int readCount = 0;
            int len = 1024;
            byte[] buffer = new byte[len];
            while ((readCount = in.read(buffer)) != -1) {
                fos.write(buffer, 0, readCount);
            }
            fos.flush();
            if (null != fos) {
                fos.close();
                fos = null;
            }
            if (null != in) {
                in.close();
                in = null;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 创建一个文件，创建成功返回true
     *
     * @param filePath
     * @return
     */
    public static boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}
