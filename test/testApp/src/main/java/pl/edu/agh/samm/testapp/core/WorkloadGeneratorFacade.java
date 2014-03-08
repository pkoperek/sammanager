package pl.edu.agh.samm.testapp.core;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkloadGeneratorFacade implements Serializable, WorkloadGeneratorFacadeMBean {

    public static final String HOSTNAME_PROPERTY = "pl.edu.agh.samm.testapp.hostname";
    public static final String JMX_PORT_PROPERTY = "pl.edu.agh.samm.testapp.jmxport";

    private static final int DEFAULT_JMX_PORT = 33333;
    private static final int MAX_LVL = 10;
    public static final String DEFAULT_HOSTNAME = "localhost";

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

    private WorkloadGeneratorFacade() throws Exception {
        initExpressionGenerator();
        initSlaveManager();
        initSlaveDispatcher();

        registerMBean();
    }

    private void registerMBean() throws Exception {
        startMBeanServer(getHostname(), getJmxPort());
    }

    private void startMBeanServer(String hostname, int jmxPort) throws Exception {
        System.out.println("Starting MBean server: " + hostname + ":" + jmxPort);
        LocateRegistry.createRegistry(jmxPort);

        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        mbeanServer.registerMBean(this, new ObjectName("pl.edu.agh.samm.testapp:name=WorkloadGenerator"));

        JMXServiceURL url = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", hostname, jmxPort));
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, Collections.<String, Object>emptyMap(), mbeanServer);
        cs.start();
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

    private static int getJmxPort() {
        try {
            return Integer.parseInt(System.getProperty(JMX_PORT_PROPERTY, Integer.toString(DEFAULT_JMX_PORT)));
        } catch (Exception e) {
            System.out.println("Can't determine value of port basing on user input! Using default jmx port: " + DEFAULT_JMX_PORT);
        }
        return DEFAULT_JMX_PORT;
    }

    private static String getHostname() {
        return System.getProperty(HOSTNAME_PROPERTY, DEFAULT_HOSTNAME);
    }
}