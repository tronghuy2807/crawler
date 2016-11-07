package vn.vccorp.adtech.bigdata.crawlerdata.services;

import com.google.common.base.Stopwatch;
import org.apache.commons.configuration.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.ItemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql.ItemInfoDAO;
import vn.vccorp.adtech.bigdata.crawlerdata.global.GlobalInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.utils.IpUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

/**
 * Created by thuyenhx on 24/08/2015.
 */
public class HtmlProcess {

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();
    private Stopwatch sw = new Stopwatch();

    private static final Logger logger = LoggerFactory.getLogger(HtmlProcess.class);

    public static void main(String[] args) {
        HtmlProcess demo = new HtmlProcess();
//        demo.crawlerInfoByDomain("http://muachung.vn/am-thuc-nha-hang/buffet-nuong-lau-chipa-chipa-bbq-88643.html");

        ProxyProcess proxyProcess = new ProxyProcess();
        proxyProcess.crawlerListProxy("http://www.proxynova.com/proxy-server-list/country-vn/");

        //get list proxy
        Configuration conf = SystemInfo.getConfiguration();
        String redisHost = conf.getString("redis.crawler.host");
        int redisPort = conf.getInt("redis.crawler.port");
        Jedis jedis = new Jedis(redisHost, redisPort);

        //loc ra ca proxy khong bi time out
        String value = jedis.hget(conf.getString("redis.crawler.list.proxy"), conf.getString("redis.crawler.key"));
        jedis.close();
        String[] lstProxy = value.split(",");
        String input = "http://lazada.vn";
        for (int i = 0; i < lstProxy.length; i++) {
            if (IpUtils.checkProxy(input, lstProxy[i])) {
                GlobalInfo.ipLst.add(lstProxy[i]);
                logger.info("add proxy: " + lstProxy[i]);
            }
        }
//        String url = "http://shopnhat.vn/am-dun-nuoc-co-coi-bao-alaw_v1m91i2628.html";
//        ItemInfo item = demo.crawlerInfoByDomain(url.toLowerCase());
//        System.out.println(item.getTitle());

//        System.out.println(demo.getDomain("http://www.yes24.vn/Product/1559510/khan-tay-soi-bamboo-belleto-28x42%28cm%29hqk9"));
        System.out.println(demo.getHtml("Http://www.lazada.vn/may-massage-cam-tay-2-den-hong-ngoai-beurer-mg80-trang-phoi-xam-297532.html "));


    }

    public HtmlProcess() {

    }

    protected int getAddressProxy() {
        int index = 0;
        Random random = new Random();

//        logger.info("size proxy: " + GlobalInfo.ipLst.size());
        if (GlobalInfo.ipLst.size() > 0) {
            index = random.nextInt(GlobalInfo.ipLst.size());
        }

        return index;
    }

    protected String getHtml(String input) {
//        sw.start();
        String res = null;
//        int index = getAddressProxy();
//        String addressProxy = GlobalInfo.ipLst.get(index);

        try {
            URL url = new URL(input);

//            String ip = addressProxy.split(":")[0];
//            int port = Integer.parseInt(addressProxy.split(":")[1]);

//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
//            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setConnectTimeout(3000);
            uc.connect();

            String line = null;
            StringBuffer tmp = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));

