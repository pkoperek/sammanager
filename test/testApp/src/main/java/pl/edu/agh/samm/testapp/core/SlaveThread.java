package pl.edu.agh.samm.testapp.core;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SlaveThread extends Thread implements Runnable, ISlave, Serializable {
    private static final int MAX_QUEUE_SIZE = 25;

    private List<String> expressionsQueue = new LinkedList<>();
    private final long id;
    private final SlaveTask slaveTask;
    private long processedCount = 0;
    private boolean running = true;

    public SlaveThread(long id) {
        this(id, new SlaveTask());
    }

    public SlaveThread(long id, SlaveTask slaveTask) {
        this.id = id;
        this.setName("SlaveThread_" + id);
        this.slaveTask = slaveTask;

        this.start();
    }

    @Override
    public void scheduleIntegration(String expression) {
        synchronized (expressionsQueue) {
            expressionsQueue.add(expression);
            SlaveTask.logMessage("Slave: " + getName() + ": queue length: " + expressionsQueue.size());
            expressionsQueue.notifyAll();
        }
    }

    @Override
    public void run() {
        while (running) {
            SlaveTask.logMessage("Slave: " + getName() + ": Waiting for data...");

            String expression = retrieveExpression();

            if (running) {
                try {
                    slaveTask.numericIntegration(expression, 1, 100);
                } catch (Exception e) {
                    SlaveTask.logMessage("Slave: " + getName() + ": Computations failure! " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private String retrieveExpression() {
        String expression = null;
        synchronized (expressionsQueue) {
            while (expressionsQueue.size() == 0 && running) {
                try {
                    expressionsQueue.wait(1000);
                } catch (InterruptedException e) {
                    // nothing happens
                }
            }

            if (running) {
                expression = expressionsQueue.remove(0);
            }
        }
        return expression;
    }

    public void stopExecution() {
        this.running = false;
    }

    public int getQueueLength() {
        return this.expressionsQueue.size();
    }

    public long getProcessedCount() {
        return processedCount;
    }

    @Override
    public boolean canTakeMore() {
        boolean canTakeMore = false;
        synchronized (expressionsQueue) {
            canTakeMore = expressionsQueue.size() < MAX_QUEUE_SIZE;
        }
        return canTakeMore;
    }

    @Override
    public String getSlaveId() {
        return getName();
    }

    public long getAvgProcessTime() {
        return slaveTask.getSumProcessingTime() / getProcessedCount();
    }

}