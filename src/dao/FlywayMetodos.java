/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import org.flywaydb.core.Flyway;

/**
 *
 * @author Arnaldo_Cantero
 */
public class FlywayMetodos {
    private String localizacionSQLs = "resources/db/migration";

    public void iniciarFlyway(String url, String user, String pass){
        Flyway flyway = org.flywaydb.core.Flyway
                .configure()
                .dataSource(url, user, pass) //url, user, pass de la base
                .locations(localizacionSQLs)
                .baselineOnMigrate(true) //Para que genere la tabla flyway history, el siguiente script debe ser V2
                .load();
        
        flyway.migrate();
        
    }
    
}
