package vn.vccorp.adtech.bigdata.crawlerdata.services;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huydt on 20/04/2016.
 */
public class WriteToJsonFile {
    public Map<String,List<String>> groupItemId(Map<String, String> data) {
        String json = "";
        Map<String, List<String>> dataMap = new HashMap<>();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!dataMap.containsKey(value)) {
                dataMap.put(value, new ArrayList<String>());
            }
            dataMap.get(value).add(key);
        }
        return dataMap;
//        System.out.println(dataMap);
    }
    public void writeFile(Map<String,List<String>>dataGroup) throws IOException {
        JSONObject obj = new JSONObject();
        String path = "data/titeInCat.txt";
        String line = null;
        File file = new File(path);
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
        BufferedWriter bw = new BufferedWriter(fw);

        for(Map.Entry<String,List<String>> entry: dataGroup.entrySet()){
            String key = entry.getKey();
            obj.put("Cat",key);
            List<String> value = entry.getValue();
//            JSONArray itemId = new JSONArray();
//            for(String s: value){
//                itemId.add("itemId:" + s);
//            }
            obj.put("ItemId", value.toString());
            bw.write(obj.toJSONString());
            bw.newLine();
        }

        bw.close();
    }
}
