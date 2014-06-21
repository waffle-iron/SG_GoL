package de.sydsoft.sg_gol.world;


/**
 * 
 * @author syd
 */
public class AlienTimer {

    private AlienWorld p;
    private SpTimer spT1;

    /**
     * 
     * @return
     */
    public SpTimer getSpT1() {
        return spT1;
    }

    /**
     * 
     * @param p
     * @param sleept
     */
    public AlienTimer(AlienWorld p, int sleept) {
        this.p = p;
        spT1 = new SpTimer(sleept);
        spT1.start();
    }

    /**
     * 
     */
    public void stopThread() {
        spT1.stop();
    }

    /**
     * 
     * @param sleepTime
     */
    public void action(int sleepTime) {
        p.callNextOrbicularStep();
    }

    /**
     * 
     */
    public void startThread() {
        spT1.start();
    }

    /**
     * 
     * @return
     */
    public int getSleepTime() {
        return spT1.sleepTime;
    }

    /**
     * 
     * @param sleepTime
     */
    public void setSleepTime(int sleepTime) {
        spT1.sleepTime = sleepTime;
    }

    // inner class
    class SpTimer implements Runnable {

        private int sleepTime = 1000; // milli-sec

        public int getSleepTime() {
            return sleepTime;
        }

        public void setSleepTime(int sleepTime) {
            this.sleepTime = sleepTime;
        }
        private Thread thread = null;

        public SpTimer() {
        }

        public SpTimer(int sleepTime) {
            this.sleepTime = sleepTime;
        }

        public synchronized void start() {
            if (thread == null) {
                thread = new Thread(this);
                thread.start();
            }
        }

        public synchronized void stop() {
            if (thread != null) {
                thread = null;
            }
        }

        // l�uft ab start(), run()-Ende = Thread-Ende!
        public void run() {
            while (thread != null) {
                try {
                    Thread.sleep(sleepTime);
                    // do anything, for example
                    action(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }// end of inner class
}