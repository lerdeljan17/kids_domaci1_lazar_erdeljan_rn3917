package scanners;

import main.Main;

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
                    System.out.println("JobDispatcher took a new job wit corpus name: " + fileJob.getCorpusName());
                    Main.fileScannerPool.submit(new FileScannerThread(fileJob.getFilesToScan()));
                }




            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
