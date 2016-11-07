package vn.vccorp.adtech.bigdata.crawlerdata;

import com.google.common.base.Stopwatch;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.NodeInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql.ItemInfoDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.neo4j.Neo4jDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.global.GlobalInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.services.ProxyProcess;
import vn.vccorp.adtech.bigdata.crawlerdata.task.CrawlerKeyWordTask;
import vn.vccorp.adtech.bigdata.crawlerdata.task.CrawlerNeo4jTask;
import vn.vccorp.adtech.bigdata.crawlerdata.utils.IpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by thuyenhx on 27/08/2015.
 */
public class MainCrawler {

    private static final Logger logger = LoggerFactory.getLogger(MainCrawler.class);

    public static void main(String[] args) throws Exception {

        ProxyProcess proxyProcess = new ProxyProcess();
        proxyProcess.crawlerListProxy("http://www.proxynova.com/proxy-server-list/country-vn/");

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        //get list proxy
        Configuration conf = SystemInfo.getConfiguration();
        String redisHost = conf.getString("redis.crawler.host");
        int redisPort = conf.getInt("redis.crawler.port");
        Jedis jedis = new Jedis(redisHost, redisPort);

        //loc ra ca proxy khong bi time out
        String value = jedis.hget(conf.getString("redis.crawler.list.proxy"), conf.getString("redis.crawler.key"));
        String[] lstProxy = value.split(",");
        String input = "http://lazada.vn";
        for (int i = 0; i < lstProxy.length; i++) {
            if (IpUtils.checkProxy(input, lstProxy[i])) {
                GlobalInfo.ipLst.add(lstProxy[i]);
                logger.info("add proxy: " + lstProxy[i]);
            }
        }
        logger.info("time get proxy: " + stopwatch.stop());

        List<NodeInfo> lstNode = new ArrayList<>();

        Neo4jDAO neo4jDAO = new Neo4jDAO();
        ItemInfoDAO itemInfoDAO = new ItemInfoDAO();
        List<String> urlParsed = itemInfoDAO.getAllUrl();

        List<NodeInfo> aliasNodes = neo4jDAO.getAllNode();
        for (NodeInfo line : aliasNodes) {
            List<NodeInfo> lst = neo4jDAO.getUrlByNode(line);
            lstNode.addAll(lst);
        }
        List<NodeInfo> lstQueue = lstNode.stream().filter(x -> !urlParsed.contains(x.getUrl())).collect(Collectors.toList());

        itemInfoDAO.dispose();
        neo4jDAO.dispose();
        GlobalInfo.urlQueue.addAll(lstQueue);



        Thread[] threads = new Thread[10];
        CrawlerNeo4jTask[] crawlerKeyWordTasks = new CrawlerNeo4jTask[10];
        for (int i = 0; i < 10; i++) {
            crawlerKeyWordTasks[i] = new CrawlerNeo4jTask();
            threads[i] = new Thread(crawlerKeyWordTasks[i], "thread " + i);
            threads[i].start();
        }
    }
}
