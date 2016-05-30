package org.popkit.leap.elpa.services.handler;

import org.popkit.core.entity.SimpleResult;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.FetchHandler;
import org.popkit.leap.elpa.services.RecipesService;
import org.popkit.leap.elpa.utils.HGCommandUtils;
import org.popkit.leap.elpa.utils.PelpaUtils;
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
        try {
            SimpleResult simpleResult = HGCommandUtils.hgFetchRemote2Local(recipeDo);
            if (simpleResult.isSuccess()) {
                long lastModified = HGCommandUtils.getHGLastModified(PelpaUtils.getWorkingPath(recipeDo.getPkgName()));
                if (lastModified > 0) {
                    boolean status = RecipesService.updateLastCommit(recipeDo.getPkgName(), lastModified);
                    LeapLogger.info("update " + recipeDo.getPkgName() + " lastcommit, status=" + status);
                } else {
                    LeapLogger.warn("mercurialFetchHandler pkg(" + recipeDo.getPkgName() + ") failed!");
                }
            }
        } catch (Exception e) {
            LeapLogger.warn("mercurialFetchHandler pkg(" + recipeDo.getPkgName() + ") exception!");
        }

        LeapLogger.info("MercurialFetchHandler finished:" + recipeDo.getPkgName());
    }
}
