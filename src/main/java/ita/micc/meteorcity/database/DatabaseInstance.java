package ita.micc.meteorcity.database;

import ita.micc.meteorcity.database.config.MySQL;
import ita.micc.meteorcity.database.query.QueryInfo;
import ita.micc.meteorcity.database.interfaces.QueryInterface;
import lombok.Cleanup;
import lombok.Getter;
import org.sql2o.Connection;
import org.sql2o.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Database Instance
 * @author Codeh.
 */
public class DatabaseInstance implements QueryInterface {

    private @Getter final MySQL mySQL;

    /**
     * @param mySQL MySQL class
     * @throws SQLException MySQL class throws SQLException
     */
    public DatabaseInstance(MySQL mySQL) throws SQLException {
        this.mySQL = mySQL;
        mySQL.createConnection();
    }

    @Override
    public <A> List<A> fetchClassData(Class<A> returnData, QueryInfo queryInfo) {
        try {
            @Cleanup Connection connection = mySQL.getConnection().open();
            @Cleanup Query conn = connection.createQuery(queryInfo.getQuery());
            queryInfo.getParameters().forEach((param) -> conn.addParameter(param.getParamName(), param.getObject()));
            return conn.executeAndFetch(returnData);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insertObjectDataInt(QueryInfo queryInfo) {
        @Cleanup Connection connection = mySQL.getConnection().beginTransaction();
        try {
            @Cleanup Query conn = connection.createQuery(queryInfo.getQuery());
            queryInfo.getParameters().forEach((param) -> conn.addParameter(param.getParamName(), param.getObject()));
            conn.bind(queryInfo.getBindObject()).executeUpdate().commit();
        } catch (Throwable e) {
            connection.rollback();
            e.printStackTrace();
        }
        return connection.getKey(int.class);
    }

    @Override
    public boolean executeQuery(QueryInfo... queryInfo) {
        @Cleanup Connection connection = mySQL.getConnection().beginTransaction();

        for (QueryInfo query : queryInfo) {
            try {
                @Cleanup Query conn = connection.createQuery(query.getQuery());
                query.getParameters().forEach((param) -> conn.addParameter(param.getParamName(), param.getObject()));
                conn.bind(query.getBindObject()).executeUpdate();
            } catch (Throwable e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            }
        }
        connection.commit();
        return true;
    }

    @Override
    public boolean existRow(String query) throws SQLException {
        @Cleanup PreparedStatement ps =
                mySQL.getConnection().getConnectionSource().getConnection().prepareStatement(query);
        @Cleanup ResultSet rs = ps.executeQuery();
        return rs.next();
    }
}
