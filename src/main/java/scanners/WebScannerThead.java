package scanners;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.ApplicationProperties;
import main.Main;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

@Data
@AllArgsConstructor
public class WebScannerThead implements Callable<Map<String,Integer>> {

    private WebJob job;
    @Override
    public Map<String, Integer> call() throws Exception {

//        System.out.println(job.getUrl());

        Document doc = null;
        try {
//            System.out.println(job.getUrl());
           doc = Jsoup.connect(job.getUrl()).get();
        }catch (Exception e){
            System.out.println("Inaccessible url " + job.getUrl() + " -----");
            job.setInvalid(true);
            Main.resultRetriever.getWebResults().remove(job.getUrl());
            return null;
        }

        Elements links = doc.select("a[href]");
//        System.out.println(links.size());

        List<String> urls = new ArrayList<>();

        for (Element link : links) {
            urls.add(link.attr("abs:href"));
        }
//        System.out.println(urls);

        if(job.getHopCount() > 0){
            for (String url : urls) {
                if (url == null || url.isEmpty() || url.isBlank())continue;
                try {
                    System.out.println("url iz petlje " + url);
                    url = url.replaceAll(" ", "%20");
                    WebJob webJob = new WebJob(ScanType.WEB,false,url,job.getHopCount() - 1);
                    if (Main.cachedWebJobs.contains(webJob)){
                        System.out.println("Job with that url already exists");
                        continue;
                    }
                    Main.cachedWebJobs.add(webJob);
                    Main.jobs.put(webJob);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }



        return countWords(job.getUrl());

    }



    private static Map<String, Integer>  countWords(String url)
    {
        Map<String,Integer> wordCountMap = new HashMap<>();
        Scanner file= null;
        try {
            file = new Scanner(Jsoup.connect(url).get().text());
        }  catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            assert file != null;
            if (!file.hasNext()) break;
            String word=file.next();
            word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
//            System.out.println("rec je " + word + " " + word.length());
            if(!ApplicationProperties.getInstance().getKeywords().contains(word)){
                //  System.out.println(ApplicationProperties.getInstance().getKeywords());
//                System.out.println("nema me");
                continue;
            }

            Integer count=wordCountMap.get(word);
            if(count!=null)
                count++;
            else
                count=1;
            wordCountMap.put(word,count);

        }
        file.close();
//        System.out.println(wordCountMap);
        return wordCountMap;
    }
//
//    public static void main(String[] args) {
//        countWords("https://en.wikipedia.org/wiki/Number");
//        countWords("https://en.wikipedia.org/wiki/Number");
//
//    }

}
