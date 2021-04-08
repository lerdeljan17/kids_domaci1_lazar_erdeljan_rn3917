package main;

import lombok.*;
import scanners.FileJob;
import scanners.ScanType;
import scanners.ScanningJob;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DirectoryCrawlerThread extends Thread {


    private CopyOnWriteArrayList<String> dirsToCrawl = new CopyOnWriteArrayList<>();
    private HashMap<String, Long> lastModifiedMap = new HashMap<>();
    private BlockingQueue<ScanningJob> jobs = Main.jobs;
    private boolean startJob = false;
    private List<File> jobFiles = new CopyOnWriteArrayList<>();

    public void crawl(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                if (startJob) {
                    try {
                        List<File> toSend = new ArrayList<>();
                        toSend.addAll(jobFiles);
                        jobs.put(new FileJob(ScanType.FILE, toSend,false,jobFiles.get(0).getParentFile().getName()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    jobFiles.clear();
                }
//                System.out.println("Directory: " + file.getAbsolutePath());
                startJob = false;
                jobFiles.clear();
                crawl(file.listFiles());
            } else if (!file.isDirectory() && file.getParentFile().getName().startsWith(ApplicationProperties.getInstance().getPrefix())) {
                Long lm = lastModifiedMap.put(file.getAbsolutePath(), file.lastModified());
                jobFiles.add(file);
//                if(lm !=null)
//                    System.out.println(lm.equals(file.lastModified()));
                if (lm == null || !lm.equals(file.lastModified())) {
                    // TODO: 3.4.2021. job treba startovati
                    if (Main.resultRetriever.getResultSummaryCache() != null) {
                        Main.resultRetriever.setResultSummaryCache(null);
                    }

                    startJob = true;
//                    System.out.println("start job");
//                    System.out.println("lm " + lm  + file.getName());
                } else {
                    // TODO: 3.4.2021. job ne treba statovati
//                    System.out.println("dont start job");
                }

//                System.out.println("File: " + file.getAbsolutePath() + " lm: " + file.lastModified());
            }
        }
    }

    public void run() {
        while (true) {

            for (String s : dirsToCrawl) {
                if(s.equals("stop")){
                    System.out.println("DirectoryCrawlerThread stopped");
                    try {
                        jobs.put(new FileJob(ScanType.FILE,null,true,null));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                File dir = null;
                try {
                    dir  = new File(s);
                    if (!dir.exists() || !dir.canRead()){
                        System.out.println("Can not open or find file with path " + s);
                        continue;
                    }
                }catch (Exception e){
                    System.out.println("Can not open or find file with path " + s);
                    continue;
                }

                crawl(dir.listFiles());
            }

            try {
                System.out.println("-- DirectoryCrawlerThread going to sleep");
                // TODO: 4.4.2021. *10 da bi sporije islo izbrisati
                Thread.sleep(Long.parseLong(ApplicationProperties.getInstance().getDir_crawler_sleep_time())*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
