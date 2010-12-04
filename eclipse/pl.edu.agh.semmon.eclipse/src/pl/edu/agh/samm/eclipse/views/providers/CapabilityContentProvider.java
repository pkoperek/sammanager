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
package pl.edu.agh.samm.eclipse.views.providers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.model.ResourceCapability;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class CapabilityContentProvider implements IStructuredContentProvider {

	private List<ResourceCapability> capabilities = new LinkedList<ResourceCapability>();

	public CapabilityContentProvider() {
	}

	@Override
	public Object[] getElements(Object source) {
		return capabilities.toArray();
	}

	@Override
	public void dispose() {
		// nothing happens
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object newInput) {
		String resourceUri = (String) newInput;
		capabilities.clear();
		if (SAMM.getCoreManagement() == null) {
			return;
		}
		String resourceType = SAMM.getCoreManagement().getResourceType(resourceUri);
		if (resourceType == null) {
			return;
		}
		for (String capability : SAMM.getKnowledge().getCapabilitiesOfResourceType(resourceType)) {
			ResourceCapability cap = new ResourceCapability(capability);
			capabilities.add(cap);
		}
	}

}
