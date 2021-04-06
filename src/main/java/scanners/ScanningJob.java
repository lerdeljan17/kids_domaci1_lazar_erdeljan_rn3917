package scanners;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public interface ScanningJob {


    ScanType getType();
    String getQuery();
    Future<Map<String, Integer>>  initiate(RecursiveTask<?> task);

}
