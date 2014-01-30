package pl.edu.agh.samm.testapp.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlaveManager extends LoggingClass implements ISlaveManager, Serializable {

    private static final long serialVersionUID = 901969699794692223L;
    private List<SlaveThread> slaves = new ArrayList<>();
    private int slaveNum = 0;
    private long id = 0;

    public SlaveManager() {
        addNextSlave();
    }

    @Override
    public void addNextSlave() {
        synchronized (slaves) {
            slaves.add(new SlaveThread(++id));
        }
    }

    @Override
    public void removeSlave() throws Exception {
        synchronized (slaves) {
            if (slaves.size() > 0) {
                SlaveThread slaveToRemove = slaves.get(0);
                slaveToRemove.stopExecution();
                slaveToRemove.join();
            }
        }
    }

    public int getSlavesCount() {
        synchronized (slaves) {
            return slaves.size();
        }
    }

    public synchronized ISlave getNextSlave() {
        ISlave retVal = null;

        synchronized (slaves) {
            if (slaves.size() > 0) {
                if (slaveNum >= slaves.size()) {
                    slaveNum = 0;
                }

                retVal = slaves.get(slaveNum);
                slaveNum++;
            }
        }

        return retVal;
    }

}
