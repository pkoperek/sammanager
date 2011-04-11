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

package pl.edu.agh.samm.common.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Manages resource instances
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public interface IResourceInstancesManager {

	Collection<String> getAllRegisteredResources();

	String getResourceType(String uri);

	boolean isResourceRegistered(String uri);

	void removeResource(String uri);

	void addResourceListener(IResourceListener resourceListener);

	void removeResourceListener(IResourceListener resourceListener);

	List<String> getResourceCapabilities(String uri)
			throws ResourceNotRegisteredException;

	void addResource(Resource resource)
			throws ResourceAlreadyRegisteredException;

	void addChildResource(String parentUri, Resource childResource)
			throws ResourceNotRegisteredException;

	Resource getResourceForURI(String uri);

	List<String> getResourcesOfType(String type);

	void addResourceParameters(String uri, Map<String, Object> parameters);
}
