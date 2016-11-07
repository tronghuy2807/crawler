package vn.vccorp.adtech.bigdata.crawlerdata;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql.ItemInfoDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.global.GlobalInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.task.CrawlerMCTask;

/**
 * Created by huydt on 20/04/2016.
 */
public class CrawlerMcMain {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerMcMain.class);
    public static void main(String[] args) throws Exception {
        ItemInfoDAO itemInfoDAO = new ItemInfoDAO();
        GlobalInfo.lstUrl = itemInfoDAO.getMuachung();
        logger.info("Num of url from DB: " + GlobalInfo.lstUrl.size());

        for(String url : GlobalInfo.lstUrl){
            try {
                GlobalInfo.urlMcQueue.put(url);
            } catch (InterruptedException e) {
                logger.warn("add to queue failed", e);
            }
        }
        logger.info("queue builder done");

        Thread[] threads = new Thread[5];
        CrawlerMCTask[] crawlerMCTasks = new CrawlerMCTask[5];
        for (int i = 0; i < 5; i++) {
            crawlerMCTasks[i] = new CrawlerMCTask();
            threads[i] = new Thread(crawlerMCTasks[i], "thread " + i);
            threads[i].start();
            logger.info("Done update sub cat");
        }
    }
}
