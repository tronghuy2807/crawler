package vn.vccorp.adtech.bigdata.crawlerdata.dao.neo4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.NodeInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.base.Neo4jManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thuyenhx on 27/01/2016.
 */
public class Neo4jDAO {
    private Connection conn;
    private static Logger logger = LoggerFactory.getLogger(Neo4jDAO.class);

    public Neo4jDAO() throws SQLException {
        conn = Neo4jManager.createConnection();
        conn.setAutoCommit(false);
    }

    public List<NodeInfo> getUrlByNode(NodeInfo info) throws Exception {
        List<NodeInfo> lstNode = new ArrayList<>();
        String sql = "match (n:Product)-[r]->(a:Attribute)-[r2]->(i:Instance) " +
                "where n.alias={0} AND a.name=~'.*Link.*'" +
                "return i.value; ";
        String alias = info.getAlias();
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement(sql);
            st.setString(0, alias);

            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                String url = rs.getString("i.value").toLowerCase();
                NodeInfo node = new NodeInfo();
                node.setAlias(info.getAlias());
                node.setName(info.getName());
                node.setUrl(url);

                lstNode.add(node);
            }
        }catch (Exception e) {
            logger.error("error get url: ", e);
        }

        return lstNode;
    }

    public List<NodeInfo> getAllNode() throws Exception {
        List<NodeInfo> lstAlias = new ArrayList<>();
        String sql = "match (n:Product)-[r]->(a:Attribute)" +
                "where a.name=~'.*Link.*' " +
                "return n.name,n.alias ";
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                NodeInfo info = new NodeInfo();
                info.setAlias(rs.getString("n.alias"));
                info.setName(rs.getString("n.name"));

                lstAlias.add(info);
            }
        }catch (Exception e) {
            logger.error("error get node: ", e);
        }

        return lstAlias;
    }

    public void dispose() throws SQLException {
        if(conn != null) {
            conn.close();
        }
    }

}
