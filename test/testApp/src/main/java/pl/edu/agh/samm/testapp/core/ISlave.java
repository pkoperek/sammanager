package pl.edu.agh.samm.testapp.core;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;

public interface ISlave extends Stoppable {

    double numericIntegration(final String formula, double min, double max) throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException;

    void scheduleIntegration(String expression);

    String getId();

    void setId(String string);

    boolean canTakeMore();
}
