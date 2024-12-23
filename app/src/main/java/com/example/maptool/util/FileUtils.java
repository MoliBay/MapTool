package com.example.maptool.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static void copyDir(Context context, String srcDir, String destDir) {
        File file = new File(srcDir);
        if(!file.exists() || !file.isDirectory()) {
            Log.d("FileUtils","invalid map directory.");
            return;
        }

        File file01 = new File(destDir);
        if(!file.exists()) {
            file01.mkdir();
        }

        file01 = new File(destDir);
        if(!file.exists()) {
            file01.mkdir();
        }
        File[] files01 = file.listFiles();
        for (File item : files01) {
            Log.d("FileUtils","fileName is:"+item.getName());
            String srcFileName = srcDir+File.separator + item.getName();
            String destFileName = destDir+File.separator + item.getName();
            copyFile(srcFileName, destFileName);
        }
    }

    public static void copyFile(String srcFilePath, String destFilePath) {
        Log.d(TAG, "copyFile: " + "srcFilePath: " + srcFilePath
                + " destFilePath: " + destFilePath );
        if (srcFilePath.equals(destFilePath)) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try{
            fis = new FileInputStream(srcFilePath);
            fos = new FileOutputStream(destFilePath);
            byte datas[] = new byte[4*1024];
            int len = 0;
            while((len = fis.read(datas))!=-1){
                fos.write(datas,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            }catch(Exception ex) {
            }
        }
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param filePath
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, "删除文件失败:" + filePath + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(filePath);
            else
                return deleteDirectory(filePath);
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e(TAG, "删除单个文件" + filePath + "成功！");
                return true;
            } else {
                Log.e(TAG, "删除单个文件" + filePath + "失败！");
                return false;
            }
        } else {
            Log.e(TAG, "删除单个文件失败：" + filePath + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Log.e(TAG, "删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            Log.e(TAG, "删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.d(TAG, "删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }


    public static String readJsonFile(String filePath) {

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        BufferedReader reader = null;
        String jsonStr = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                jsonStr = jsonStr + tempString;
                ++line;
            }
            Log.d(TAG, "jsonStr=" + jsonStr);
            reader.close();
        } catch (IOException  e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return jsonStr;
    }

    public static boolean writeFile(String filePath, String content) {
        File file = new File(filePath);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.write(content);
            printWriter.close();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean createDir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            if (file.mkdirs()) {
                Log.d(TAG, "folder created successfully");
                return true;
            }
        }
        return false;
    }

    public static void saveBitmap(String filePath, Bitmap bitmap) {

        File file = new File(filePath);
        if (bitmap ==null)
        {
            return;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String randomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }
        return randomString.toString();
    }
}
