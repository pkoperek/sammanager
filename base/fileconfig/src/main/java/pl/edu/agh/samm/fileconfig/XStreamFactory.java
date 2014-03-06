package pl.edu.agh.samm.fileconfig;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import pl.edu.agh.samm.api.action.Action;
import pl.edu.agh.samm.api.core.Rule;
import pl.edu.agh.samm.api.metrics.Metric;

public class XStreamFactory {

    public XStream createXStream() {
        XStream xstream = new XStream(new DomDriver());

        // resources
        xstream.alias("resourceSet", ConfigurationResourceSet.class);
        xstream.addImplicitCollection(ConfigurationResourceSet.class, "resources");
        xstream.alias("resource", ConfigurationResource.class);
        xstream.alias("property", ConfigurationResourceProperty.class);
        xstream.useAttributeFor(ConfigurationResource.class, "uri");
        xstream.addImplicitCollection(ConfigurationResource.class, "properties");

        // rules
        xstream.alias("ruleSet", RuleSet.class);
        xstream.addImplicitCollection(RuleSet.class, "rules");
        xstream.alias("rule", Rule.class);
        xstream.useAttributeFor(Rule.class, "name");

        // action
        xstream.alias("action", Action.class);

        // configuration
        xstream.alias("configuration", Configuration.class);
        //xstream.useAttributeFor(Configuration.class, "gracePeriod");

        // metrics
        xstream.alias("metric", Metric.class);
        xstream.useAttributeFor(Metric.class, "metricURI");
        xstream.useAttributeFor(Metric.class, "resourceURI");
        xstream.useAttributeFor(Metric.class, "metricPollTimeInterval");
        xstream.alias("metricSet", ConfigurationMetricSet.class);
        xstream.addImplicitCollection(ConfigurationMetricSet.class, "metrics");

        return xstream;
    }
}