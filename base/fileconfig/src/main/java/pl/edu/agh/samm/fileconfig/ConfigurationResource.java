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
package pl.edu.agh.samm.fileconfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.edu.agh.samm.api.core.Resource;

/**
 * @author koperek
 */
public class ConfigurationResource {
    private String uri;
    private String type;
    private List<ConfigurationResourceProperty> properties = new LinkedList<ConfigurationResourceProperty>();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addProperty(ConfigurationResourceProperty property) {
        properties.add(property);
    }

    public List<ConfigurationResourceProperty> getProperties() {
        return properties;
    }

    public Resource getResource() {
        Map<String, Object> resourceProperties = new HashMap<String, Object>();
        for (ConfigurationResourceProperty property : properties) {
            resourceProperties.put(property.getKey(), property.getValue());
        }
        return new Resource(uri, type, resourceProperties);
    }

    @Override
    public String toString() {
        return "ConfigurationResource: URI: " + uri + " type: " + type;
    }
}
