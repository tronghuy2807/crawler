package vn.vccorp.adtech.bigdata.crawlerdata.bo;

/**
 * Created by thuyenhx on 26/08/2015.
 */
public class ItemInfo {

    private String title;
    private String alias;
    private String price;
    private String details;
    private String url;
    private String catName;
    private Long id;
    private int catId;
    private String date;
    private String keyWord;
    private String productName;

    public ItemInfo() {
        this.title = "";
        this.price = "";
        this.details = "";
    }

    public ItemInfo(String title, String price, String details, String url, String catName) {
        this.title = title;
        this.price = price;
        this.details = details;
        this.url = url;
        this.catName = catName;
    }

    public ItemInfo(String title, String price, String url, String catName) {
        this.title = title;
        this.price = price;
        this.url = url;
        this.catName = catName;
    }

    public ItemInfo(String title, String url, Long id, String keyWord, String date) {
        this.title = title;
        this.url = url;
        this.id = id;
        this.keyWord = keyWord;
        this.date = date;
    }

    public ItemInfo(long id, String alias, String url, String productName, String title, String catName, String details, String date) {
        this.id = id;
        this.alias = alias;
        this.url = url;
        this.productName = productName;
        this.title = title;
        this.catName = catName;
        this.details = details;
        this.date = date;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return getUrl() + ", " + getTitle() + ", " + getCatName() + ", " + getPrice() + ", " + getDetails();
    }
}
