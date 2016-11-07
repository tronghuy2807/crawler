package vn.vccorp.adtech.bigdata.crawlerdata.dao;

import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import redis.clients.jedis.Jedis;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql.ItemInfoDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class ItemInfoDAOTest extends TestCase {

    private  String redisHost;
    private  int redisPort;

    public void testGetTitleId() throws Exception {


        String title = "http://www.lazada.vn/tui-deo-cheo-dung-ipad-jeep-39501-nau-1073257.html";
        CRC32 crc = new CRC32();

        try {
            crc.update(title.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("UTF-8 encoding is not supported");
        }

        System.out.println(crc.getValue());

        String input = "http://10.3.14.127/muachung/item/list?catid=8&fd=2015-08-21&td=2015-08-27";
        System.out.println(input.split("\\?")[0]);
    }
    public void testUpdateSubCat() throws Exception {
        Map<Integer,String> dataSc = new HashMap<>();
        dataSc.put(76575,"Đồ dùng tiện ích");
        ItemInfoDAO itemInfoDAO = new ItemInfoDAO();
        itemInfoDAO.updateSubCatMC(dataSc);
        System.out.println("OK");
    }

    public void testRedis() {

        Configuration conf = SystemInfo.getConfiguration();
        redisHost = conf.getString("redis.crawler.host");
        redisPort = conf.getInt("redis.crawler.port");
        Jedis jedis = new Jedis(redisHost, redisPort);
//        jedis.sadd(conf.getString("redis.crawler.list.url"), "http://www.lazada.vn/tui-deo-cheo-dung-ipad-jeep-39501-nau-1073257.html");
//        System.out.println(jedis.spop(conf.getString("redis.crawler.list.url")));

//        jedis.hset(conf.getString("redis.crawler.list.proxy"), conf.getString("redis.crawler.key"),"118.70.184.182:3128");


        String key = conf.getString("redis.crawler.list.url_passe");
        String id = getId("http://lazada.vn/dong-ho-tranh-hoa-sen-1-dyvina-1t4040-14-220662.html").toString();
        System.out.println(jedis.hexists(key, id));
        jedis.close();
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
}