package ita.micc.meteorcity.database.bindclass;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/** City class for database table (cities)
 * @author Codeh
 */
@Getter
@Setter
@RequiredArgsConstructor
public class City {

    private int ID;
    private @NonNull double PIL;
    private @NonNull String cityTemplate;
}
