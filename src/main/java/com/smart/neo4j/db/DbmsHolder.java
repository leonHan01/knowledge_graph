package com.smart.neo4j.db;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import java.io.File;
import java.time.Duration;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

public class DbmsHolder {
    private static final File databaseDirectory = new File("target/neo4j-hello-db");

    private static DatabaseManagementService managementService;

    private static GraphDatabaseService graphDb;

    public static DatabaseManagementService getDatabaseManagementService() {
        if (managementService == null) {
            initNeo4j();
        }

        return managementService;

    }

    public static void initNeo4j() {
        managementService = new DatabaseManagementServiceBuilder(databaseDirectory)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.transaction_timeout, Duration.ofSeconds(60))
                .setConfig(GraphDatabaseSettings.preallocate_logical_logs, true)
                .setConfig(BoltConnector.enabled, true)
                .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", 7687))
                .build();

        graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        registerShutdownHook(managementService);

        cleanDB();
    }

    private static void cleanDB() {
        try (Transaction tx = DbmsHolder.getGraphDb().beginTx()) {
            Result reuslt = tx.execute("MATCH (n) DETACH DELETE n");
            tx.commit();
        }
    }

    public static GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    private static void registerShutdownHook(DatabaseManagementService managementService) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> managementService.shutdown()));
    }
}
