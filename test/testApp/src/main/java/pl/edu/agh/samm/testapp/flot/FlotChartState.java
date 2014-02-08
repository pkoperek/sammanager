package pl.edu.agh.samm.testapp.flot;

import com.vaadin.shared.ui.JavaScriptComponentState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: koperek
 * Date: 01.02.14
 * Time: 16:17
 */
public class FlotChartState extends JavaScriptComponentState {

    private List<List<Double>> values = new ArrayList<>();
    private String color = "#0000FF";

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<List<Double>> getValues() {
        return values;
    }

    public void setValues(List<List<Double>> values) {
        this.values = values;
    }

    public void addPoint(double x, double y) {
        values.add(Arrays.asList(x, y));
    }
}
