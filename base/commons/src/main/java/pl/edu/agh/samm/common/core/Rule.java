package pl.edu.agh.samm.common.core;

import pl.edu.agh.samm.common.action.Action;

/**
 * Rule which defines what user expects SAMM to do when particular condition is
 * met
 *
 * @author koperek
 */
public class Rule {
    private String name = null;
    private String resourceTypeUri = null;
    private String resourceUri = null;
    private String metricUri = null;
    private String condition = null;
    private Action actionToExecute = null;
    private String customStatement = null;

    // required by xstream
    public Rule() {
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rule) {
            Rule o = (Rule) obj;
            return o.getName().equals(this.getName());
        }
        return false;
    }

    public Rule(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Rule name can't be null!");
        }
        this.name = name;
    }

    public String getResourceTypeUri() {
        return resourceTypeUri;
    }

    public void setResourceTypeUri(String resourceTypeUri) {
        this.resourceTypeUri = resourceTypeUri;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getMetricUri() {
        return metricUri;
    }

    public void setMetricUri(String metricUri) {
        this.metricUri = metricUri;
    }

    public String getCondition() {
        return condition;
    }

    public String getCustomStatement() {
        return customStatement;
    }

    public void setCustomStatement(String customStatement) {
        this.customStatement = customStatement;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Action getActionToExecute() {
        return actionToExecute;
    }

    public void setActionToExecute(Action actionToExecute) {
        this.actionToExecute = actionToExecute;
    }

    public String getName() {
        return name;
    }
}
