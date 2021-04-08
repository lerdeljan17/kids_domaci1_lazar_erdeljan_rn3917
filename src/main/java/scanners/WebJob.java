package scanners;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import main.Main;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class WebJob implements ScanningJob {

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        WebJob webJob = (WebJob) o;
//        return this.url.equals(webJob.url);
//    }



    @EqualsAndHashCode.Include
    private String url;
    private ScanType type = ScanType.WEB;
    private int hopCount;
    private Future<Map<String,Integer>> jobResult;
    private boolean isPoison;
    private boolean invalid;

    public WebJob(ScanType file,  boolean b, String name,int hopCount) {
        this.type = file;
        this.isPoison =  b;
        this.url = name;
        this.hopCount = hopCount;
    }


    @Override
    public ScanType getType() {
        return type;
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public Future<Map<String, Integer>> initiate(RecursiveTask<?> task) {
        return null;
    }


    public Future<Map<String, Integer>> initiateWeb(Callable webTask) {
        this.jobResult = Main.WebService.submit(webTask);
        Main.cachedWebJobs.add(((WebScannerThead)webTask).getJob());
        return jobResult;
    }
}
