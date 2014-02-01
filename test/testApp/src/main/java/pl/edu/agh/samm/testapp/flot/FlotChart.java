package pl.edu.agh.samm.testapp.flot;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractJavaScriptComponent;

/**
 * User: koperek
 * Date: 01.02.14
 * Time: 16:14
 */
@JavaScript({"https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js", "jquery.flot.js", "flot_connect.js"})
public class FlotChart extends AbstractJavaScriptComponent {

    public FlotChart(String chartCaption) {
        setCaption(chartCaption);
        setWidth(Double.toString(Page.getCurrent().getBrowserWindowWidth() * 0.95));
        setHeight("300");
    }

    public void addPoint(double x, double y) {
        getState().addPoint(x, y);
    }

    @Override
    protected FlotChartState getState() {
        return (FlotChartState) super.getState();
    }
}
