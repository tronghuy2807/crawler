package vn.vccorp.adtech.bigdata.crawlerdata;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

/**
 * Example program to list links from a URL.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String path = "data/thethao";
        FileWriter fw = new FileWriter(path,true);
        BufferedWriter bw = new BufferedWriter(fw);

        int index = 1;

//        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        for(int i=1;i<20;i++) {
            String url ="";

            if(i!=1){
                url = "http://dantri.com.vn/the-thao/trang-"+i+".htm";
            }else {
                url = "http://dantri.com.vn/the-thao.htm";
            }
            print("Fetching %s...", url);

            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");
            for (Element l : links) {
                String s = l.ownText();
                System.out.println("xxx");
            }
//        Elements imports = doc.select("link[href]");


//        print("\nImports: (%d)", imports.size());
//        for (Element link : imports) {
//            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
//        }

//        print("\nLinks: (%d)", links.size());
            Set<String> dkmHiep = new HashSet<>();
            for (Element link : links) {
                print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
                String slink = link.attr("abs:href");

                dkmHiep.add(slink);
                System.out.println("dkmNam");

            }

            for (String slink : dkmHiep) {
                if (slink.contains(".htm") && slink.contains("the-thao/")) {
                    Document doc1 = Jsoup.connect(slink).get();
                    String description = doc1.select("meta[name=description]").get(0).attr("content");
                    Elements content = doc1.select("p");
                    Elements title = doc1.select("title");
                    System.out.println(title.text());
                    System.out.println(content.text());

                    String combine = String.valueOf(index)+"@@@"+title.text()+"@@@"+description+"@@@"+content.text();

                    bw.write(combine);
                    bw.newLine();
                    bw.newLine();
                    index++;
                }
            }
        }
        bw.close();
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}
