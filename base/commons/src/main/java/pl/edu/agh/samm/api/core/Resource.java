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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.edu.agh.samm.api.tadapter.ITransportAdapter;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class Resource {

	private static final long serialVersionUID = -3891412413578714709L;

	public static final String TRANSPORTS_KEY = "_TRANSPORTS_KEY_";

	private String type;
	private String uri;
	private Map<String, Object> properties;

	public Resource(String uri, String type, Map<String, Object> properties) {
		this.type = type;
		this.uri = uri;
		this.properties = properties;
	}

	public boolean hasProperty(String key) {
		return properties.containsKey(key);
	}

	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	public void addTransportAdapter(ITransportAdapter adapter) {
		List<ITransportAdapter> adapters = null;
		if (properties.containsKey(TRANSPORTS_KEY)) {
			adapters = (List<ITransportAdapter>) properties.get(TRANSPORTS_KEY);
		} else {
			adapters = new LinkedList<ITransportAdapter>();
		}
		if (!adapters.contains(adapter)) {
			adapters.add(adapter);
		}
		properties.put(TRANSPORTS_KEY, adapters);
	}

	public List<ITransportAdapter> getTransportAdapters() {
		List<ITransportAdapter> retVal = null;
		if (properties.containsKey(TRANSPORTS_KEY)) {
			retVal = (List<ITransportAdapter>) properties.get(TRANSPORTS_KEY);
		} else {
			retVal = new LinkedList<ITransportAdapter>();
			properties.put(TRANSPORTS_KEY, retVal);
		}
		return retVal;
	}

	public Object getProperty(String key) {
		return properties.get(key);
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public String getUri() {
		return uri;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return uri + " " + type;
	}

}
