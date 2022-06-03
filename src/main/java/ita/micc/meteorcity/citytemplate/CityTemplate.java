package ita.micc.meteorcity.citytemplate;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * CityTemplate class
 * @author Codeh.
 */
@Getter
@Setter
public class CityTemplate {

    private final String name;
    private final File schematicFile;
    private final String permissionRequired;

    private Pivot main;
    private Pivot[] wildZone;
    private Pivot[] townHall;

    private Pivot playerSpawn;

    public CityTemplate(String name, String schematicFileName, String schematicsFolderPath, String permissionRequired) throws FileNotFoundException {
        this.name = name;
        this.permissionRequired = permissionRequired;
        schematicFile = new File(schematicsFolderPath + File.separator + schematicFileName);
        wildZone = new Pivot[2];
        townHall = new Pivot[2];
        if(!schematicFile.exists()) {
            throw new FileNotFoundException();
        }
    }
}
