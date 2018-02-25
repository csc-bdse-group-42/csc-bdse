package ru.csc.bdse.datasource;

import com.sleepycat.je.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.csc.bdse.ApplicationProperties;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class BerkleyDataSource {

    private Environment dbEnvironment = null;
    private Database database = null;

    @Autowired
    BerkleyDataSource(ApplicationProperties applicationProperties) {
        String dbFilename = applicationProperties.getDbfile();

        try{
            if (Files.notExists(Paths.get(dbFilename))) {
                Files.createDirectories(Paths.get(dbFilename));
            }
        } catch (IOException e) {
            throw new RuntimeException("Directory " + dbFilename + " not exists and cannot be created.");
        }

        if (!Files.isDirectory(Paths.get(dbFilename))) {
            throw new RuntimeException(dbFilename + " should be directory");
        }

        try {
            EnvironmentConfig environmentConfig = new EnvironmentConfig();
            environmentConfig.setAllowCreate(true);
            dbEnvironment = new Environment(new File(dbFilename), environmentConfig);
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            database = dbEnvironment.openDatabase(null, "TestDatabase", dbConfig);
        } catch (DatabaseException dbe) {
            System.out.println(dbe.toString());
        }
    }

    @PreDestroy
    private void destroy() {
        try {
            if (database != null) {
                database.close();
            }

            if (dbEnvironment != null) {
                dbEnvironment.close();
            }
        } catch (DatabaseException dbe) {
            System.out.println(dbe.toString());
        }

    }

    public Database getDatabase() {
        return database;
    }

    public Environment getDbEnvironment() {
        return dbEnvironment;
    }
}
