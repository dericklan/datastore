package org.apache.airavata.datastore.server;

import org.apache.airavata.datastore.server.common.ServerProperties;
import org.apache.airavata.datastore.server.dispatcher.Dispatcher;
import org.apache.airavata.datastore.server.monitor.AbstractMonitor;
import org.apache.airavata.datastore.server.monitor.FileSystemMonitor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.nio.file.Paths;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private AbstractMonitor monitor;
    private Dispatcher dispatcher;
    private ServerProperties props;

    public Server() throws Exception {
        props = ServerProperties.getInstance();
        dispatcher =  new Dispatcher();
        monitor = new FileSystemMonitor(Paths.get(props.getDataRoot()));
    }

    public void startService() throws Exception {
        //Loading Log4J property from External Source
        if (new File(ServerProperties.LOG4J_PROPERTY_FILE).exists()) {
            PropertyConfigurator.configure(ServerProperties.LOG4J_PROPERTY_FILE);
        }

        System.out.println("\nStarting DataStore Server...!\n");

        //Starting directory monitor
        monitor.startMonitor();

        //Starting directory update message dispatcher
        dispatcher.startDispatcher();
    }

    public void stopService(){
        monitor.stopMonitor();
        System.out.println("\nGood bye from DataStore Server...!\n");
    }

    public static void main(String[] args) throws Exception {
        final Server dataStoreServer = new Server();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("ShutDown called...");
                dataStoreServer.stopService();
            }
        });
        dataStoreServer.startService();
    }
}