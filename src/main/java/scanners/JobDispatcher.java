package scanners;

import main.Main;

import java.util.Map;
import java.util.concurrent.Future;

public class JobDispatcher extends Thread{

    @Override
    public void run() {

        while (true){
            try {

                ScanningJob job = Main.jobs.take();

                if(job.getType() == ScanType.FILE){
                    FileJob fileJob = (FileJob)job;

                    // TODO: 4.4.2021. stopping
                    if (fileJob.isPoison()){
                        System.out.println("-- Shutting down JobDispatcher");
                        return;
                    }
                    // TODO: 5.4.2021. result
                    System.out.println("JobDispatcher took a new job wit corpus name: " + fileJob.getCorpusName());
//                    Future<Map<String,Integer>> result = Main.fileScannerPool.submit(new FileScannerThread(fileJob.getFilesToScan()));
                    Future<Map<String, Integer>> res = job.initiate(new FileScannerThread(fileJob.getFilesToScan()));
                    Main.resultRetriever.addCorpusResult(((FileJob) job).getCorpusName(),res);
                }




            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
