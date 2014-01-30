package pl.edu.agh.samm.testapp.core;

public interface ISlaveManager {

    void addNextSlave();

    void removeSlave() throws Exception;

    int getSlavesCount();

	ISlave getNextSlave();
}
