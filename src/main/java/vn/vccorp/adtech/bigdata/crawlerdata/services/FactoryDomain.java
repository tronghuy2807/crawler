package vn.vccorp.adtech.bigdata.crawlerdata.services;

import org.apache.commons.configuration.Configuration;
import vn.vccorp.adtech.bigdata.crawlerdata.global.GlobalInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;

import java.util.HashMap;

/**
 * Created by thuyenhx on 26/08/2015.
 */
public class FactoryDomain {

    private static final Configuration conf = SystemInfo.getConfiguration();

    public static HashMap<String, String> getInfoParse(String domain) {
        HashMap<String, String> res = new HashMap<>();
        if (domain.equals("lazada.vn")) {
            res.put(GlobalInfo.CAT, conf.getString("lazada.select.cat"));
            res.put(GlobalInfo.TITLE, conf.getString("lazada.select.title"));
            res.put(GlobalInfo.PRICE, conf.getString("lazada.select.price"));
            res.put(GlobalInfo.DETAILS, conf.getString("lazada.select.details"));
        }else if (domain.equals("sendo.vn")) {
            res.put(GlobalInfo.CAT, conf.getString("sendo.select.cat"));
            res.put(GlobalInfo.TITLE, conf.getString("sendo.select.title"));
            res.put(GlobalInfo.PRICE, conf.getString("sendo.select.price"));
            res.put(GlobalInfo.DETAILS, conf.getString("sendo.select.details"));
        }else if (domain.equals("muachung.vn") || domain.equals("beta.muachung")) {
            res.put(GlobalInfo.CAT, conf.getString("muachung.select.cat"));
            res.put(GlobalInfo.TITLE, conf.getString("muachung.select.title"));
            res.put(GlobalInfo.PRICE, conf.getString("muachung.select.price"));
            res.put(GlobalInfo.DETAILS, conf.getString("muachung.select.details"));
        }else if (domain.equals("plaza.muachung") || domain.equals("beta.plaza")) {
            res.put(GlobalInfo.CAT, conf.getString("plaza.muachung.select.cat"));
            res.put(GlobalInfo.TITLE, conf.getString("plaza.muachung.select.title"));
            res.put(GlobalInfo.PRICE, conf.getString("plaza.muachung.select.price"));
            res.put(GlobalInfo.DETAILS, conf.getString("plaza.muachung.select.details"));
        }else if (domain.equals("yes24.vn")) {
            res.put(GlobalInfo.CAT, conf.getString("yes24.select.cat"));
            res.put(GlobalInfo.TITLE, conf.getString("yes24.select.title"));
            res.put(GlobalInfo.PRICE, conf.getString("yes24.select.price"));
            res.put(GlobalInfo.DETAILS, conf.getString("yes24.select.details"));
            res.put(GlobalInfo.QUERY_DETAILS, conf.getString("yes24.select.query.details"));
            res.put(GlobalInfo.ID, conf.getString("yes24.select.id"));
        }else if (domain.equals("shopnhat.vn")) {
            res.put(GlobalInfo.CAT, conf.getString("shopnhat.select.cat"));
            res.put(GlobalInfo.TITLE, conf.getString("shopnhat.select.title"));
            res.put(GlobalInfo.PRICE, conf.getString("shopnhat.select.price"));
            res.put(GlobalInfo.DETAILS, conf.getString("shopnhat.select.details"));
        }

        return res;
    }
}
