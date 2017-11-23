import com.inspur.MountShellUtils;

import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        //本地目录
        String param1 = "/data/data8";
        //远程目录
        String param2 = "192.168.1.174:/testspeed";
        String msg = MountShellUtils.executeMountWithMaxtime(param1,param2,5000l);
        System.out.println("finally:"+msg);
    }

    public static void main1(String[] args) {
        //本地目录
        String param1 = "/data/data8";
        //远程目录
        String param2 = "/dev/sde1";
        //scp源
        //String param3 = "192.168.1.173:/data/data8/.";
        String msg = MountShellUtils.executeUmountWithMaxtime(param1,param2,5000l);
        System.out.println("finally:"+msg);
    }
}
