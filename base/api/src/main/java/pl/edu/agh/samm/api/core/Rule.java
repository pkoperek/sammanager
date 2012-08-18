/**
 * This file is part of SAMM.
 *
 * SAMM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SAMM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SAMM.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.agh.samm.api.core;

import pl.edu.agh.samm.api.action.Action;

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
