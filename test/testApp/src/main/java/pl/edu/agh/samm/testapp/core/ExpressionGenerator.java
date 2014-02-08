package pl.edu.agh.samm.testapp.core;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import pl.edu.agh.samm.testapp.core.record.ExpressionsSet;
import pl.edu.agh.samm.testapp.core.record.RecordedSession;

public class ExpressionGenerator extends LoggingClass implements Runnable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5045355327465779200L;
    private long waitTime = 1000;
    private boolean running = true;
    private ExpressionGeneratorEngine generator = new ExpressionGeneratorEngine();
    private List<String> expressionQueue = new LinkedList<>();
    private List<Long> timestamps = new LinkedList<>();
    private boolean generating = false;
    private int maxLvl;
    private XStream xstream = null;
    private String expressionFilePath = null;
    private RecordedSession recordedSession = null;
    private long sumWaitTime = 0;
    private long numHandled = 0;

    public static void main(String[] args) {
        logMessage("Generating a session...");

        String outputFile = args[0];
        long numberOfSets = Long.parseLong(args[1]);
        String sleepAfterDefaultString = args[2];
        long sleepAfterDefault = Long.parseLong(sleepAfterDefaultString);

        long minExprCnt = Long.parseLong(args[3]);
        long maxExprCnt = Long.parseLong(args[4]);

        int maxLvl = Integer.parseInt(args[5]);

        // print out the params
        logMessage("> Parameters:");
        logMessage(">> Output file: " + outputFile);
        logMessage(">> Number of sets: " + numberOfSets);
        logMessage(">> sleepAfterDefault:" + sleepAfterDefault);
        logMessage(">> Min number of expressions in set:" + minExprCnt);
        logMessage(">> Max number of expressions in set:" + maxExprCnt);
        logMessage(">> Max level:" + maxLvl);

        ExpressionGeneratorEngine engine = new ExpressionGeneratorEngine();
        RecordedSession recordedSession = new RecordedSession();

        long exprCnt = minExprCnt;
        for (long i = 0; i < numberOfSets; i++) {
            logMessage("Generating set: " + i + " with: " + exprCnt
                    + " expressions and sleep time: " + sleepAfterDefault);
            ExpressionsSet set = new ExpressionsSet();
            set.setSleepAfterInterval(sleepAfterDefault);
            for (long j = 0; j < exprCnt; j++) {
                String expr = engine.generateExpression(maxLvl);
                set.addExpression(expr);
            }

            recordedSession.addExpressionSet(set);

            exprCnt += 5;

            if (exprCnt > maxExprCnt) {
                exprCnt = minExprCnt;
            }
        }

        logMessage(">>> Storing to file <<<");

        XStream xstream = createConfiguredInstance();
        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFile);
            xstream.toXML(recordedSession, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        logMessage(">>> Stored in " + outputFile + " <<<");
    }

    public static XStream createConfiguredInstance() {
        XStream xstream = new XStream(new DomDriver());

        xstream.alias("recordedsession", RecordedSession.class);
        xstream.alias("expressionsset", ExpressionsSet.class);
        xstream.useAttributeFor(ExpressionsSet.class, "sleepAfterInterval");
        xstream.addImplicitCollection(ExpressionsSet.class, "expressions");

        return xstream;
    }

    public ExpressionGenerator(int maxLvl) {
        this(maxLvl, null);
    }

    public ExpressionGenerator(int maxLvl, String exprFilePath) {
        this.maxLvl = maxLvl;

        // xstream config
        xstream = createConfiguredInstance();
        this.expressionFilePath = exprFilePath;

        if (exprFilePath != null) {
            logMessage("Loading recorded session from " + exprFilePath);
            readRecordedSession();
            logMessage("Recorded session loaded");
        }
    }

    private void readRecordedSession() {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(expressionFilePath);
            long start = System.currentTimeMillis();
            recordedSession = (RecordedSession) xstream.fromXML(fileReader);
            long stop = System.currentTimeMillis();
            logMessage("Reading took: " + ((stop - start)) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    // nothing happens
                    e.printStackTrace();
                }
            }

        }
    }

    /*
     * (non-Javadoc)
     *
     * @see IExpressionGenerator#getNewExpression()
     */
    public String getNewExpression() {
        synchronized (expressionQueue) {
            while (expressionQueue.size() == 0 && running) {
                try {
                    expressionQueue.wait(1000);
                } catch (InterruptedException e) {
                    // nothing happens - just wait
                }
            }
            if (running) {
                String expression = expressionQueue.remove(0);
                long end = System.currentTimeMillis();
                long start = timestamps.remove(0);

                long waitTime = end - start;
                sumWaitTime += waitTime;
                numHandled++;

                return expression;
            } else {
                return null;
            }
        }
    }

    public void run() {
        Thread.currentThread().setName("ExpressionGenerator");
        long iteration = 0L;
        logMessage("ExpressionGenerator started!");
        if (recordedSession == null) {
            // running until user stops us
            while (running) {
                logMessage("Iteration: " + iteration++ + " generating: " + generating + " maxLvl: " + maxLvl);

                if (generating) {
                    String expression = generator.generateExpression(maxLvl);
                    synchronized (expressionQueue) {
                        expressionQueue.add(expression);
                        timestamps.add(System.currentTimeMillis());
                        logMessage("Queue length: " + expressionQueue.size());
                        expressionQueue.notifyAll();
                    }
                }

                sleep();
            }
        } else {
            while (!generating && running) {
                try {
                    logMessage("Waiting for start signal...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (running) {
                logMessage("Playing recorded session");

                List<ExpressionsSet> sets = recordedSession
                        .getExpressionsSets();

                for (int i = 0; i < sets.size(); i++) {
                    ExpressionsSet set = sets.get(i);
                    logMessage("Set: " + set.getExpressions().size()
                            + " expressions; waiting for: "
                            + set.getSleepAfterInterval());
                    synchronized (expressionQueue) {
                        expressionQueue.addAll(set.getExpressions());
                        long time = System.currentTimeMillis();
                        for (int j = 0; j < set.getExpressions().size(); j++) {
                            timestamps.add(time);
                        }
                    }

                    try {
                        Thread.sleep(set.getSleepAfterInterval());
                    } catch (InterruptedException e) {
                        logMessage("!PROBLEM!",
                                "Played session not the same as recorded! Somebody woke up the thread!");
                    }
                }
            }
        }
    }

    private void sleep() {
        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                // nothing happens...
            }
        }
    }

    public void stopExecution() {
        running = false;
    }

    public int getQueueLength() {
        return expressionQueue.size();
    }

    public void setExpressionGenerationWaitTime(long waitTime) {
        this.waitTime = waitTime;
        logMessage("Changed expression generation wait time to: " + waitTime);
    }

    public void startGeneration() {
        logMessage("Start generating!");
        this.generating = true;
    }

    public void stopGeneration() {
        logMessage("Stop generating!");
        this.generating = false;
    }

    public String getName() {
        return "MasterNodeApp#1";
    }

    public void giveBack(String expression) {
        synchronized (expressionQueue) {
            expressionQueue.add(expression);
            timestamps.add(System.currentTimeMillis());
        }
    }

    public void setMaxLvl(int maxLvl) {
        logMessage("Setting maxLvl to " + maxLvl);
        this.maxLvl = maxLvl;
    }

    public long getSumWaitTime() {
        return sumWaitTime;
    }

    public long getServedCount() {
        return numHandled;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
}
