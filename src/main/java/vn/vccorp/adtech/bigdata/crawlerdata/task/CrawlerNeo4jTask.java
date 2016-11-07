package vn.vccorp.adtech.bigdata.crawlerdata.task;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.ItemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.NodeInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.cass.ItemInfoCass;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql.ItemInfoDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.global.GlobalInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.services.HtmlProcess;
import vn.vccorp.adtech.bigdata.crawlerdata.utils.FileUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * Created by thuyenhx on 27/01/2016.
 */
public class CrawlerNeo4jTask implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(CrawlerNeo4jTask.class);
    private Configuration conf = SystemInfo.getConfiguration();

    private HtmlProcess htmlProcess;
    private ItemInfoDAO itemInfoDAO;

    public CrawlerNeo4jTask() throws Exception {
        itemInfoDAO = new ItemInfoDAO();
        htmlProcess = new HtmlProcess();
    }
    @Override
    public void run() {

        while (true) {
            String url = null;

            try {

                if (GlobalInfo.checkBlock) {
                    logger.info("processing insert db => go to sleep 10s");
                    Thread.sleep(10000);
                    continue;
                }

                final NodeInfo node = GlobalInfo.urlQueue.poll();
                url = node.getUrl();
                String pn = node.getName();
                String alias = node.getAlias();

                if (node == null) {
                    if (!GlobalInfo.checkBlock && GlobalInfo.listData.size() > 0) {
                        GlobalInfo.checkBlock = true;
                        itemInfoDAO.insertItemInfo(GlobalInfo.listData);
                        GlobalInfo.checkBlock = false;
                    }
                    logger.info("data blank => finish");
                    itemInfoDAO.dispose();
                    System.exit(1);
                }

                ItemInfo itemInfo = htmlProcess.crawlerInfoByDomain(url);
                if (itemInfo != null) {
                    itemInfo.setAlias(alias);
                    itemInfo.setProductName(pn);
                    GlobalInfo.listData.add(itemInfo);
                }else {
                    logger.warn("do not parse url: " + url);
                }

                if (GlobalInfo.listData.size() == 50) {
                    GlobalInfo.checkBlock = true;
                    itemInfoDAO.insertItemInfo(GlobalInfo.listData);
                    GlobalInfo.listData = new ArrayList<>();
                    GlobalInfo.checkBlock = false;
                }

            }catch (Exception e) {
                logger.error("error url: " + url);
            }
        }
    }
}
