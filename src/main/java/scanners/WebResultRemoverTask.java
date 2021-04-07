package scanners;

import main.Main;

public class WebResultRemoverTask implements Runnable {
    @Override
    public void run() {
        Main.cachedWebJobs.clear();
//        System.out.println("obrisano " + Main.cachedWebJobs.toString());
    }
}
