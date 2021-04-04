package scanners;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileJob implements ScanningJob {


    private ScanType type = ScanType.FILE;
    private List<File> filesToScan;

    @Override
    public ScanType getType() {
        return type;
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public Future<Map<String, Integer>> initiate() {
        return null;
    }
}
