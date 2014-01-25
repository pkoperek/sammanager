package pl.edu.agh.samm.testapp.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SlaveResolver extends LoggingClass implements SlaveResolverMBean,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 901969699794692223L;
	private Map<String, IRemoteSlave> idsToSlaves = new HashMap<String, IRemoteSlave>();
	private Map<IRemoteSlave, String> slavesToIds = new HashMap<IRemoteSlave, String>();
	private long id = 0;
	private int slaveNum = 0;

	public synchronized IRemoteSlave getSlaveProxy(String id) {
		return idsToSlaves.get(id);
	}

	public synchronized String registerSlave(IRemoteSlave slave) {
		String id = generateId();
		idsToSlaves.put(id, slave);
		slavesToIds.put(slave, id);
		return id;
	}

	private synchronized String generateId() {
		id++;
		return "ID" + id;
	}

	public synchronized void unregisterSlave(String id) {
		IRemoteSlave slave = idsToSlaves.remove(id);
		slavesToIds.remove(slave);
	}

	public synchronized int getSlavesCount() {
		return idsToSlaves.size();
	}

	public synchronized IRemoteSlave getNextSlave() {
		IRemoteSlave retVal = null;
		if (idsToSlaves.size() > 0) {
			ArrayList<String> slavesIds = new ArrayList<String>(
					idsToSlaves.keySet());
			if (slaveNum >= slavesIds.size()) {
				slaveNum = 0;
			}

			String id = slavesIds.get(slaveNum);
			retVal = idsToSlaves.get(id);
			slaveNum++;
		}

		return retVal;
	}

	public synchronized String getId(IRemoteSlave slave) {
		return slavesToIds.get(slave);
	}

	public void unregisterSlave(IRemoteSlave slave) {
		String id = slavesToIds.remove(slave);
		idsToSlaves.remove(id);
	}

	public boolean hasNextSlave() {
		// TODO Auto-generated method stub
		return false;
	}
}
