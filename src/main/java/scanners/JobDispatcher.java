package scanners;

import main.Main;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class JobDispatcher extends Thread{

    @Override
    public void run() {

        while (true){
            try {

                ScanningJob job = Main.jobs.take();

                // TODO: 4.4.2021. stopping
                if (job.isPoison()){
                    System.out.println("-- Shutting down JobDispatcher");
                    System.out.println("-- Shutting down FileScanner pool");
                    Main.fileScannerPool.shutdown();
                    System.out.println("-- Shutting down resultRetriever pool");
                    Main.resultRetriever.getService().shutdown();
                    // TODO: 9.4.2021. mozda shutdownNow
                    Main.scheduledWebService.shutdownNow();
                    Main.WebService.shutdown();
                    System.out.println("-- Shutting down webService");
                    return;
                }


                if(job.getType() == ScanType.FILE){
                    FileJob fileJob = (FileJob)job;

                    // TODO: 5.4.2021. result
                    System.out.println("JobDispatcher took a new job wit corpus name: " + fileJob.getCorpusName());
//                    Future<Map<String,Integer>> result = Main.fileScannerPool.submit(new FileScannerThread(fileJob.getFilesToScan()));
                    Future<Map<String, Integer>> res = job.initiate(new FileScannerThread(fileJob.getFilesToScan()));
                    Main.resultRetriever.addCorpusResult(((FileJob) job).getCorpusName(),res);
                }

                if (job.getType() == ScanType.WEB){

                    WebJob webJob = (WebJob)job;
//                    System.out.println("JobDispatcher took a new job wit url: " + webJob.getUrl());
                    Future<Map<String, Integer>> res = ((WebJob) job).initiateWeb(new WebScannerThead(webJob));
                    Main.resultRetriever.addWebResult(((WebJob) job).getUrl(),res);

//                    System.out.println("res za web je " + ((WebJob) job).getUrl() + " " + res.get().toString());
                }




            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
