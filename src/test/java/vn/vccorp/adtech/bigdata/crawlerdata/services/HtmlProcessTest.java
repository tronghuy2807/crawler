package vn.vccorp.adtech.bigdata.crawlerdata.services;

import com.google.common.base.Stopwatch;
import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import redis.clients.jedis.Jedis;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.ItemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql.ItemInfoDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.global.GlobalInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.task.CrawlerKeyWordTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

public class HtmlProcessTest extends TestCase {

    public void testCrawlerInfoByDomain() throws Exception {

//        ProxyProcess proxyProcess = new ProxyProcess();
//        proxyProcess.crawlerListProxy("http://www.proxynova.com/proxy-server-list/country-vn/");

        //get list proxy
        Configuration conf = SystemInfo.getConfiguration();
//        String redisHost = conf.getString("redis.crawler.host");
//        int redisPort = conf.getInt("redis.crawler.port");
//        Jedis jedis = new Jedis(redisHost, redisPort);
//        String value = jedis.hget(conf.getString("redis.crawler.list.proxy"), conf.getString("redis.crawler.key"));
//        String[] lstProxy = value.split(",");
//        GlobalInfo.ipLst = Arrays.asList(lstProxy);

        List<String> lstUrl = new ArrayList<>();
        ItemInfoDAO itemInfoDAO = new ItemInfoDAO();
        lstUrl = itemInfoDAO.getMuachung();
        Map<String,String> itemByCat = new HashMap<>();

//        for(String url: lstUrl) {
//            url = url.replace("http","https");
            HtmlProcess demo = new HtmlProcess();
//        System.out.println(demo.getDomain("http://lazada.vn/may-nghe-nhac-mp4-titishop-cam-251778.html"));
//        System.out.println(demo.getHtml("http://lazada.vn/may-nghe-nhac-mp4-titishop-cam-251778.html"));
            ItemInfo item = demo.crawlerInfoByDomain("https://muachung.vn/thuc-pham/10-chai-dong-trung-ha-thao-75054.html");

        if(item.getCatName().contains(">")) {
            System.out.println(item.getCatName().split(">")[1]);
        }else System.out.println(item.getCatName());
//            itemByCat.put(item.getTitle(),item.getCatName());
//        }
//        System.out.println(item.getTitle());
//        ItemInfoDAO itemInfoDAO = new ItemInfoDAO();
//        itemInfoDAO.insertItemInfo(item);
    }

    public void testProxy() {

        String res = null;

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        try {
            URL url = new URL("http://www.lazada.vn/ca-no-sieu-toc-dieu-khien-tu-xa-xanh-1013034.html");

//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("118.69.226.241", 8888));
//            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
//            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setConnectTimeout(5000);
            uc.connect();

            System.out.println("Started get HTML");

            String line = null;
            StringBuffer tmp = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));

            while((line = in.readLine()) != null) {
                tmp.append(line);
            }
            res = String.valueOf(tmp);

            System.out.println("Finish get HTML");

            System.out.println(res);

        }catch(SocketTimeoutException e) {
            System.out.println("Connection reset!");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Unable to tunnel through proxy. Proxy returns \"HTTP/1.0 404 Not Found\"");
        }

        System.out.println("time parsed: " + stopwatch.stop());
    }

    public void testParseStringHtml() throws Exception {

        HtmlProcess htmlProcess = new HtmlProcess();
        htmlProcess.parseStringHtml("");
//        htmlProcess.parseHTML();
    }

    public void testUncoding() throws Exception{

        CrawlerKeyWordTask.test();
    }
    public void testGetHtml(){
        HtmlProcess html = new HtmlProcess();
        String input = "https://muachung.vn/thuc-pham/10-chai-dong-trung-ha-thao-75054.html";
        System.out.println(html.getHtml(input));
    }
}