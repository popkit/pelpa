package org.popkit.leap.elpa.utils;

import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.OriginSource;
import org.popkit.leap.elpa.entity.RecipeDo;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aborn Jiang
 * Email : aborn.jiang@gmail.com
 * Date  : 07-01-2016
 * Time  : 9:24 AM
 */
public class OriginSourceElpaUtils {

    public static void main(String[] args) {
        List<RecipeDo> recipeDos = collectionRecipes();
        for (RecipeDo recipeDo : recipeDos) {
            System.out.println(recipeDo.toString());
        }
    }

    public static List<OriginSource> getSourceElpaList() {
        List<OriginSource> result = new ArrayList<OriginSource>();
        result.add(new OriginSource("gnu", "http://elpa.gnu.org/packages/"));
        result.add(new OriginSource("org", "http://orgmode.org/elpa/"));
        return result;
    }

    public static List<RecipeDo> collectionRecipes() {
        List<RecipeDo> recipeDos = new ArrayList<RecipeDo>();
        try {
            for (OriginSource originSource : getSourceElpaList()) {
                boolean status = FetchRemoteFileUtils.downloadFile(originSource.getRomoteArchiveContents(), originSource.getLocalFilePath());
                if (!status) { continue; }

            }
        } catch (Exception e) {
            LeapLogger.warn("error in collectionRecipes", e);
        }

        return recipeDos;
    }

}
