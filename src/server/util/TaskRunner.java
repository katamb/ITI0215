package server.util;

import java.util.TimerTask;

public class TaskRunner extends TimerTask {
    public TaskRunner() {
        //Some stuffs
    }

    @Override
    public void run() {
        // ToDo: ask for ip's
        System.out.println("Hi see you after 60 seconds");
    }
}