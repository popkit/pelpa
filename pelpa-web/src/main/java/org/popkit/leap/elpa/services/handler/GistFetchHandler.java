package org.popkit.leap.elpa.services.handler;

import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.FetchHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * cannot download gist file in china
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-19:23:26
 */
@Service
public class GistFetchHandler implements FetchHandler {

    public boolean validate(RecipeDo recipeDo, Map<String, Object> extra) {

        if (recipeDo.getFetcherEnum() == FetcherEnum.GIT
                && recipeDo.getUrl().contains("gist")) {
            return true;
        }

        return false;
    }

    public void execute(RecipeDo recipeDo, Map<String, Object> extra) {

    }
}
