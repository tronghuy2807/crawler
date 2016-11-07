package vn.vccorp.adtech.bigdata.crawlerdata.services;

import org.apache.commons.configuration.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;
import vn.vccorp.adtech.bigdata.crawlerdata.global.GlobalInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by thuyenhx on 27/08/2015.
 */
public class ProxyProcess {

    private  String redisHost;
    private  int redisPort;
    Configuration conf = SystemInfo.getConfiguration();

    public static void main(String[] args) {
        ProxyProcess demo = new ProxyProcess();
//        demo.crawlerListProxy("http://www.proxynova.com/proxy-server-list/country-vn/");
        demo.crawlerListProxy("http://www.proxynova.com/proxy-server-list/country-us/");
    }

    public ProxyProcess() {
        redisHost = conf.getString("redis.crawler.host");
        redisPort = conf.getInt("redis.crawler.port");
    }

    public void crawlerListProxy(String input) {

        List<String> lstProxy = new ArrayList<String>();

        try {
            URL url = new URL(input);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.connect();

            String line = null;
            StringBuffer tmp = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));

            while((line = in.readLine()) != null) {
                tmp.append(line);
            }

            //parse html su dung Jsoup
            Document doc = Jsoup.parse(String.valueOf(tmp));

            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = null;

            Elements eles = doc.select("tr");
            for (Element el : eles) {
                Element esProxy = el.child(0);
                Element esPort = esProxy.nextElementSibling();

                if (esProxy != null && esPort != null) {
                    String ip = esProxy.text();
                    String port = esPort.text();

                    matcher = pattern.matcher(port);
                    if (matcher.matches()) {
                       lstProxy.add(ip + ":" + port);
                    }
                }
            }

            String value = lstProxy.stream().map(i -> i.toString()).collect(Collectors.joining(","));
            System.out.println(value);
            Jedis jedis = new Jedis(redisHost, redisPort);
            jedis.hset(conf.getString("redis.crawler.list.proxy"), conf.getString("redis.crawler.key"),value);
//            jedis.hset(conf.getString("redis.crawler.list.proxy"), conf.getString("redis.crawler.key"), value);
            jedis.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
