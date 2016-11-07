package vn.vccorp.adtech.bigdata.crawlerdata.dao.cass;

import com.datastax.driver.core.*;
import org.apache.commons.configuration.Configuration;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;

/**
 * Created by thuyenhx on 09/09/2015.
 */
public class ItemInfoCass {

    private Session session;
    private String contentTable;
    private String urlTable;
    private String urlNull;
    private Configuration conf = SystemInfo.getConfiguration();

    public ItemInfoCass() {

        session = CassandraCluster.getInstance().getSession();
        contentTable = conf.getString("cass.table.content");
        urlTable = conf.getString("cass.table.url.parsed");
        urlNull = conf.getString("cass.table.url.null");
    }

    public void createSchema() {

        String query = "CREATE KEYSPACE IF NOT EXISTS recommendation WITH replication " +
                "= { 'class': 'SimpleStrategy', 'replication_factor':2 }; ";

        session.execute(query);
    }

    public void createTable() {

//        String query = "CREATE TABLE IF NOT EXISTS recommendation.content_url (" +
//                "id bigint PRIMARY KEY," +
//                "title text," +
//                "cat_name text," +
//                "price text," +
//                "details text," +
//                "dt text" +
//                ")";
//
        String query = "CREATE TABLE IF NOT EXISTS recommendation.url (" +
                "id bigint PRIMARY KEY" +
                ")";

        session.execute(query);
    }

    public void insertUrlNull(String domain, Long id) {

        String query = "INSERT INTO " + urlNull + " (id, domain) VALUES (?,?)";

        try {
            PreparedStatement statement = session.prepare(query);
            BoundStatement boundStatement = new BoundStatement(statement);
            session.execute(boundStatement.bind(id, domain));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isUrlNull(String domain, Long id) {

        String query = "SELECT * FROM " + urlNull + " WHERE domain=? AND id=?";

        try {
            PreparedStatement statement = session.prepare(query);
            BoundStatement boundStatement = new BoundStatement(statement);

            ResultSet results = session.execute(boundStatement.bind(domain, id));

            if (results.one()!= null) {
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void insertUrlParsed(String domain, Long id) {

        String query = "INSERT INTO " + urlTable + "(id, domain) VALUES (?,?)";

        try {
            PreparedStatement statement = session.prepare(query);
            BoundStatement boundStatement = new BoundStatement(statement);
            session.execute(boundStatement.bind(id, domain));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isUrlParsed(String domain, Long id) {

        String query = "SELECT * FROM " + urlTable + " WHERE domain=? AND id=?";

        try {
            PreparedStatement statement = session.prepare(query);
            BoundStatement boundStatement = new BoundStatement(statement);

            ResultSet results = session.execute(boundStatement.bind(domain, id));

            if (results.one()!= null) {
                return true;
            }

//        System.out.println(results.one() == null);
//
//        for (Row row : results) {
//            System.out.printf("id: %d,\tdomain: %s", row.getLong("id"), row.getString("domain"));
//        }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
