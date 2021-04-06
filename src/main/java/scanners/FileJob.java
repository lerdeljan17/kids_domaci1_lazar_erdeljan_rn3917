package scanners;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.Main;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileJob implements ScanningJob {

    private ScanType type = ScanType.FILE;
    private List<File> filesToScan;
    private boolean isPoison;
    private String corpusName;
    private Future<Map<String,Integer>> jobResult;

    public FileJob(ScanType file, List<File> toSend, boolean b, String name) {
        this.type = file;
        this.filesToScan = toSend;
        this.isPoison =  b;
        this.corpusName = name;
    }

    @Override
    public ScanType getType() {
        return type;
    }

    @Override
    public String getQuery() {
        return corpusName;
    }

    @Override
    public Future<Map<String, Integer>> initiate(RecursiveTask task) {
//        System.out.println("doso do initiate");
        this.jobResult =  Main.fileScannerPool.submit(task);
//        try {
////            System.out.println("result for corpus " + corpusName+  " " + jobResult.get().toString());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        return jobResult;
    }

}
