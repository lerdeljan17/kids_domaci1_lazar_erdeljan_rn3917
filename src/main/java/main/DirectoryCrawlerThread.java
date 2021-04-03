package main;

import lombok.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DirectoryCrawlerThread implements Runnable {


    private String file_corpus_prefix;
    private String dir_crawler_sleep_time;
    private CopyOnWriteArrayList<String> dirsToCrawl;
    private HashMap<String,Long> lastModifiedMap ;


    public void crawl(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getAbsolutePath());
                crawl(file.listFiles());
            } else if(!file.isDirectory() && file.getParentFile().getName().startsWith(file_corpus_prefix)){
                Long lm = lastModifiedMap.putIfAbsent(file.getAbsolutePath(),file.lastModified());
                boolean startJob = false;
                if(lm == null || !lm.equals(file.lastModified())){
                    // TODO: 3.4.2021. job treba startovati
                    startJob = true;
                    System.out.println("start job");
                }else {
                    // TODO: 3.4.2021. job ne treba statovati
                    System.out.println("dont start job");

                }
                System.out.println("File: " + file.getAbsolutePath() + " lm: " + file.lastModified());
            }
        }
    }

    public void run() {
        for (String s : dirsToCrawl) {
            File dir = new File(s);
            crawl(dir.listFiles());
        }

    }
}
