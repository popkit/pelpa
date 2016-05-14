package org.popkit.leap.elpa.services.handler;

import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.FetchHandler;

import java.io.File;
import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:10:09
 */
public class GithubFetchHandler implements FetchHandler {

    public boolean validate(RecipeDo recipeDo, Map<String, Object> extra) {
        if (recipeDo.getFetcherEnum() == FetcherEnum.GITHUB) {
            return true;
        }

        return false;
    }

    public void execute(RecipeDo recipeDo, Map<String, Object> extra) {
        File pkgPath = (File) extra.get("pkgPath");
    }
}
