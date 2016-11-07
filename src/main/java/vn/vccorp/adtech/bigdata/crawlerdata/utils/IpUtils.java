package vn.vccorp.adtech.bigdata.crawlerdata.utils;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by thuyenhx on 26/10/2015.
 */
public class IpUtils {

    public static boolean checkProxy(String input, String addressProxy) {
        try {
            URL url = new URL(input);

            String ip = addressProxy.split(":")[0];
            int port = Integer.parseInt(addressProxy.split(":")[1]);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
            uc.setConnectTimeout(3000);
            uc.connect();

        }catch (Exception e) {
            return false;
        }

        return true;
    }
}
