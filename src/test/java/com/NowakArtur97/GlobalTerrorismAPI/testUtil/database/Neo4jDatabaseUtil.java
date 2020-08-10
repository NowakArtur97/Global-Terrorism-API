package com.NowakArtur97.GlobalTerrorismAPI.testUtil.database;

import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Neo4jDatabaseUtil {

    private final Driver driver;

    public void cleanDatabase() {

        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        }
    }
}
