package com.inspur;

import java.io.*;

public class MountShellUtils {
    private static String LOCAL_SHELL_DIR = "/mountshells";
    private static String MOUNT_SHELL_NAME = "mountshell.sh";
    private static String UMOUNT_SHELL_NAME = "umountshell.sh";
    private static String FILE_SPLIT_MARK="/";

    private static String FULL_MOUNT_SHELL_PATH=LOCAL_SHELL_DIR+FILE_SPLIT_MARK+MOUNT_SHELL_NAME;
    private static String FULL_UMOUNT_SHELL_PATH=LOCAL_SHELL_DIR+FILE_SPLIT_MARK+UMOUNT_SHELL_NAME;

    private static String MOUNT_SHELL_CLASSPATH="shell"+FILE_SPLIT_MARK+MOUNT_SHELL_NAME;
    private static String UMOUNT_SHELL_CLASSPATH="shell"+FILE_SPLIT_MARK+UMOUNT_SHELL_NAME;

    public static void executeMount(String param1,String param2,String param3){
        init();
        String cmd = FULL_MOUNT_SHELL_PATH+" "+param1+" "+param2+" "+param3;
        ProcessBuilder builder = new ProcessBuilder("/bin/sh","-c",cmd);
        executeProcessBuilder(builder);
    }

    public static void executeUmount(String param1,String param2,String param3){
        init();
        String cmd = FULL_UMOUNT_SHELL_PATH+" "+param1+" "+param2+" "+param3;
        ProcessBuilder builder = new ProcessBuilder("/bin/sh","-c",cmd);
        executeProcessBuilder(builder);
    }

    private static void init(){
        File shellDirF = new File(LOCAL_SHELL_DIR);
        if(!shellDirF.exists()){
            shellDirF.mkdir();
        }
        File mf = new File(FULL_MOUNT_SHELL_PATH);
        if(!mf.exists()){
            fileCopy(MOUNT_SHELL_CLASSPATH,FULL_MOUNT_SHELL_PATH);
            ProcessBuilder builder = new ProcessBuilder("chmod","+x",FULL_MOUNT_SHELL_PATH);
            executeProcessBuilder(builder);
        }
        File umf = new File(FULL_UMOUNT_SHELL_PATH);
        if(!umf.exists()){
            fileCopy(UMOUNT_SHELL_CLASSPATH,FULL_UMOUNT_SHELL_PATH);
            ProcessBuilder builder = new ProcessBuilder("chmod","+x",FULL_UMOUNT_SHELL_PATH);
            executeProcessBuilder(builder);
        }
    }

    private synchronized static void executeProcessBuilder(ProcessBuilder builder){
        int runningStatus = 0;
        String s = null;
        StringBuffer sb = new StringBuffer();
        try {
            Process p = builder.start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
                System.out.println("shell System.out.println ...." + s);
                sb.append(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.err.println("shell System.error.println...." + s);
                sb.append(s);
            }
            try {
                runningStatus = p.waitFor();
            } catch (InterruptedException e) {
                runningStatus = 1;
                System.err.println("等待shell脚本执行状态时，报错...");
                e.printStackTrace();
                sb.append(e.getMessage());
            }

            closeStream(stdInput);
            closeStream(stdError);

        } catch (Exception e) {
            System.err.println("执行shell脚本出错...");
            e.printStackTrace();
            sb.append(e.getMessage());
            runningStatus =1;
        }
        System.out.println("runningStatus = " + runningStatus);
        if(runningStatus == 0){
            System.out.println("成功");
        }else{
            System.out.println("失败");
        }
    }

    private synchronized static int fileCopy(String srcFilePathInClassPath, String destFilePath){
        int flag = 0;
        File destFile = new File(destFilePath);
        try {
            InputStream fis = MountShellUtils.class.getClassLoader().getResourceAsStream(srcFilePathInClassPath);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int c = 0;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }
            fis.close();
            fos.close();
            flag = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    private static void closeStream(BufferedReader reader){
        try {
            if(reader != null){
                reader.close();
            }
        } catch (Exception e) {
            reader = null;
        }
    }
}
