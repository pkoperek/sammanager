package pl.edu.agh.samm.testapp.core;

public interface ISlave extends Stoppable {

    void scheduleIntegration(String expression);

    boolean canTakeMore();

    String getSlaveId();
}
