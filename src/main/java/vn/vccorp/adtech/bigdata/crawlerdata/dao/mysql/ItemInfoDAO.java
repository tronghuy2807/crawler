package vn.vccorp.adtech.bigdata.crawlerdata.dao.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.ItemInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.bo.NodeInfo;
import vn.vccorp.adtech.bigdata.crawlerdata.dao.base.BaseMysqlDbDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by thuyenhx on 28/08/2015.
 */
public class ItemInfoDAO extends BaseMysqlDbDAO {

    private static Logger logger = LoggerFactory.getLogger(ItemInfoDAO.class);

    public ItemInfoDAO() throws Exception{

    }

    public List<String> getAllUrl() throws SQLException {

        List<String> result = new ArrayList<>();
        PreparedStatement ps = null;
        String sql = "SELECT DISTINCT (url) " +
                "FROM crawl_neo4j ";
        try{
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String url = rs.getNString("url").toLowerCase();
                result.add(url);
            }
        }catch (Exception e) {
            logger.error("error get all url: ", e);
        }finally {
            if (ps != null) {
                ps.close();
            }
        }

        return result;
    }

    public List<String> getMuachung() throws SQLException {

        List<String> result = new ArrayList<>();
        PreparedStatement ps = null;
        String sql = "SELECT DISTINCT (url) " +
                "FROM mc_deal_info_date WHERE sub_cat IS NULL ";
        try{
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String url = rs.getNString("url").toLowerCase();
                result.add(url);
            }
        }catch (Exception e) {
            logger.error("error get all url: ", e);
        }finally {
            if (ps != null) {
                ps.close();
            }
        }

        return result;
    }
    public void updateSubCatMC(Map<Integer, String> dataMap) throws SQLException {
        if(dataMap!=null) {
            String sql = "UPDATE mc_deal_info_date SET " +
                    "sub_cat=?" +
                    "Where id = ?";
            PreparedStatement statement = null;
            try {
                statement = conn.prepareStatement(sql);
                for(Map.Entry<Integer,String> data : dataMap.entrySet()){
                    statement.setString(1,data.getValue());
                    statement.setInt(2,data.getKey());
                    statement.addBatch();
                    logger.info("add Item "+ data.getValue()+ " to Batch");
                }
                statement.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                logger.error("error " + e);
                conn.rollback();
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }

        }
    }

    public void insertItemInfo(List<ItemInfo> lstData) throws SQLException {

        String sql = "INSERT INTO " +
                "crawl_neo4j (id, alias, url, product_name, title, cat_name, details, dt)" +
                " VALUES (?,?,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE title=?, cat_name=?, details=?, dt=? ";
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(sql);

            for (ItemInfo item : lstData) {
                ps.setLong(1, item.getId());
                ps.setNString(2, item.getAlias());
                ps.setNString(3, item.getUrl());
                ps.setNString(4, item.getProductName());
                ps.setNString(5, item.getTitle());
                ps.setNString(6, item.getCatName());
                ps.setNString(7, item.getDetails());
                ps.setNString(8, item.getDate());
                ps.setNString(9, item.getTitle());
                ps.setNString(10, item.getCatName());
                ps.setNString(11, item.getDetails());
                ps.setNString(12, item.getDate());
                ps.addBatch();

            }
            ps.executeBatch();
            conn.commit();
        }catch (SQLException e) {
            System.out.println("error insert data!");
            e.printStackTrace();
            conn.rollback();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public void insertInfoItem(List<ItemInfo> lstItem) throws SQLException {

        if (lstItem != null) {

            String sql = "INSERT INTO mc_data_train (id, cat_id, url, title, details) VALUES (?,?,?,?,?) ";
            PreparedStatement ps = null;

            try {
                ps = conn.prepareStatement(sql);

                for (ItemInfo item : lstItem) {
                    ps.setLong(1, item.getId());
                    ps.setLong(2, item.getCatId());
                    ps.setNString(3, item.getUrl());
                    ps.setNString(4, item.getTitle());
                    ps.setNString(5, item.getDetails());

                    ps.addBatch();
                }

                ps.executeBatch();
                conn.commit();

            } catch (Exception e) {
                e.printStackTrace();
                conn.rollback();
            }finally {
                if (ps != null) {
                    ps.close();
                }
            }
        }
    }

    public List<ItemInfo> getInfoItem() throws SQLException {

        List<ItemInfo> lstItem = new ArrayList<>();

        String sql = "SELECT id, cat_id, url, title, short_description FROM mc_deal_info_date ";
        PreparedStatement ps = null;
        try{

            ps= conn.prepareStatement(sql);
//            ps.setInt(1, 12);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                ItemInfo item = new ItemInfo();
                item.setId(rs.getLong("id"));
                item.setTitle(rs.getString("title"));
                item.setDetails(rs.getString("short_description"));
                item.setUrl(rs.getString("url"));
                item.setCatId(rs.getInt("cat_id"));

                lstItem.add(item);
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (ps != null) {
                ps.close();
            }
        }

        return lstItem;
    }

    public void dispose() throws SQLException {
        if(conn != null) {
            conn.close();
        }
    }

}
