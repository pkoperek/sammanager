package pl.edu.agh.samm.testapp.core;

import javax.management.*;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class WorkloadGenerator implements Serializable {

    private static final int MAX_LVL = 10;

    private static WorkloadGenerator workloadGenerator;

    private List<WorkloadGeneratorListener> workloadGeneratorListeners = new ArrayList<>();

    static {
        try {
            workloadGenerator = new WorkloadGenerator();
        } catch (Exception e) {
            System.err.println("ERROR!");
            e.printStackTrace();
        }
    }

    private Thread expressionGeneratorThread;
    private ExpressionGenerator expressionGenerator;
    private SlaveManager slaveManager;
    private SlaveDispatcher slaveDispatcher;
    private Thread slaveDispatcherThread;

    private WorkloadGenerator() throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        expressionGenerator = new ExpressionGenerator(MAX_LVL);

        initExpressionGenerator();
        initSlaveManager();
        initSlaveDispatcher();

        registerMBeans();
    }

    private void registerMBeans() throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        mbeanServer.registerMBean(slaveManager, new ObjectName("pl.edu.agh.samm:name=TestApp"));
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

        fireSlavesCountChangedEvent(slaveManager.getSlavesCount());
    }

    public void removeSlave() throws Exception {
        slaveManager.removeSlave();

        fireSlavesCountChangedEvent(slaveManager.getSlavesCount());
    }

    private void fireSlavesCountChangedEvent(int slavesCount) {
        for (WorkloadGeneratorListener workloadGeneratorListener : workloadGeneratorListeners) {
            workloadGeneratorListener.handleSlavesCountChangedEvent(slavesCount);
        }
    }

    public void addWorkloadGeneratorListener(WorkloadGeneratorListener listener) {
        this.workloadGeneratorListeners.add(listener);
    }
}