package vn.vccorp.adtech.bigdata.crawlerdata.dao.base;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by thuyenhx on 27/01/2016.
 */
public class Neo4jManager {
    private final static Logger logger = LoggerFactory.getLogger(Neo4jManager.class);
    private static volatile String url;
    private static volatile String user;
    private static volatile String pass;

    static{
        init();
        try{
            Class.forName("org.neo4j.jdbc.Driver").newInstance();
        }catch (Exception e) {
            logger.error("can't load mysql driver", e);
        }
    }

    public static synchronized void init() {
        Configuration conf = SystemInfo.getConfiguration();
        url = conf.getString("neo4j.url");
        user = conf.getString("neo4j.user");
        pass = conf.getString("neo4j.pass");
    }

    public synchronized static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url,user,pass);
    }
}
