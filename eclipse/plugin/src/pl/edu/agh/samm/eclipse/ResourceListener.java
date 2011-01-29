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
package pl.edu.agh.samm.eclipse;

import java.rmi.RemoteException;
import java.util.Collection;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.IRMIResourceListener;
import pl.edu.agh.samm.common.core.IResourceEvent;
import pl.edu.agh.samm.eclipse.model.ResourcesList;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourceListener implements IRMIResourceListener {

	private static Logger logger = LoggerFactory.getLogger(ResourceListener.class);

	@Override
	public void processEvent(IResourceEvent event) throws RemoteException {
		final Collection<String> allResources = SAMM.getCoreManagement().getAllRegisteredResources();
		logger.info("Event: " + event.toString() + ", resources: " + allResources + ",attachment:"
				+ event.getAttachment());
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				ResourcesList.getInstance().setResourcesList(allResources);
				// viewer.setInput(allResources);
			}
		});
		// System.out.println("GOT EVENT!!!!!!!");
		// if(event.getType() == ResourceEventType.RESOURCES_CHANGED ) {
		// final HashMap<String, ArrayList<String>> resources = (HashMap<String,
		// ArrayList<String>>) event.getAttachment();
		// logger.warn("RESOURCES CHANGED!!!!"); //FIXME remove....
		// //logger.info(resources.entrySet().size());
		// for(Map.Entry<String, ArrayList<String>> res : resources.entrySet())
		// {
		// logger.info("key: " + res.getKey());
		// for(String val : res.getValue()) {
		// logger.info("       val: " + val);
		// }
		// }
		// System.out.println("VIEVER IS: " + viewer);
		// Display.getDefault().asyncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		// viewer.setInput(resources);
		// }
		//
		// });
		// }
		// if(event.getType() == ResourceEventType.RESOURCES_ADDED) {
		// Resource r = (Resource) event.getAttachment();
		// final Map<String, ArrayList<String>> resources = new HashMap<String,
		// ArrayList<String>>();
		// ArrayList<String> l = new ArrayList<String>();
		// l.add(r.getType());
		// resources.put(r.getUri(), l);
		// Display.getDefault().asyncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		// viewer.setInput(resources);
		// }
		//
		// });
		// }
	}

}
