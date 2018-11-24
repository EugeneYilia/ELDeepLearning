package com.EL.utils;

import java.io.File;

public class Utils {
    public static boolean deleteFiles(File file){
        if(!file.exists()){
            return false;
        }
        if(file.isFile()){
            return file.delete();
        } else {
            for(File subFile:file.listFiles()){
                deleteFiles(subFile);
            }
            return file.delete();
        }
    }

    public static boolean createDir(File file){
        return file.mkdir();
    }
}
