package main;

import lombok.Data;
import lombok.Singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
public class ApplicationProperties {

    private static ApplicationProperties instance = null;

    private final Properties properties;

    private String prefix;
    private String dir_crawler_sleep_time;
    private List<String> keywords;
    private long file_scanning_size_limit;
    private int hop_count;
    private long url_refresh_time;


    private ApplicationProperties() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));

        } catch (IOException ioex) {
            Logger.getLogger(getClass().getName()).log(Level.ALL, "IOException Occured while loading properties file::::" +ioex.getMessage());
        }
        loadData();
    }

    public static ApplicationProperties getInstance(){
    {
        if (instance == null)
            instance = new ApplicationProperties();

        return instance;
    }
}

    public String readProperty(String keyName) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Reading Property " + keyName);
        return properties.getProperty(keyName, "There is no key in the properties file");
    }

    public void loadData(){
        prefix = new String();
        dir_crawler_sleep_time = new String();
        prefix = readProperty("file_corpus_prefix");
        dir_crawler_sleep_time = readProperty("dir_crawler_sleep_time");
        String keyw = readProperty("keywords");
        keywords = new ArrayList<>(Arrays.asList(keyw.split(",")));
//        System.out.println(keywords);
        file_scanning_size_limit = Long.parseLong(readProperty("file_scanning_size_limit"));
        hop_count = Integer.parseInt(readProperty("hop_count"));
        url_refresh_time = Long.parseLong(readProperty("url_refresh_time"));
    }

}
