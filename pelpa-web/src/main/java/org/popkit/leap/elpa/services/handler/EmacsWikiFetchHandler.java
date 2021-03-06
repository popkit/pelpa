package org.popkit.leap.elpa.services.handler;

import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.FetchHandler;
import org.popkit.leap.elpa.services.HttpProxyService;
import org.popkit.leap.elpa.services.LocalCache;
import org.popkit.leap.elpa.utils.FetchRemoteFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-23:21:29
 */
@Service
public class EmacsWikiFetchHandler implements FetchHandler {
    private static final String WIKI_ROOT = "https://www.emacswiki.org/emacs/download/";

    @Autowired
    private HttpProxyService httpProxyService;

    public boolean validate(RecipeDo recipeDo, Map<String, Object> extra) {
        return recipeDo.getFetcherEnum() == FetcherEnum.WIKI;
    }

    public void execute(RecipeDo recipeDo, Map<String, Object> extra) {
        String pkgName = recipeDo.getPkgName();

        LeapLogger.info("EmacsWikiFetchHandler start:" + recipeDo.getPkgName());
        String remoteUrl = FetchRemoteFileUtils.getRemoteWikiUrl(pkgName);
        long lastModified = FetchRemoteFileUtils.getLastModified(remoteUrl);

        if (lastModified > 0) {
            if (FetchRemoteFileUtils.downloadWikiFile(pkgName)) {
                boolean status = LocalCache.updateLastCommit(recipeDo.getPkgName(), lastModified);
                LeapLogger.info("update " + recipeDo.getPkgName() + " lastcommit, status=" + status);
            } else {
                LeapLogger.warn("downloadWikiFile(" + remoteUrl + ") failed!");
            }
        } else {
            LeapLogger.warn("getLastModified(" + remoteUrl + ") failed!");
        }
        LeapLogger.info("EmacsWikiFetchHandler finished:" + recipeDo.getPkgName());
    }
}
