package pl.edu.agh.samm.testapp.core;

/**
 * User: koperek
 * Date: 08.02.14
 * Time: 15:15
 */
public interface WorkloadGeneratorFacadeMBean {
    String getId();

    void stopGenerating() throws InterruptedException;

    void startGenerating(long expressionsPerMinute);

    void addSlave();

    void removeSlave() throws Exception;

    long getProcessedExpressionsCount();

    long getExpressionsQueueLength();

    int getSlavesCount();
}
