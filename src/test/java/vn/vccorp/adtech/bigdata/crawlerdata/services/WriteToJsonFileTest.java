package vn.vccorp.adtech.bigdata.crawlerdata.services;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huydt on 20/04/2016.
 */
public class WriteToJsonFileTest extends TestCase {

    public void testWriteJson() throws Exception {
        WriteToJsonFile writeToJsonFile = new WriteToJsonFile();
        Map<String, String> test = new HashMap<>();
        test.put("124","Ẩm thực & Nhà hàng > Buffet & BBQ");
        test.put("111","Ẩm thực & Nhà hàng > Buffet & BBQ");
        test.put("3","B");
        test.put("4","C");
        test.put("5","C");
        writeToJsonFile.writeFile(writeToJsonFile.groupItemId(test));
    }
}