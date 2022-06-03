package ita.micc.meteorcity.citytemplate.utils;

import ita.micc.meteorcity.citytemplate.Pivot;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Utils class for calculate template.
 * @author Codeh.
 */
public class CityTemplateUtils {

    /**
     * Static method utils for calculate single point (X,Y,Z)
     * @param cityTemplateSection template section.
     * @param template section's arg.
     * @return new pivot calculated.
     */
    public static Pivot calculateSinglePoint(ConfigurationSection cityTemplateSection, String type) {
        return new Pivot(cityTemplateSection.getInt(type + ".X"), cityTemplateSection.getInt(type + ".Y"),
                cityTemplateSection.getInt(type + ".Z"));
    }

    /**
     * Static method utils for calculate multiple point (minX,minY,minZ,maxX,maxY,maxZ)
     * @param cityTemplateSection template section.
     * @param template section's arg.
     * @return new pivot calculated.
     */
    public static Pivot[] calculateMultiplePoint(ConfigurationSection cityTemplateSection, String type) {
        Pivot[] pivot = new Pivot[2];

        pivot[0] = new Pivot(cityTemplateSection.getInt(type + ".minX"), cityTemplateSection.getInt(type + ".minY"),
                cityTemplateSection.getInt(type + ".minZ"));
        pivot[1] = new Pivot(cityTemplateSection.getInt(type + ".maxX"), cityTemplateSection.getInt(type + ".maxY"),
                cityTemplateSection.getInt(type + ".maxZ"));

        return pivot;
    }
}
