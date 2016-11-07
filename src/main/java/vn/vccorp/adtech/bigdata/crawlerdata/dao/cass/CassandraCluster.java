package vn.vccorp.adtech.bigdata.crawlerdata.dao.cass;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import org.apache.commons.configuration.Configuration;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;

/**
 * Created by thuyenhx on 09/09/2015.
 */
public class CassandraCluster {

    private static CassandraCluster instance = null;
    private Cluster cluster;
    private Session session;

    private Configuration conf = SystemInfo.getConfiguration();

    private CassandraCluster() {

        cluster = Cluster.builder()
                .addContactPoint(conf.getString("cass.cluster.host"))
                .withPort(conf.getInt("cass.cluster.port"))
                .withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
                .build();

        String keyspace = conf.getString("cass.keyspace.name");

        session = cluster.connect(keyspace);

        System.out.printf("Connected to cluster: %s\n", session.getLoggedKeyspace());

    }

    public Session getSession() {
        return session;
    }

    public static CassandraCluster getInstance() {

        if (instance == null) {
            synchronized (CassandraCluster.class) {
                if (instance == null) {
                    instance = new CassandraCluster();
                }
            }
        }

        return instance;
    }

}
