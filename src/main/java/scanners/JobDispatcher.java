package scanners;

import main.Main;

public class JobDispatcher extends Thread{

    @Override
    public void run() {

        while (true){
            try {

                ScanningJob job = Main.jobs.take();

                // TODO: 4.4.2021. stopping

                if(job.getType() ==ScanType.FILE){

                }




            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
