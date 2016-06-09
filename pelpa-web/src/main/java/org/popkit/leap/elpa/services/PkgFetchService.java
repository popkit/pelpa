package org.popkit.leap.elpa.services;

import org.apache.commons.collections.CollectionUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:09:52
 */
@Service
public class PkgFetchService {

    @Autowired
    private List<FetchHandler> fetchHandlerList;

    public boolean downloadPackage(String pkgName) {
        try {
            RecipeDo recipeDo = LocalCache.getRecipeDo(pkgName);
            return downloadPackage(recipeDo);
        } catch (Exception e) {
            LeapLogger.warn("downloadPackage@@@" + pkgName, e);
            return true;
        }
    }

    public boolean downloadPackage(RecipeDo recipeDo) {
        if (recipeDo == null) {
            return true;
        }

        String pkgPath = PelpaUtils.getWorkingPath(recipeDo.getPkgName());
        Map<String, Object> extra = new HashMap<String, Object>();
        extra.put("pkgPath", pkgPath);

        if (CollectionUtils.isNotEmpty(fetchHandlerList)) {
            for (FetchHandler fetchHandler : fetchHandlerList) {
                if (fetchHandler.validate(recipeDo, extra)) {
                    fetchHandler.execute(recipeDo, extra);
                }
            }
        }

        return true;
    }
}
