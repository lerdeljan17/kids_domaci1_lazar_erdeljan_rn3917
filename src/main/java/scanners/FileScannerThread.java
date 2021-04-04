package scanners;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

@AllArgsConstructor
public class FileScannerThread extends  RecursiveTask<List<File>> {



    @Override
    protected List<File> compute() {
        return null;
    }

}
