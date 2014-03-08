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

package pl.edu.agh.samm.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.edu.agh.samm.api.metrics.IMetric;

/**
 * Provides utilities for processing strings
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class StringHelper {
	/**
	 * Parses arrays serialized to <code>String</code>. <br>
	 * {@link Arrays#toString(Object[])} creates a <code>String</code> in the
	 * following format:
	 * 
	 * <pre>
	 * toStringValue ::= '[' values ']'
	 * values        ::= value { &quot;, &quot; values } | &lt;empty&gt;
	 * value         ::= String
	 * </pre>
	 * 
	 * This method parses String and returns a {@link List} of interpreted
	 * String values.
	 * 
	 * @param toStringValue
	 *            value of {@link Arrays#toString(Object[]))}
	 * @return
	 */
	public static List<String> parseCollectionString(String toStringValue) {
		List<String> list = new ArrayList<String>();

		if (toStringValue != null && toStringValue.length() > 2) {
			// remove leading and trailing '[' and ']'
			toStringValue = toStringValue.substring(1, toStringValue.length() - 1);
			String[] elements = toStringValue.split(", ");
			list = Arrays.asList(elements);
		}

		return list;
	}

	/**
	 * Returns LocalName from the given <code>URIString</code>. <br>
	 * URI is constructed in the following form:
	 * 
	 * <pre>
	 * URIString  ::= NameSpace '#' LocalName
	 * NameSpace  ::= String
	 * LocalName  ::= String
	 * </pre>
	 * 
	 * This method extracts <tt>LocalName</tt>.
	 * 
	 * @param URIString
	 * @return
	 */
	public static String getNameFromURI(String URIString) {
		String name = "";
		if (URIString != null) {
			name = URIString.substring(URIString.indexOf('#') + 1);
		}
		return name;
	}

	public static String getMetricName(IMetric metric) {
		String name = getNameFromURI(metric.getMetricURI()) + " " + metric.getResourceURI();
		return name;
	}

	public static String getParentURI(String uri) {
		int indexOfSlash = uri.lastIndexOf("/");
		if (indexOfSlash == -1) {
			return null;
		}
        return uri.substring(0, indexOfSlash);
	}
}
