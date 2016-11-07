package vn.vccorp.adtech.bigdata.crawlerdata.dao.base;

import org.apache.commons.configuration.Configuration;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by thuyenhx on 28/08/2015.
 */
public class MySqlManager {
    private static volatile String url;
    private static volatile String user;
    private static volatile String password;

    static {
        init();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            System.out.println("can't load mysql driver");
            e.printStackTrace();
        }
    }

    public static synchronized void init() {
        final Configuration configuration = SystemInfo.getConfiguration();
        url = configuration.getString("mysql.url");
        user = configuration.getString("mysql.user");
        password = configuration.getString("mysql.pass");

//        url = configuration.getString("crawmysql.url");
//        user = configuration.getString("crawl.mysql.user");
//        password = configuration.getString("crawl.mysql.pass");
    }

    public synchronized static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
