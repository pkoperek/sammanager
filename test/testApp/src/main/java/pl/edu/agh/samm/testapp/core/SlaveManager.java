package pl.edu.agh.samm.testapp.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SlaveManager extends LoggingClass implements Serializable {

    private static final long serialVersionUID = 901969699794692223L;
    private List<SlaveThread> slaves = new ArrayList<>();
    private int slaveNum = 0;
    private long id = 0;

    public SlaveManager() {
        addNewSlave();
    }

    public void addNewSlave() {
        synchronized (slaves) {
            slaves.add(new SlaveThread(++id));
        }
    }

    public void removeSlave() throws Exception {
        synchronized (slaves) {
            if (slaves.size() > 0) {
                SlaveThread slaveToRemove = slaves.remove(0);
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

    public ISlave getNextSlave() {
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
