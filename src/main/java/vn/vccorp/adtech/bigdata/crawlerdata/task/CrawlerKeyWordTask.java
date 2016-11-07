package vn.vccorp.adtech.bigdata.crawlerdata.task;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.ItemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql.ItemInfoDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.services.HtmlProcess;
import vn.vccorp.adtech.bigdata.crawlerdata.utils.FileUtils;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

/**
 * Created by thuyenhx on 23/10/2015.
 */
public class CrawlerKeyWordTask implements Runnable{

    private HtmlProcess htmlProcess;
    private ItemInfoDAO itemInfoDAO;

    private String redisHost;
    private int redisPort;
    private String redisAuth;
    private String keyUrl;

    private PrintWriter writer;

    private Configuration conf = SystemInfo.getConfiguration();
    private static final Logger logger = LoggerFactory.getLogger(CrawlerKeyWordTask.class);


    public CrawlerKeyWordTask() throws  Exception {

        htmlProcess = new HtmlProcess();
        itemInfoDAO = new ItemInfoDAO();

        redisHost = conf.getString("redis.url.host");
        redisPort = conf.getInt("redis.url.port");
        keyUrl = conf.getString("redis.url.list.url");

        writer = FileUtils.getInstanceWriter();
    }

    @Override
    public void run() {

        while (true) {
            Jedis jedis = null;
            String url = null;
            try {

                jedis = new Jedis(redisHost, redisPort);
                url = jedis.spop(keyUrl);

                if (url == null) {
                    logger.info("data blank => finish");
                    itemInfoDAO.dispose();
                    FileUtils.closeWriter();
                    System.exit(1);
                }

                System.out.println(url);

                String parseUrl = url.split("\t")[1];
                String keyWord = url.split("\t")[0];


                ItemInfo itemInfo = htmlProcess.crawlerInfoByDomain(parseUrl);

                if (itemInfo != null) {
                    itemInfo.setKeyWord(keyWord);
//                    itemInfoDAO.insertInfoKeyWord(itemInfo);

                    //writer data csv
                    long id = itemInfo.getId();
                    String urlInfo = itemInfo.getUrl();
                    String title = itemInfo.getTitle();
                    String date = itemInfo.getDate();

                    String line = id + "@@" + urlInfo + "@@" + title + "@@" + keyWord + date;

                    writer.write(line + "\n");
                    writer.flush();

                    logger.info("url: " + url + " finish parsed");
                    jedis.close();
                }else {
                    logger.info("url: " + url + " not data!");
                }

            } catch (Exception e) {
                logger.error("error url: " + url, e);
                e.printStackTrace();
            }

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

    public void cacheUrlNull(String url) {

        Jedis jedis = new Jedis(redisHost, redisPort);
        String key = conf.getString("redis.url.list.null");
        String id = getId(url).toString();

        jedis.hset(key, id, id);

        jedis.close();
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

    public static void test() {
        System.out.println("Miếng dán cường lực ip6 plus\thttp://lazada.vn/mieng-dan-cuong-luc-cho-iphone-6-plus-apple-scren-protected-324526.html");
    }
}
