package vn.vccorp.adtech.bigdata.crawlerdata.dao.cass;

import com.datastax.driver.core.Session;
import junit.framework.TestCase;

public class CassandraClusterTest extends TestCase {

    public void testCass() {

//        Session session = CassandraCluster.getInstance().getSession();


        ItemInfoCass itemInfoCass = new ItemInfoCass();
//        itemInfoCass.createSchema();
//        itemInfoCass.createTable();
        itemInfoCass.insertUrlParsed("lazada", 425948989L);
//        System.out.println(itemInfoCass.isPasseUrl("lazada", 425948989L));

    }

}