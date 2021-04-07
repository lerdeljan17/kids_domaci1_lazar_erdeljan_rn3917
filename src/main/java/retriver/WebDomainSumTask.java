package retriver;

import lombok.AllArgsConstructor;
import main.Main;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
@AllArgsConstructor
public class WebDomainSumTask implements Callable<Map<String, Integer>> {

    private String domain;
    @Override
    public Map<String, Integer> call() throws Exception {

        Map<String, Map<String, Integer>> toReturn = new ConcurrentHashMap<>();

        for (Map.Entry<String, Future<Map<String, Integer>>> entry: Main.resultRetriever.getWebResults().entrySet()){
//            toReturn.put(entry.getKey(),entry.getValue().get())
            if(getDomainName(entry.getKey()).equals(domain)){
//                toReturn.(entry.getKey())
                if (toReturn.containsKey(getDomainName(entry.getKey()))){
                    toReturn.put(entry.getKey(), sumMaps(entry.getValue().get(), toReturn.get(entry.getKey())));
                }else {
                    toReturn.put(entry.getKey(), entry.getValue().get());
                }
            }

        }


        return toReturn.get(domain);
    }

    public Map<String,Integer> sumMaps (Map<String,Integer> mapA,Map<String,Integer> mapB){

           for (Map.Entry<String,Integer> entry : mapB.entrySet()) {
            if(mapA.containsKey(entry.getKey())){
//                    entry.setValue(entry.getValue() + leftResult.get(entry.getKey()));
                Integer sum = mapA.get(entry.getKey()) + entry.getValue();
                mapA.put(entry.getKey(),sum);
            }else {
                mapA.put(entry.getKey(),entry.getValue());
            }

        }
           return mapA;


    }


    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}