package main;

import scanners.JobDispatcher;
import scanners.ScanningJob;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {



    public static String prefix;
    public static String dir_crawler_sleep_time;
    //    static List<String> dirsToCrawl = new ArrayList<String>();
    public static CopyOnWriteArrayList<String> dirsToCrawl = new CopyOnWriteArrayList();
    public static BlockingQueue<ScanningJob> jobs = new LinkedBlockingQueue<>();
    public static ForkJoinPool fileScannerPool;
    public static JobDispatcher jobDispatcher;

    public static DirectoryCrawlerThread directoryCrawlerThread;

    public static void main(String[] args) {
        loadData();

//        Thread directoryCrawlerThread = new Thread(new DirectoryCrawlerThread().builder()
////                .dirsToCrawl(dirsToCrawl)
//                .lastModifiedMap(new HashMap<>())
//                .jobs(jobs)
//                .jobFiles(new ArrayList<>())
//                .build()
//        );
        directoryCrawlerThread = new DirectoryCrawlerThread();
        directoryCrawlerThread.start();

        jobDispatcher = new JobDispatcher();
        jobDispatcher.start();

        fileScannerPool = new ForkJoinPool();


        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();

            if (line.equals("stop")) {
                directoryCrawlerThread.getDirsToCrawl().addIfAbsent("stop");
                break;

            } else if (line.contains("ad")) {
                // add segment za DirectoryCrawlerThread
                String dir = line.replaceFirst("ad ", "");
                System.out.println(dir);
//                dirsToCrawl.addIfAbsent(dir);
                directoryCrawlerThread.getDirsToCrawl().addIfAbsent(dir);


            }

        }
        sc.close();


    }

    public static void loadData() {
        prefix = new String();
        dir_crawler_sleep_time = new String();
        prefix = ApplicationProperties.getInstance().readProperty("file_corpus_prefix");
        dir_crawler_sleep_time = ApplicationProperties.getInstance().readProperty("dir_crawler_sleep_time");
    }
}
