package ita.micc.meteorcity.database.config;

import ita.micc.meteorcity.database.interfaces.DatabaseInterface;
import org.sql2o.Sql2o;

import java.sql.SQLException;

/**
 * MySQL class.
 * @author Codeh.
 */
public class MySQL implements DatabaseInterface {

    private final String hostname;
    private final int port;
    private final String username;
    private final String password;
    private final String database;
    private Sql2o connection;

    /**
     * @param hostname mysql hostname
     * @param port mysql port
     * @param username mysql username
     * @param password mysql password
     * @param database mysql database schema name
     */
    public MySQL(String hostname, int port, String username, String password, String database) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @Override
    public void createConnection() throws SQLException {
        /* if connection already exist, return */
        if (checkConnection()) {
            return;
        }

        connection = new Sql2o("jdbc:mysql://" + this.hostname + ':' + this.port + '/' + this.database, this.username, this.password);
        connection.getConnectionSource().getConnection().setAutoCommit(false);
    }

    @Override
    public boolean checkConnection() {
        /* if connection is null, return false */
        if (connection == null) {
            return false;
        }
        try (java.sql.Connection conn = connection.getConnectionSource().getConnection()) {
            return !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Sql2o getConnection() {
        return this.connection;
    }

    @Override
    public void closeConnection() throws SQLException {
        connection.getConnectionSource().getConnection().close();
    }
}
