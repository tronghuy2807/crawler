package vn.vccorp.adtech.bigdata.crawlerdata.global;

import vn.vccorp.adtech.bigdata.crawlerdata.bo.ItemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.NodeInfo;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by thuyenhx on 26/08/2015.
 */
public class GlobalInfo {

    public static final String CAT="cat";
    public static final String TITLE="title";
    public static final String PRICE="price";
    public static final String DETAILS="details";
    public static final String ID="id";
    public static final String QUERY_DETAILS="queryDetails";

    public static final LinkedBlockingQueue<NodeInfo> urlQueue = new LinkedBlockingQueue<>();
    public static final LinkedBlockingQueue<String> proxyQueue = new LinkedBlockingQueue<>();
    public static List<String> ipLst = new LinkedList<>();

    public static List<ItemInfo> listData = new ArrayList<>();

    public static Map<Integer, String> itemByCat = new HashMap<>();
    public static boolean flag=true;
    public static List<String> lstUrl = new ArrayList<>();
    public static final LinkedBlockingQueue<String> urlMcQueue = new LinkedBlockingQueue<>();

    public static boolean checkBlock = false;
}
