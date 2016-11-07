package vn.vccorp.adtech.bigdata.crawlerdata.bo;

import java.util.List;

/**
 * Created by thuyenhx on 01/02/2016.
 */
public class NodeInfo {
    private String name;
    private String alias;
    private String url;

    public NodeInfo() {}

    public NodeInfo(String alias, String name, String url) {
        this.alias = alias;
        this.name = name;
        this.url = url;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
