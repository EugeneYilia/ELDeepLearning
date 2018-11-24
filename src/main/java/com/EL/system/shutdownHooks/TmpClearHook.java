package com.EL.system.shutdownHooks;

import com.EL.utils.Utils;

import java.io.File;

public class TmpClearHook {
    public static void registerHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                File tmpDir = new File("/home/eugeneliu/IdeaProjects/ELNeuralNetwork/tmp/");
                boolean isDeleteSuccessful = Utils.deleteFiles(tmpDir);
                boolean isCreateSuccessful = Utils.createDir(tmpDir);
                if (isCreateSuccessful && isDeleteSuccessful) {
                    System.out.println("临时文件夹清理任务结束");
                }
            }
        });
    }
}
