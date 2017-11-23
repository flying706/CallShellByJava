import com.inspur.MountShellUtils;

public class Main {

    public static void main1(String[] args) {
        //本地目录
        String param1 = "/data/data8";
        //远程目录
        String param2 = "192.168.1.173:/testspeed";
        //scp源
        String param3 = "192.168.1.173:/data/data8/.";
        MountShellUtils.executeMount(param1,param2,param3);
    }

    public static void main(String[] args) {
        //本地目录
        String param1 = "/data/data8";
        //远程目录
        String param2 = "/dev/sde1";
        //scp源
        String param3 = "192.168.1.173:/data/data8/.";
        MountShellUtils.executeUmount(param1,param2,param3);
    }
}
