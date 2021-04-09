package scanners;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.Main;

import java.net.URI;
import java.net.URISyntaxException;

@Data
@AllArgsConstructor
public class WebResultRemoverTask implements Runnable {
   private WebJob webJob;
    @Override
    public void run() {
        Main.cachedWebJobs.remove(webJob);
        try {
            Main.resultRetriever.getWebDomainResults().remove(getDomainName(webJob.getUrl()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        System.out.println("obrisano " + Main.cachedWebJobs.toString());
    }
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
