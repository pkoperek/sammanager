package pl.edu.agh.samm.testapp;

import pl.edu.agh.samm.testapp.core.ExpressionGenerator;
import pl.edu.agh.samm.testapp.core.SlaveDispatcher;
import pl.edu.agh.samm.testapp.core.SlaveManager;

import java.io.Serializable;

public class WorkloadGenerator implements Serializable {

    private static final int MAX_LVL = 10;

    private static final WorkloadGenerator workloadGenerator = new WorkloadGenerator();

    private Thread expressionGeneratorThread;
    private ExpressionGenerator expressionGenerator;
    private SlaveManager slaveManager;
    private SlaveDispatcher slaveDispatcher;
    private Thread slaveDispatcherThread;

    private WorkloadGenerator() {
        expressionGenerator = new ExpressionGenerator(MAX_LVL);

        initExpressionGenerator();
        initSlaveManager();
        initSlaveDispatcher();
    }

    public void stopGenerating() throws InterruptedException {
        expressionGenerator.stopGeneration();
    }

    public void startGenerating(long expressionsPerMinute) {
        expressionGenerator.setWaitTime(computeWaitTime(expressionsPerMinute));
        expressionGenerator.startGeneration();
    }

    private long computeWaitTime(long expressionsPerMinute) {
        if (expressionsPerMinute < 0) {
            return -1;
        }
        return 60000 / expressionsPerMinute;
    }

    private void initExpressionGenerator() {
        expressionGeneratorThread = new Thread(expressionGenerator);
        expressionGeneratorThread.start();
    }

    private void initSlaveDispatcher() {
        slaveDispatcher = new SlaveDispatcher(expressionGenerator, slaveManager);
        slaveDispatcherThread = new Thread(slaveDispatcher);
        slaveDispatcherThread.start();
    }

    private void initSlaveManager() {
        slaveManager = new SlaveManager();
    }

    public static WorkloadGenerator getInstance() {
        return workloadGenerator;
    }

    public void addSlave() {
        slaveManager.addNewSlave();
    }

    public void removeSlave() throws Exception {
        slaveManager.removeSlave();
    }
}