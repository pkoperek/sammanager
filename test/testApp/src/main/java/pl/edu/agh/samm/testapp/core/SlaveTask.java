package pl.edu.agh.samm.testapp.core;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.integration.LegendreGaussIntegrator;

public class SlaveTask extends LoggingClass {

    private static final LegendreGaussIntegrator integrator = new LegendreGaussIntegrator(5, 100);
    private static final Interpreter beanShellInterpreter = new Interpreter();

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

        logMessage("Integrated: " + formula + "(" + min + "," + max + "): " + retVal + " time spent: " + timeSpent);

        sumProcessingTime += timeSpent / 1000;

        return retVal;
    }

    public long getSumProcessingTime() {
        return sumProcessingTime;
    }


}
