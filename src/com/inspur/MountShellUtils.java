package com.inspur;

import java.io.*;
import java.util.concurrent.*;

public class MountShellUtils {
    private static String LOCAL_SHELL_DIR = "/mountshells";
    private static String MOUNT_SHELL_NAME = "mountshell.sh";
    private static String UMOUNT_SHELL_NAME = "umountshell.sh";
    private static String FILE_SPLIT_MARK="/";

    private static String FULL_MOUNT_SHELL_PATH=LOCAL_SHELL_DIR+FILE_SPLIT_MARK+MOUNT_SHELL_NAME;
    private static String FULL_UMOUNT_SHELL_PATH=LOCAL_SHELL_DIR+FILE_SPLIT_MARK+UMOUNT_SHELL_NAME;

    private static String MOUNT_SHELL_CLASSPATH="shell"+FILE_SPLIT_MARK+MOUNT_SHELL_NAME;
    private static String UMOUNT_SHELL_CLASSPATH="shell"+FILE_SPLIT_MARK+UMOUNT_SHELL_NAME;

    //private static ThreadLocal<Process> lp = new ThreadLocal<Process>();

    private static Process lp = null;

    public static String executeMountWithMaxtime(final String param1,final String param2,long maxTime){
        return executeMountWithMaxtime(param1,param2,"",maxTime);
    }

    public static String executeUmountWithMaxtime(final String param1,final String param2,long maxTime){
        return executeUmountWithMaxtime(param1,param2,"",maxTime);
    }

    public static String executeMountWithMaxtime(final String param1,final String param2,final String param3,long maxTime){
        return executeWithMaxtime(param1,param2,param3,maxTime,"mount");
    }

    public static String executeUmountWithMaxtime(final String param1,final String param2,final String param3,long maxTime){
        return executeWithMaxtime(param1,param2,param3,maxTime,"umount");
    }

    public static String executeWithMaxtime(final String param1,final String param2,final String param3,long maxTime,final String type){
        String msg = "failed";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<String> future =
                new FutureTask<String>(new Callable<String>() {//使用Callable接口作为构造参数
                    public String call() {
                        if("mount".equals(type)){
                            return MountShellUtils.executeMount(param1,param2,param3);
                        }else{
                            return MountShellUtils.executeUmount(param1,param2,param3);
                        }
                    }});
        executor.execute(future);
        try {
            if(maxTime<=0l){
                msg = future.get(); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
            }else{
                msg = future.get(maxTime, TimeUnit.MILLISECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
            }
        } catch (Exception e) {
            e.printStackTrace();
            free();
            future.cancel(true);
        } finally {
            executor.shutdown();
        }
        return msg;
    }

    public static String executeMount(String param1,String param2,String param3){
        return execute(param1,param2,param3,"mount");
    }

    public static String executeUmount(String param1,String param2,String param3){
        return execute(param1,param2,param3,"umount");
    }

    public static String execute(String param1,String param2,String param3,String type){
        init();
        String cmd = FULL_MOUNT_SHELL_PATH+" "+param1+" "+param2;//+" "+param3;
        if("umount".equals(type)){
            cmd = FULL_UMOUNT_SHELL_PATH+" "+param1+" "+param2;//+" "+param3;
        }
        ProcessBuilder builder = new ProcessBuilder("/bin/sh","-c",cmd);
        System.out.println("脚本执行……"+cmd);
        int ret = executeProcessBuilder(builder);
        if(ret==0){
            return "success";
        }else{
            return "failed";
        }

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

    private synchronized static int executeProcessBuilder(ProcessBuilder builder){
        int runningStatus = 0;
        String s = null;
        Process p = null;
        BufferedReader stdInput = null;

        try {
            //存在阻塞问题，不再打印builder.redirectErrorStream(true);
            p = builder.start();
            //lp.set(p);

            lp=p;

            /*存在阻塞问题，不再打印stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = stdInput.readLine()) != null) {
                System.out.println("shell System.out.println ...." + s);
            }*/
            runningStatus = p.waitFor();

        } catch (Exception e) {
            System.err.println("执行shell脚本出错...");
            e.printStackTrace();
            runningStatus =1;
        }finally {
            closeStream(stdInput);
            if(p!=null){
                p.destroy();
            }
        }
        System.out.println("runningStatus = " + runningStatus);
        if(runningStatus == 0){
            System.out.println("成功");
        }else{
            System.out.println("失败");
        }
        return runningStatus;
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
            e.printStackTrace();
        }
    }

    private static void free(){
        System.out.println("free method execute");
        /*if(lp.get()!=null){
            System.out.println("process destroyed in free");
            lp.get().destroy();
        }*/
        if(lp!=null){
            System.out.println("process destroyed in free");
            lp.destroy();
        }
    }
}
