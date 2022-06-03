package ita.micc.meteorcity.database.interfaces;

import org.sql2o.Sql2o;

import java.sql.SQLException;

/**
 * Database interface
 * @author Codeh
 */
public interface DatabaseInterface {

    /**
     * Method for create a new connection.
     * @throws SQLException if the connection cannot be created.
     */
    void createConnection() throws SQLException;

    /**
     * Checks if a connection is created.
     * @return true if the connection was created.
     */
    boolean checkConnection();

    /**
     * Gets the connection with the database.
     * @return Connection with the database, null if none
     */
    Sql2o getConnection();

    /**
     * Close connection with the database.
     * @throws SQLException if the connection cannot be closed
     */
    void closeConnection() throws SQLException;
}