            while((line = in.readLine()) != null) {
                tmp.append(line);
            }
            res = String.valueOf(tmp);
            in.close();
//            System.out.println(sw.stop());
        }catch(Exception e) {
//            logger.warn("timeout ip: " + addressProxy);
            return null;
        }

        return res;
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

    public ItemInfo crawlerInfoByDomain(String url) {
        String html = null;
        int loop = 0;

        while (loop <= 5) {
            html = getHtml(url);
            loop++;
        }
        if (html == null) return null;

        logger.info("start parse html "+url);
        Document doc = Jsoup.parse(html);

        String domain = getDomain(url);
        HashMap<String, String> parseLst = FactoryDomain.getInfoParse(domain);
        String parseTitle = parseLst.get(GlobalInfo.TITLE);
        String parseCat = parseLst.get(GlobalInfo.CAT);
        String parsePrice = parseLst.get(GlobalInfo.PRICE);
        String parseDetails = parseLst.get(GlobalInfo.DETAILS);

        ItemInfo itemInfo = null;
        Elements eles = null;

        String temp = doc.html().replace("<br>", " - ");
        doc = Jsoup.parse(temp);

        //lay thong tin ve category
        eles = doc.select(parseCat);
        String catName = "";
        for (Element el : eles) {
            if (!catName.isEmpty()) {
                catName += " > " + el.text();
            }else {
                catName += el.text();
            }
        }

        //lay thong tin chi tiet ve san pham
        String details = "";
        String price = null;
        String title = null;


        eles = doc.select(parseTitle);
        title = eles.text();

        eles = doc.select(parsePrice);
        price = eles.text();

        if (!url.contains("yes24.vn")) {
            eles = doc.select(parseDetails);

            for (Element el : eles) {
                if (!details.isEmpty()) {
                    details += " - " + el.text().trim();
                } else {
                    details += el.text().trim();
                }
            }
        }else {
            String parseId = parseLst.get(GlobalInfo.ID);
            String parseQuery = parseLst.get(GlobalInfo.QUERY_DETAILS);

            eles = doc.select(parseId);
            String itemId = eles.text();
            String queryDetails = parseQuery + itemId;

            String inHtml = null;
            while(inHtml == null) {
                inHtml = getHtml(queryDetails);
            }

            Document d = Jsoup.parse(inHtml);
            eles = d.select(parseDetails);
            details = eles.text();
        }

        if (title.isEmpty()) {
            return null;
        }

        Long id = getId(url);
        String dt = df.format(calendar.getTime());
        itemInfo = new ItemInfo(title, price, details, url, catName);
        itemInfo.setDate(dt);
        itemInfo.setId(id);
//        System.out.println(sw.stop());
        logger.info(" Finish parse html!");
        return itemInfo;
    }

    public Long getId(String url) {

        String input = url.split("\\?")[0];

        CRC32 crc = new CRC32();
        try {
            crc.update(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.warn("UTF-8 encoding is not supported");
        }

        return crc.getValue();
    }

    protected void parseStringHtml(String html) throws Exception {

        ItemInfoDAO itemInfoDAO = new ItemInfoDAO();
        List<ItemInfo> lstItem = itemInfoDAO.getInfoItem();

        for(ItemInfo item : lstItem) {

            if (item.getDetails() == null) {
                continue;
            }

//            System.out.println(item.getDetails());

            Document doc = Jsoup.parse(item.getDetails());
            ItemInfo itemInfo = null;
            Elements eles = null;

            String details = "";

            eles = doc.select("ul>li, p");

            if (!eles.isEmpty()) {
                for (Element el : eles) {
                    if (!details.isEmpty()) {
                        details += "\n" + el.text().trim();
                    } else {
                        details += el.text().trim();
                    }
                }

                item.setDetails(details);
            }
        }

        itemInfoDAO.insertInfoItem(lstItem);

        System.out.println("Finish parse html!");
    }

    public void parseHTML() {
        String html = "<p><span style=\"color: rgb(17, 17, 17); font-family: Arial, sans-serif; font-size: 14.0086154937744px; line-height: 25.2155075073242px; background-color: rgb(255, 255, 255);\">Những cơn mưa bất chợt&nbsp;lu&ocirc;n khiến ch&uacute;ng ta lu&ocirc;n cảm thấy kh&oacute; chịu,&nbsp;ảnh hưởng đến c&ocirc;ng việc của bạn. Do đ&oacute;, chuẩn bị sẵn 1 bộ &aacute;o mưa trong cốp xe để c&oacute; thể an t&acirc;m chạy xe ngo&agrave;i đường l&agrave; một điều rất cần thiết. H&atilde;y biến những cơn mưa th&agrave;nh niềm vui ri&ecirc;ng của bạn với sản phẩm &aacute;o mưa 2&nbsp;</span><span style=\"color: rgb(17, 17, 17); font-family: Arial, sans-serif; font-size: 14.0086154937744px; line-height: 25.2155075073242px; background-color: rgb(255, 255, 255);\">gi&uacute;p bạn bảo vệ sức khỏe khi m&ugrave;a mưa đến.</span></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><img alt=\"Áo mưa 2 đầu chống thấm nước - 3\" src=\"http://muachung10.vcmedia.vn/thumb_w/640,90/i:gallery/2015/06/24/i73g8/Ao-mua-2-dau-chong-tham-nuoc.jpg\" /></p>\n" +
                "\n" +
                "<p><span style=\"color: rgb(17, 17, 17); font-family: arial, helvetica, sans-serif; font-size: 14px; line-height: 25.2155075073242px; text-align: justify; background-color: rgb(255, 255, 255);\">Với sản phẩm &aacute;o mưa kiểu d&aacute;ng&nbsp;chất liệu nhẹ nh&agrave;ng, mềm mại v&agrave; l&aacute;ng nước mang đến cho người mặc sự dễ chịu, thoải m&aacute;i như mặc quần &aacute;o th&ocirc;ng thường. Bạn sẽ v&ocirc; c&ugrave;ng y&ecirc;n t&acirc;m khi sử dụng &aacute;o mưa 1 đầu c&oacute; k&iacute;nh chắn ph&iacute;a trước gi&uacute;p bạn điều khiển xe một c&aacute;ch an to&agrave;n khi trời mưa.</span></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><img alt=\"Áo mưa 2 đầu chống thấm nước - 11\" src=\"http://muachung10.vcmedia.vn/thumb_w/640,90/i:gallery/2015/06/24/gok67/Ao-mua-2-dau-chong-tham-nuoc.jpg\" /></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><em>Ảnh minh họa</em></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><img alt=\"Áo mưa 2 đầu chống thấm nước - 12\" src=\"http://muachung10.vcmedia.vn/thumb_w/640,90/i:gallery/2015/06/24/nfebu/Ao-mua-2-dau-chong-tham-nuoc.jpg\" /></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><em>&Aacute;o mưa thiết kế d&aacute;nh cho 2 người thật tiện dung khi điều khiển phương tiện giao th&ocirc;ng dưới trời mưa (ảnh minh họa)</em></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><img alt=\"Áo mưa 2 đầu tiện dụng - 1\" src=\"http://muachung10.vcmedia.vn/thumb_w/640,90/i:gallery/2015/06/25/0gpk2/Ao-mua-2-dau-tien-dung.jpg\" /></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><em>Thiết kế tay c&aacute;nh dơi cho bạn dễ d&agrave;ng hoạt động</em></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><img alt=\"Áo mưa 2 đầu tiện dụng - 2\" src=\"http://muachung10.vcmedia.vn/thumb_w/640,90/i:gallery/2015/06/25/w6h60/Ao-mua-2-dau-tien-dung.jpg\" /></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><img alt=\"Áo mưa 2 đầu chống thấm nước - 4\" src=\"http://muachung10.vcmedia.vn/thumb_w/640,90/i:gallery/2015/06/24/ooyzj/Ao-mua-2-dau-chong-tham-nuoc.jpg\" /></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><em>Khi kh&ocirc;ng sử dụng bạn c&oacute; thể gập rất gọn g&agrave;ng để trong cốp xe</em></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><img alt=\"Áo mưa 2 đầu chống thấm nước - 9\" src=\"http://muachung10.vcmedia.vn/thumb_w/640,90/i:gallery/2015/06/24/ulraz/Ao-mua-2-dau-chong-tham-nuoc.jpg\" /></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><em>Chất liệu vải nhựa c&oacute; khả năng chống thấm nước cao</em></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><img alt=\"Áo mưa 2 đầu chống thấm nước - 10\" src=\"http://muachung10.vcmedia.vn/thumb_w/640,90/i:gallery/2015/06/24/arefg/Ao-mua-2-dau-chong-tham-nuoc.jpg\" /></p>\n" +
                "\n" +
                "<p style=\"text-align: center;\"><em>Đường may chắc chắn gi&uacute;p sản phẩm c&oacute; độ bền cao hơn</em></p>\n" +
                "\n" +
                "<p><span style=\"color: rgb(17, 17, 17); font-family: Arial, sans-serif; font-size: 14.0086154937744px; line-height: 25.2155075073242px; text-align: justify; background-color: rgb(255, 255, 255);\">Việc mang b&ecirc;n m&igrave;nh một chiếc &aacute;o mưa để &ldquo;ph&ograve;ng th&acirc;n&rdquo; l&agrave; kh&ocirc;ng thể thiếu để bạn kh&ocirc;ng bị ướt nhẹp bởi những cơn mưa bất chợt.Tuy nhi&ecirc;n, lựa chọn cho m&igrave;nh &aacute;o mưa như thế n&agrave;o để c&oacute; thể bảo vệ bạn một c&aacute;ch tốt nhất cũng kh&ocirc;ng dễ ch&uacute;t n&agrave;o. H&atilde;y nhanh tay sở hữu sản phẩm n&agrave;y của Muachung ngay n&agrave;o c&aacute;c bạn.</span></p>";

        Document doc = Jsoup.parse(html);
        ItemInfo itemInfo = null;
        Elements eles = null;

        String details = "";

        eles = doc.select("ul>li, p");

        if (!eles.isEmpty()) {
            for (Element el : eles) {
                if (!details.isEmpty()) {
                    details += "\n" + el.text().trim();
                } else {
                    details += el.text().trim();
                }
            }

            System.out.println(details);
        }
    }
}
