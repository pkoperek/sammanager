package pl.edu.agh.samm.testapp;

import pl.edu.agh.samm.testapp.core.ExpressionGenerator;
import pl.edu.agh.samm.testapp.core.SlaveDispatcher;
import pl.edu.agh.samm.testapp.core.SlaveManager;

import java.io.Serializable;

public class WorkloadGenerator implements Serializable {

    private static final int MAX_LVL = 10;

    private Thread expressionGeneratorThread;
    private ExpressionGenerator expressionGenerator;
    private SlaveManager slaveManager;
    private SlaveDispatcher slaveDispatcher;
    private Thread slaveDispatcherThread;

    public WorkloadGenerator() {
        expressionGenerator = new ExpressionGenerator(MAX_LVL);

        initExpressionGenerator();
        initSlaveManager();
        initSlaveDispatcher();
    }

    public void stopGenerating() throws InterruptedException {
        expressionGenerator.stopGeneration();
    }

    public void startGenerating() {
        expressionGenerator.startGeneration();
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

}