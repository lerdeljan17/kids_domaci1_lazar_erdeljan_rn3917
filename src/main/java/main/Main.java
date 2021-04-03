package main;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    private static final ApplicationProperties properties = new ApplicationProperties();
    private static String prefix;
    private static String dir_crawler_sleep_time;
    //    static List<String> dirsToCrawl = new ArrayList<String>();



    public static void main(String[] args) {
        loadData();
        CopyOnWriteArrayList<String> dirsToCrawl = new CopyOnWriteArrayList();
//        List<String> dirsToCrawl = new ArrayList<String>();
//        dirsToCrawl.add("src/main/resources/test");
//        dirsToCrawl.add("src/main/resources/test1");
//        dirsToCrawl.add("src/main/resources/test12");

        Thread thread = new Thread(new DirectoryCrawlerThread().builder()
                .dir_crawler_sleep_time(dir_crawler_sleep_time)
                .dirsToCrawl(dirsToCrawl)
                .file_corpus_prefix(prefix)
                .lastModifiedMap(new HashMap<>())
                .build()
        );

        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();

            if (line.equals("stop")) {
                break;

            } else if (line.contains("ad")) {
                // add segment za DirectoryCrawlerThread
                String dir = line.replaceFirst("ad ", "");
                System.out.println(dir);
                dirsToCrawl.addIfAbsent(dir);
                thread.run();
            }

        }
        sc.close();


    }

    public static void loadData() {
        prefix = new String();
        dir_crawler_sleep_time = new String();
        prefix = properties.readProperty("file_corpus_prefix");
        dir_crawler_sleep_time = properties.readProperty("dir_crawler_sleep_time");
    }
}
