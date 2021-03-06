package org.popkit.leap.elpa.services.handler;

import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.OriginSource;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.FetchHandler;
import org.popkit.leap.elpa.utils.FetchRemoteFileUtils;
import org.popkit.leap.elpa.utils.OriginSourceElpaUtils;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * Author: Aborn Jiang
 * Email : aborn.jiang@gmail.com
 * Date  : 07-01-2016
 * Time  : 12:25 PM
 */
@Service
public class OriginSourceFetchHandler implements FetchHandler {

    public boolean validate(RecipeDo recipeDo, Map<String, Object> extra) {
        return FetcherEnum.isOriginSource(recipeDo.getFetcher());
    }

    public void execute(RecipeDo recipeDo, Map<String, Object> extra) {
        OriginSource originSource = OriginSourceElpaUtils.getOriginSource(recipeDo.getFetcher());
        if (originSource == null) { return; }
        String workingPath = PelpaUtils.getWorkingPath(recipeDo.getPkgName());
        File finalPkgFile = PelpaUtils.getOriginSourceFinalPkgFile(recipeDo);
        if (!finalPkgFile.exists()) {
            FetchRemoteFileUtils.downloadRemoteFile(getRemoteUrl(recipeDo, originSource), workingPath + "/" + recipeDo.getPkgName() + "." + recipeDo.getFileSuffix());
        }
    }


    private String getRemoteUrl(RecipeDo recipeDo, OriginSource originSource) {
        String root = originSource.getRoot();
        String fileName = recipeDo.getPkgName() + "-" + recipeDo.getVersionRegexp() + "." + recipeDo.getFileSuffix();
        return root + fileName;
    }

}
