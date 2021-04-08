package retriver;

import lombok.Data;
import scanners.ScanType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.*;

@Data
public class ResultRetriever {

    private Future<Map<String, Map<String, Integer>>> resultSummaryCache ;
    private Map<String, Future<Map<String, Integer>>> files = new ConcurrentHashMap<>();

    private Map<String, Future<Map<String, Integer>>> webResults = new ConcurrentHashMap<>();
    private Map<String, Future<Map<String, Integer>>> webDomainResults = new ConcurrentHashMap<>();
    private Future<Map<String, Map<String, Integer>>> webResultSummaryCache ;


//    private Future<Map<String, Map<String, Integer>>> summaryFiles;
    private ExecutorService service = Executors.newCachedThreadPool();
//    private ExecutorCompletionService<Map<String, Map<String, Integer>>> summaryFilesPool ;


    public Map<String, Integer> getResult(String query) throws Exception {
        String parameter;
        String type;

        String[] queryParts = query.split("\\|");
//        System.out.println(Arrays.toString(queryParts));

        if(queryParts.length < 2){
            // TODO: 6.4.2021. staviti exception
            System.out.println("Incomplete query");
            return null;
        }
        parameter = queryParts[1];
        type = queryParts[0];

        if(type.equals("file")){


            if(!files.containsKey(parameter)){
                throw new Exception("Corpus not in jobs");
            }


            Future<Map<String, Integer>> res = files.get(parameter);

            if(res != null){
                try {
//                    System.out.println("resulatat je " + res.get());
                    return res.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }

        }else if(type.equals("web")){


            Future<Map<String, Integer>> res = webDomainResults.get(parameter);

            if (res == null) {

                Future<Map<String, Integer>> domainRes = service.submit(new WebDomainSumTask(parameter));

                webDomainResults.put(parameter, domainRes);
//                System.out.println("rt1 " +domainRes.get());
                return domainRes.get();
            }else {
//                System.out.println("rt2 " +res.get());
                return res.get();
            }


        }


        return null;
    }



    public Map<String, Integer> queryResult(String query) throws Exception {
        String parameter;
        String type;

        String[] queryParts = query.split("\\|");
//        System.out.println(Arrays.toString(queryParts));

        if(queryParts.length < 2){
            // TODO: 6.4.2021. staviti exception
            System.out.println("Incomplete query");
            return null;
        }
        parameter = queryParts[1];
        type = queryParts[0];
        if(type.equals("file")) {


            if (!files.containsKey(parameter)) {
                throw new Exception("Corpus not in jobs");
            }


            Future<Map<String, Integer>> res = files.get(parameter);
            if(res != null){
                if(res.isDone()){

                    try {
//                    System.out.println("resulatat je " + res.get());
                        return res.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }else {
                    throw new Exception("job not done yet");
                }
            }
        }else if(type.equals("web")){


            Future<Map<String, Integer>> res = webDomainResults.get(parameter);

            if (res == null) {

                Future<Map<String, Integer>> domainRes = service.submit(new WebDomainSumTask(parameter));

                webDomainResults.put(parameter, domainRes);
//                System.out.println("rt1 " +domainRes.get());
//                return domainRes.get();
                throw new Exception("job not done yet");
            }else {
                if (res.isDone()){
                    return res.get();
                }else {
                    throw new Exception("job not done yet");
                }

//                System.out.println("rt2 " +res.get());

            }


        }


            return null;
        }

    public void clearSummary(String query) {

        if(query.equals("cfs")){
            resultSummaryCache = null;
        }else if (query.equals("cws")){
            webResultSummaryCache = null;
        }
        System.out.println("summaries cleared");

    }

    public Map<String, Map<String, Integer>> getSummary(String query) throws Exception {
        String parameter;
        String type;

        String[] queryParts = query.split("\\|");
//        System.out.println(Arrays.toString(queryParts));

        if(queryParts.length < 2){
            // TODO: 6.4.2021. staviti exception
            System.out.println("Incomplete query");
            return null;
        }
        parameter = queryParts[1];
        type = queryParts[0];

        if(type.equals("file")) {

            if (parameter.equals("summary")) {
                if(files.isEmpty()){
                    throw new Exception("no files to query");
                }
                if (resultSummaryCache != null && resultSummaryCache.isDone()) {
//                    System.out.println(summaryFilesPool.take().get().toString());
//                    resultSummaryCache.putAll(summaryFilesPool.take().get());
                    return resultSummaryCache.get();
                } else {
//                    summaryFilesPool = new ExecutorCompletionService<>(
//                            service);
                    resultSummaryCache = service.submit(new FileJobsSumThread());
//                    System.out.println(summaryFilesPool.take().get());
//                    resultSummaryCache.putAll(summaryFilesPool.take().get());
                    return resultSummaryCache.get();
                }

            }

            if (!files.containsKey(parameter)) {
                throw new Exception("Corpus not in jobs");
            }
        }else if (type.equals("web")){

            if (parameter.equals("summary")) {
                if(webResults.isEmpty()){
                    throw new Exception("no web pages to query");
                }
                if (webResultSummaryCache != null && webResultSummaryCache.isDone()) {
//                    System.out.println(summaryFilesPool.take().get().toString());
//                    resultSummaryCache.putAll(summaryFilesPool.take().get());
                    return webResultSummaryCache.get();
                } else {
//                    summaryFilesPool = new ExecutorCompletionService<>(
//                            service);
                    webResultSummaryCache = service.submit(new WebCorpusSumTask());
//                    System.out.println(summaryFilesPool.take().get());
//                    resultSummaryCache.putAll(summaryFilesPool.take().get());
                    return webResultSummaryCache.get();
                }

            }

        }

        return null;
    }

    public Map<String, Map<String, Integer>> querySummary(String query) throws Exception {

        String parameter;
        String type;

        String[] queryParts = query.split("\\|");
//        System.out.println(Arrays.toString(queryParts));

        if(queryParts.length < 2){
            // TODO: 6.4.2021. staviti exception
            System.out.println("Incomplete query");
            return null;
        }
        parameter = queryParts[1];
        type = queryParts[0];

        if(type.equals("file")) {

            if (parameter.equals("summary")) {
                if(files.isEmpty()){
                    throw new Exception("no files to query");
                }
                if (resultSummaryCache != null && resultSummaryCache.isDone()) {
//                    System.out.println(summaryFilesPool.take().get().toString());
//                    resultSummaryCache.putAll(summaryFilesPool.take().get());
                    return resultSummaryCache.get();
                } else {
//                    summaryFilesPool = new ExecutorCompletionService<>(
//                            service);
                    resultSummaryCache = service.submit(new FileJobsSumThread());
//                    System.out.println(summaryFilesPool.take().get());
//                    return summaryFilesPool.take().get();
                    throw new Exception("job not done yet");
//                    System.out.println("job not done");
//                    return null;
                }

            }

            if (!files.containsKey(parameter)) {
                throw new Exception("Corpus not in jobs");
            }
        }else if (type.equals("web")){

            if (parameter.equals("summary")) {
                if(webResults.isEmpty()){
                    throw new Exception("no web pages to query");
                }
                if (webResultSummaryCache != null && webResultSummaryCache.isDone()) {
//                    System.out.println(summaryFilesPool.take().get().toString());
//                    resultSummaryCache.putAll(summaryFilesPool.take().get());
                    return webResultSummaryCache.get();
                } else {
//                    summaryFilesPool = new ExecutorCompletionService<>(
//                            service);
                    webResultSummaryCache = service.submit(new WebCorpusSumTask());
//                    System.out.println(summaryFilesPool.take().get());
//                    resultSummaryCache.putAll(summaryFilesPool.take().get());
//                    return webResultSummaryCache.get();
                    throw new Exception("jobs not done yet");
                }

            }

        }

        return null;
    }

    public void addCorpusResult(String corpusName, Future<Map<String, Integer>> corpusResult) {

       files.put(corpusName,corpusResult);


    }


    public void addWebResult(String url, Future<Map<String, Integer>> corpusResult) {

        webResults.put(url,corpusResult);



    }

}
