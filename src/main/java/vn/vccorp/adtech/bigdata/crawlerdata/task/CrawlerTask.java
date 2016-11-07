package vn.vccorp.adtech.bigdata.crawlerdata.task;

import org.apache.commons.configuration.Configuration;
import redis.clients.jedis.Jedis;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.ItemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql.ItemInfoDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.cass.ItemInfoCass;
import vn.vccorp.adtech.bigdata.crawlerdata.global.GlobalInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.services.HtmlProcess;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

/**
 * Created by thuyenhx on 27/08/2015.
 */
public class CrawlerTask implements Runnable{

    private HtmlProcess htmlProcess;
    private ItemInfoDAO itemInfoDAO;
    private String redisHost;
    private int redisPort;
    private Configuration conf = SystemInfo.getConfiguration();
    private ItemInfoCass itemInfoCass;

    public CrawlerTask() throws  Exception{

        htmlProcess = new HtmlProcess();
        itemInfoDAO = new ItemInfoDAO();
        itemInfoCass = new ItemInfoCass();

        redisHost = conf.getString("redis.crawler.host");
        redisPort = conf.getInt("redis.crawler.port");
    }

    @Override
    public void run() {


        while (true) {

//            final String url = GlobalInfo.urlQueue.poll();
//
//            try {
//
//                String domain = getDomain(url);
//                Long id = getId(url);
//
//                //kt neu url da xu ly roi thi bo qua (get tu cass)
//                if (!itemInfoCass.isUrlParsed(domain, id) && !itemInfoCass.isUrlNull(domain, id)) {
//
//                    ItemInfo itemInfo = htmlProcess.crawlerInfoByDomain(url);
//                    if (itemInfo != null) {
//
//                        itemInfoDAO.insertItemInfo(itemInfo);
//
//                        //cahe Url parsed
//                        itemInfoCass.insertUrlParsed(domain, id);
//                    }else {
//
//                        //cache Url Null
//                        itemInfoCass.insertUrlNull(domain, id);
//                    }
//
//                    System.out.println(Thread.currentThread().getName() + ": " + itemInfo);
//                    System.out.println(Thread.currentThread().getName() + ": url " + url + " done!");
//                }else {
//                    System.out.println("url: " + url + " parsed!");
//                }
//
//            }catch (Exception e) {
//                System.out.println("error url: " + url);
//                e.printStackTrace();
//            }
        }
    }

    public boolean checkUrlNull(String url) {

        Jedis jedis = new Jedis(redisHost, redisPort);

        String key = conf.getString("redis.url.list.null");
        String id = getId(url).toString();
        boolean passe = jedis.hexists(key, id);
        jedis.close();

        return passe;
    }

    public boolean checkUrlParse(String url) {

        Jedis jedis = new Jedis(redisHost, redisPort);

//        String key = conf.getString("redis.crawler.list.url_parsed");
        String key = conf.getString("redis.url.list.url_parsed");
        String id = getId(url).toString();
        boolean passe = jedis.hexists(key, id);
        jedis.close();

        return passe;
    }

    public Long getId(String url) {

        String input = url.split("\\?")[0];

        CRC32 crc = new CRC32();
        try {
            crc.update(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("UTF-8 encoding is not supported");
        }

        return crc.getValue();
    }

    protected String getDomain(String url) {

        String domain;
        String fdomain;

        if (url.contains("http") || url.contains("file")) {
            domain = url.split("/")[2];
        }else {
            domain = url.split("/")[0];
        }

        if (domain.contains("www.") || domain.contains("m.")) {
            fdomain = domain.split("\\.")[1] + "." + domain.split("\\.")[2];
        }else {
            fdomain = domain.split("\\.")[0] + "." + domain.split("\\.")[1];
        }

        return fdomain;
    }

    public void cacheUrl(String url) {

        Jedis jedis = new Jedis(redisHost, redisPort);
//        String key = conf.getString("redis.crawler.list.url_passe");
        String key = conf.getString("redis.url.list.url_parsed");
        String id = getId(url).toString();

        jedis.hset(key, id, id);

        jedis.close();
    }

    public String getUrl() {

        Jedis jedis = new Jedis(redisHost, redisPort);
//        String url = jedis.spop(conf.getString("redis.crawler.list.url"));
        String url = jedis.spop(conf.getString("redis.url.list.url"));
        jedis.close();

        return url;
    }
}
