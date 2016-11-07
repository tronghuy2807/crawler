package vn.vccorp.adtech.bigdata.crawlerdata.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.ItemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql.ItemInfoDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.global.GlobalInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.services.HtmlProcess;

/**
 * Created by huydt on 20/04/2016.
 */
public class CrawlerMCTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerMCTask.class);
    private HtmlProcess htmlProcess;
    private ItemInfoDAO itemInfoDAO;
    int count = 0;
    public CrawlerMCTask() throws Exception {
        itemInfoDAO = new ItemInfoDAO();
        htmlProcess = new HtmlProcess();
    }

    @Override

    public void run(){
        while (true){
            String url = GlobalInfo.urlMcQueue.poll();
            try {
                if(url==null){
                    itemInfoDAO.updateSubCatMC(GlobalInfo.itemByCat);
                    logger.info("Import sub cat to DB, Finish!!!");
                    itemInfoDAO.dispose();
                    System.exit(1);
                }else{
                    url = url.replace("http", "https");
                    ItemInfo item = htmlProcess.crawlerInfoByDomain(url);
                    String path = item.getUrl().substring(item.getUrl().lastIndexOf('/')+1);
                    if(path!=null) {
                        String itemId = path.substring(path.lastIndexOf('-')+1).replace(".html","");
                        String subCat = null;
                        if (itemId != null) {
                            if(item.getCatName().contains(">")){
                                subCat = item.getCatName().split(">")[1];
//                                if(GlobalInfo.flag==true) {
                                    GlobalInfo.itemByCat.put(Integer.parseInt(itemId), subCat);
//                                }else {
//                                    logger.info("processing update db => go to sleep 5s");
//                                    Thread.sleep(5000);
//                                }
                            }else {
                                count=count+1;
                                logger.info("\nNUM OF NONE SUB CAT--> " + count);
                            }
                        }
                    }
                    logger.info("\nNum url have Sub cat--> "+GlobalInfo.itemByCat.size());
                }
            }catch (Exception e){
                logger.error("Error: " + e);
                System.exit(1);
            }

        }


        }
    }
