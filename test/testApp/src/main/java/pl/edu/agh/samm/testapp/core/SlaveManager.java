package pl.edu.agh.samm.testapp.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SlaveManager extends LoggingClass implements ISlaveManager, Serializable {

    private static final long serialVersionUID = 901969699794692223L;
    private Map<String, ISlave> idsToSlaves = new HashMap<>();
    private Map<ISlave, String> slavesToIds = new HashMap<>();
    private int slaveNum = 0;
    private long id = 0;

    @Override
    public void addNextSlave() {
        SlaveTask slaveTask = new SlaveTask();

        Thread slaveThread = new Thread(slaveTask);
        slaveThread.start();
    }

    @Override
    public void removeSlave() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public synchronized String registerSlave(ISlave slave) {
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
        ISlave slave = idsToSlaves.remove(id);
        slavesToIds.remove(slave);
    }

    public synchronized int getSlavesCount() {
        return idsToSlaves.size();
    }

    public synchronized ISlave getNextSlave() {
        ISlave retVal = null;

        if (idsToSlaves.size() > 0) {
            ArrayList<String> slavesIds = new ArrayList<>(idsToSlaves.keySet());
            if (slaveNum >= slavesIds.size()) {
                slaveNum = 0;
            }

            String id = slavesIds.get(slaveNum);
            retVal = idsToSlaves.get(id);
            slaveNum++;
        }

        return retVal;
    }

    public synchronized String getId(ISlave slave) {
        return slavesToIds.get(slave);
    }

    public void unregisterSlave(ISlave slave) {
        String id = slavesToIds.remove(slave);
        idsToSlaves.remove(id);
    }

}
