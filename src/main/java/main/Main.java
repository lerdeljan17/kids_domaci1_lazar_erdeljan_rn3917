package main;

import retriver.ResultRetriever;
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
    public static ResultRetriever resultRetriever;

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

        resultRetriever = new ResultRetriever();


        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();
//            System.out.println("line " +line );
            String param = null;
            if(line.split(" ").length > 1 ){
                param = line.split(" ")[1];
            }
            String command  = line.split(" ")[0];

            if (line.equals("stop")) {
                directoryCrawlerThread.getDirsToCrawl().addIfAbsent("stop");
                break;

            } else if (command.equals("ad")) {
                // add segment za DirectoryCrawlerThread

                if(param == null){
                    System.out.println("missing param dir");
                    continue;
                }

                String dir = line.replaceFirst("ad ", "");
                System.out.println(dir);
//                dirsToCrawl.addIfAbsent(dir);
                directoryCrawlerThread.getDirsToCrawl().addIfAbsent(dir);


            }else if (command.equals("get")){
                // TODO: 6.4.2021. ako fali param
//                System.out.println(resultRetriever.getResult(param));
                if(param.contains("summary")){
                    try {
                        Map<String, Map<String, Integer>> r = resultRetriever.getSummary(param);
//                    System.out.println(resultRetriever.getResult(param).toString());
                        if (r != null){
                            System.out.println(r.toString());
                        }else {
                            // TODO: 6.4.2021. postoji bolje resenje
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }else {
                    try {
                        Map<String, Integer> r = resultRetriever.getResult(param);
//                    System.out.println(resultRetriever.getResult(param).toString());
                        if (r != null){
                            System.out.println(r.toString());
                        }else {
                            // TODO: 6.4.2021. postoji bolje resenje
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }

            }else if (command.equals("query")){
                if(param.contains("summary")){
                    try {
                        Map<String, Map<String, Integer>> r = resultRetriever.querySummary(param);
                        if(r != null)
                            System.out.println(r.toString());
                        else
                            System.err.println("error");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }else {
                    try {
                        Map<String, Integer> r = resultRetriever.queryResult(param);
                        if(r != null)
                            System.out.println(r.toString());
                        else
                            System.err.println("error");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }

            }else if (command.equals("cfs")){
                resultRetriever.clearSummary(command);
            }else {
                System.out.println("Not a command");
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
