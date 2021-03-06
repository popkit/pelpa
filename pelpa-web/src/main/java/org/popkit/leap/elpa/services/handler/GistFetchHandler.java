package org.popkit.leap.elpa.services.handler;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.config.LeapConfigLoader;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.FetchHandler;
import org.popkit.leap.elpa.services.HttpProxyService;
import org.popkit.leap.elpa.services.LocalCache;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.gist.FetchJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 *
 * http://gist.popkit.org/tango-2-theme/tango-2-theme.el
 * https://appkit.popkit.org/gist/get.json?pkgName=tango-2-theme&url=https://gist.github.com/2024464.git
 *
 * cannot download gist file in china
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-19:23:26
 */
@Service
public class GistFetchHandler implements FetchHandler {

    @Autowired
    private HttpProxyService httpProxyService;

    private String getJSONUrl(String pkgName, String url) {
        String jsonUrl = LeapConfigLoader.get("elpa_gist_json_server");
        return jsonUrl + "gist/get.json?pkgName=" + pkgName +
                "&url=" + url;
    }

    private String getGistFileUrl(String pkgFile) {
        String gistServer = LeapConfigLoader.get("elpa_gist_server");
        return gistServer + pkgFile;
    }

    private FetchJSON getFetchFile(String pkgName, String url) {
        String jsonUrl = getJSONUrl(pkgName, url);
        try {
            LeapLogger.info("#httpProxyService.getJSON#" + jsonUrl);
            String result = httpProxyService.getJSON(jsonUrl);
            LeapLogger.info("#httpProxyService.getJSON.result#" + result);
            if (StringUtils.isNotBlank(result)) {
                return JSON.parseObject(result, FetchJSON.class);
            }
        } catch (Exception e) {
            LeapLogger.warn("error in getFetchFile for pkgName=" + pkgName + e.getMessage(), e);
        }
        return null;
    }

    public boolean validate(RecipeDo recipeDo, Map<String, Object> extra) {

        if (recipeDo.getFetcherEnum() == FetcherEnum.GIT
                && recipeDo.getUrl().contains("https://gist.github.com")) {   // gist.github repo
            return true;
        }

        return false;
    }

    public void execute(RecipeDo recipeDo, Map<String, Object> extra) {
        FetchJSON fetchFile = getFetchFile(recipeDo.getPkgName(), recipeDo.getUrl());
        if (fetchFile != null && fetchFile.isSuccess()) {
            String gistFileUrl = getGistFileUrl(fetchFile.getPkgFile());
            String workingPath = PelpaUtils.getWorkingPath(recipeDo.getPkgName()) + gistFileUrl.substring(gistFileUrl.lastIndexOf("/"));
            boolean status = LocalCache.updateLastCommit(recipeDo.getPkgName(), fetchFile.getLastCommit());
            System.out.println("update " + recipeDo.getPkgName() + " lastcommit, status=" + status);
            boolean downloadStatus = httpProxyService.downloadGistFile(gistFileUrl, workingPath);
            if (!downloadStatus) {
                LeapLogger.info("error fetch " + recipeDo.getPkgName());
            }
            System.out.println("download " + recipeDo.getPkgName() + " status=" + downloadStatus);
        } else {
            LeapLogger.info("error fetch " + recipeDo.getPkgName() + ", error in fetchFIleJSON");
        }
    }
}
