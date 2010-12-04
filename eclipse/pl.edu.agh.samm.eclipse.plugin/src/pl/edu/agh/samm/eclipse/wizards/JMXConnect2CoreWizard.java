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
package pl.edu.agh.samm.eclipse.wizards;

import java.rmi.server.UnicastRemoteObject;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.ICoreManagement;
import pl.edu.agh.samm.common.core.IRMIActionExecutionListener;
import pl.edu.agh.samm.common.core.IRMIAlarmListener;
import pl.edu.agh.samm.common.core.IRMIResourceListener;
import pl.edu.agh.samm.common.knowledge.IKnowledge;
import pl.edu.agh.samm.common.metrics.IRMIMetricsManagerListener;
import pl.edu.agh.samm.eclipse.ActionExecutionListener;
import pl.edu.agh.samm.eclipse.AlarmListener;
import pl.edu.agh.samm.eclipse.MetricsManagerListener;
import pl.edu.agh.samm.eclipse.ResourceListener;
import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.perspectives.ResourcesPerspective;
import pl.edu.agh.samm.eclipse.wizards.pages.CoreJMXUrlWizardPage;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class JMXConnect2CoreWizard extends Wizard {

	private CoreJMXUrlWizardPage urlWp;
	private Logger logger = LoggerFactory.getLogger(JMXConnect2CoreWizard.class);

	public JMXConnect2CoreWizard() {
		urlWp = new CoreJMXUrlWizardPage();
	}

	@Override
	public void addPages() {
		addPage(urlWp);
	}

	@Override
	public boolean performFinish() {
		try {

			JMXServiceURL url = new JMXServiceURL(urlWp.getJmxUrl());
			JMXConnector jmxConnector = JMXConnectorFactory.connect(url);

			MBeanServerConnection serverConn = jmxConnector.getMBeanServerConnection();

			ObjectName semmonManagementObjectName = new ObjectName("pl.edu.agh.samm:type=Management");
			ICoreManagement coreManagement = JMX.newMBeanProxy(serverConn, semmonManagementObjectName,
					ICoreManagement.class);
			if (coreManagement == null) {
				throw new RuntimeException("Erron on accessing ICoreManagement through JMX");
			}

			ObjectName knowledgeObjectName = new ObjectName("pl.edu.agh.samm:type=Knowledge");
			IKnowledge knowledge = JMX.newMBeanProxy(serverConn, knowledgeObjectName, IKnowledge.class);
			if (knowledge == null) {
				throw new RuntimeException("IKnowledge not found in JMX");
			}

			PlatformUI.getWorkbench().showPerspective(ResourcesPerspective.ID,
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());

			IRMIResourceListener resourcesListener = new ResourceListener();
			resourcesListener = (IRMIResourceListener) UnicastRemoteObject.exportObject(resourcesListener, 0);

			IRMIAlarmListener alarmListener = new AlarmListener();
			alarmListener = (IRMIAlarmListener) UnicastRemoteObject.exportObject(alarmListener, 0);

			IRMIMetricsManagerListener metricsManagerListener = new MetricsManagerListener();
			metricsManagerListener = (IRMIMetricsManagerListener) UnicastRemoteObject.exportObject(
					metricsManagerListener, 0);

			IRMIActionExecutionListener actionExecutionListener = new ActionExecutionListener();
			actionExecutionListener = (IRMIActionExecutionListener) UnicastRemoteObject.exportObject(
					actionExecutionListener, 0);

			SAMM.connectToCore(coreManagement, knowledge, resourcesListener, alarmListener,
					metricsManagerListener, actionExecutionListener);

		} catch (Exception e) {
			SAMM.handleException(e);
		}
		return true;
	}

}
