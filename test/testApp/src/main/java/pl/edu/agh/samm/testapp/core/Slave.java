package pl.edu.agh.samm.testapp.core;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.integration.LegendreGaussIntegrator;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Slave extends LoggingClass implements SlaveMBean, Runnable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2694496615585780331L;
    private static final int MAX_QUEUE_SIZE = 25;
    private boolean running = true;
    private List<String> expressionsQueue = new LinkedList<String>();
    private String id;
    private static final Interpreter beanShellInterpreter = new Interpreter();
    private static LegendreGaussIntegrator integrator = new LegendreGaussIntegrator(5, 100);

    private long processedCount = 0;
    private long sumProcessingTime = 0;

    public double numericIntegration(final String formula, double min, double max) throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException {
        logMessage("Integrating: " + formula + "(" + min + "," + max + ")");
        UnivariateRealFunction function = new UnivariateRealFunction() {

            public double value(double x) throws FunctionEvaluationException {
                double retVal = -1.0;
                try {
                    beanShellInterpreter.set("x", x);
                    retVal = (Double) beanShellInterpreter.eval(formula);
                } catch (EvalError e) {
                    logMessage("ERROR", "Evaluation error!");
                    e.printStackTrace();
                }

                return retVal;
            }
        };

        long start = System.nanoTime();
        double retVal = integrator.integrate(function, min, max);
        long stop = System.nanoTime();
        long timeSpent = stop - start;

        logMessage("Integrated: " + formula + "(" + min + "," + max + "): "
                + retVal + " time spent: " + timeSpent);

        sumProcessingTime += timeSpent / 1000;

        processedCount++;
        return retVal;
    }

    public void scheduleIntegration(String expression) {
        synchronized (expressionsQueue) {
            expressionsQueue.add(expression);
            logMessage("Slave: " + id + ": queue length: "
                    + expressionsQueue.size());
            expressionsQueue.notifyAll();
        }
    }

    public void run() {
        while (running) {
            if (id == null) {
                Thread.currentThread().setName("Slave_NOID");
            } else {
                Thread.currentThread().setName("Slave_" + id);
            }
            logMessage("Slave: " + id + ": Waiting for data...");
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

            if (running) {
                try {
                    this.numericIntegration(expression, 1, 100);
                } catch (Exception e) {
                    logMessage("Slave: " + id + ": Computations failure! "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
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

    public long getSumProcessingTime() {
        return sumProcessingTime;
    }

    public long getAvgProcessTime() {
        return sumProcessingTime / processedCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        logMessage("setId: " + id);
        this.id = id;
    }

    public boolean canTakeMore() {
        boolean canTakeMore = false;
        synchronized (expressionsQueue) {
            canTakeMore = expressionsQueue.size() < MAX_QUEUE_SIZE;
        }
        return canTakeMore;
    }

}
