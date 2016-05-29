package org.popkit.leap.elpa.services.handler;

import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.FetchHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-30:07:18
 */
@Service
public class MercurialFetchHandler implements FetchHandler {


    public boolean validate(RecipeDo recipeDo, Map<String, Object> extra) {
        if (recipeDo.getFetcherEnum() == FetcherEnum.BITBUCKET) {
            return true;
        }

        return false;
    }

    public void execute(RecipeDo recipeDo, Map<String, Object> extra) {

    }
}
