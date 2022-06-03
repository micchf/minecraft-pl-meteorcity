package ita.micc.meteorcity.database.query;

import lombok.Getter;

import java.util.List;
import java.util.Vector;

/**
 * Query class for query database.
 * @author Codeh.
 * @version 1.0
 */
@Getter
public class QueryInfo {

    private final String query;
    private final List<Parameter> parameters;
    private final Object bindObject;

    /**
     * Constructor
     * @param query database query
     * @param object opt if want bind specific object.
     */
    public QueryInfo(String query, Object bindObject) {
        this.query = query;
        this.bindObject = bindObject;
        parameters = new Vector<>();
    }

    /**
     * add parameter into a query
     * @param paramName name
     * @param object value
     */
    public void addParameter(String paramName, Object object) {
        parameters.add(new Parameter(paramName, object));
    }
}
