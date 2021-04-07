package scanners;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Data
public class WebScanner {
    private List<WebJob> cachedJobs;
    private ScheduledExecutorService scheduledService;
    private ExecutorService service;

    public WebScanner(){
        scheduledService = Executors.newScheduledThreadPool(1);
        cachedJobs = new CopyOnWriteArrayList<>();
        service = Executors.newCachedThreadPool();

    }






}
