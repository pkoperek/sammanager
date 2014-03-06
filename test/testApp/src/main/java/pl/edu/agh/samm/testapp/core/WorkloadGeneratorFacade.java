package pl.edu.agh.samm.testapp.core;

import javax.management.*;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class WorkloadGeneratorFacade implements Serializable, WorkloadGeneratorFacadeMBean {

    private static final int MAX_LVL = 10;

    private static WorkloadGeneratorFacade workloadGeneratorFacade;

    private List<WorkloadGeneratorListener> workloadGeneratorListeners = new ArrayList<>();
    private ExpressionGenerator expressionGenerator;
    private Thread expressionGeneratorThread;
    private SlaveDispatcher slaveDispatcher;
    private Thread slaveDispatcherThread;
    private SlaveManager slaveManager;

    static {
        try {
            workloadGeneratorFacade = new WorkloadGeneratorFacade();
        } catch (Exception e) {
            System.err.println("ERROR!");
            e.printStackTrace();
        }
    }

    private WorkloadGeneratorFacade() throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        initExpressionGenerator();
        initSlaveManager();
        initSlaveDispatcher();

        registerMBean();
    }

    private void registerMBean() throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        System.out.println("Starting MBean server");
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        mbeanServer.registerMBean(this, new ObjectName("pl.edu.agh.samm.testapp:name=WorkloadGenerator"));
    }

    @Override
    public void stopGenerating() throws InterruptedException {
        expressionGenerator.stopGeneration();
    }

    @Override
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
        expressionGenerator = new ExpressionGenerator(MAX_LVL);
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

    public static WorkloadGeneratorFacade getInstance() {
        return workloadGeneratorFacade;
    }

    @Override
    public void addSlave() {
        slaveManager.addNewSlave();

        fireSlavesCountChangedEvent(slaveManager.getSlavesCount());
    }

    @Override
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

    public SlaveManager getSlaveManager() {
        return slaveManager;
    }

    @Override
    public long getProcessedExpressionsCount() {
        return this.expressionGenerator.getServedCount();
    }

    @Override
    public long getExpressionsQueueLength() {
        return this.expressionGenerator.getQueueLength();
    }

    @Override
    public int getSlavesCount() {
        return this.slaveManager.getSlavesCount();
    }

    @Override
    public String getId() {
        return "WG1";
    }
}