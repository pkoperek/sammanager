package pl.edu.agh.samm.testapp.core;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;

public interface ISlave extends Stoppable {

    void scheduleIntegration(String expression);

    boolean canTakeMore();

    String getSlaveId();
}
