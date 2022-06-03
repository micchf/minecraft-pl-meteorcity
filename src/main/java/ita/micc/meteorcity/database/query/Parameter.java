package ita.micc.meteorcity.database.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Query's parameter
 * @author Codeh.
 */
@AllArgsConstructor
@Getter
public class Parameter {

    private String paramName;
    private Object object;
}
