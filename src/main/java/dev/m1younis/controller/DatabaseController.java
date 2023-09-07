
package dev.m1younis.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A class that handles interaction with the local SQLite database utilised for persistent message
 * storage.
 */
public class DatabaseController {
    public static final String DATABASE_PATH = "src/main/resources/pm2p.db";

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(String.format("jdbc:sqlite:%s", DATABASE_PATH));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void init() {
        // A method for initialising the local database upon first-time launch
        final Connection conn = DatabaseController.connect();
        try {
            // `Statement` object used to execute queries to define a table for storing messages
            final Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS `messages`");
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `messages` ("
                + "`uid` VARCHAR(64) PRIMARY KEY NOT NULL, "
                + "`created` INTEGER NOT NULL, "
                + "`sender` TEXT NOT NULL, "
                + "`contents` TEXT NOT NULL, "
                + "`recipient` TEXT, "
                + "`topic` TEXT, "
                + "`subject` TEXT)"
            );

            // Initial message object written to newly created `messages` table
            stmt.executeUpdate(
                "INSERT INTO `messages` (`uid`, `created`, `sender`, `topic`, `contents`) VALUES ("
                + "'5103d1029e1c05d5e0a72833fbe6ed727266f1f333a64c3af2243ae6c1918b65', "
                + "1672766344, "
                + "'@m1younis', "
                + "'#testing', "
                + "'This\nis\na\nmessage\nfor\ntesting!')"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Driver session disconnected last
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
