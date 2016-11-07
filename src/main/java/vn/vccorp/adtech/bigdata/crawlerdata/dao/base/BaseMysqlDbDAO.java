package vn.vccorp.adtech.bigdata.crawlerdata.dao.base;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by thuyenhx on 28/08/2015.
 */
public class BaseMysqlDbDAO {

    protected Connection conn;

    public BaseMysqlDbDAO() throws Exception {
        conn = MySqlManager.createConnection();
        conn.setAutoCommit(false);
    }


    public void dispose() throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                if (!conn.getAutoCommit())
                    conn.commit();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("error when close connection");
        } finally {
        }
    }
}
