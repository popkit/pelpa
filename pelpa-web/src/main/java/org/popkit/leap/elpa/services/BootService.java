package org.popkit.leap.elpa.services;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ArchiveVo;
import org.popkit.leap.elpa.entity.PelpaContents;
import org.popkit.leap.elpa.utils.ArchiveParser;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 启动时的service,初始化LocalCache里的recipes和archive
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:20:51
 */
@Service
public class BootService {

    @PostConstruct
    private void init() {

        String htmlPath = PelpaUtils.getHtmlPath();
        if (StringUtils.isBlank(htmlPath)) {
            htmlPath = "/Users/aborn/github/popkit-elpa/html/";
        }
        File archiveFile = new File(htmlPath + PelpaContents.ARCHIVE_JSON_FILE_NAME);
        //File recipesFile = new File(htmlPath + PelpaContents.RECIPES_JSON_FILE_NAME);

        try {
            String archiveJSON = FileUtils.readFileToString(archiveFile, "UTF-8");
            Map<String, ArchiveVo> archiveVoMap = convert2archivemap(JSONObject.parseObject(archiveJSON));
            int archiveNumber = 0;
            if (MapUtils.isNotEmpty(archiveVoMap)) {
                for (String pkgName : archiveVoMap.keySet()) {
                    LocalCache.updateArchive(pkgName, archiveVoMap.get(pkgName));
                    archiveNumber ++;
                }
                LeapLogger.info("LocalCache archive list update success! init archiveNumber:" + archiveNumber);
            }
        } catch (Exception e) {
            LeapLogger.warn("error in readFileToString", e);
        }
    }

    private static Map<String, ArchiveVo> convert2archivemap(JSONObject jsonObject) {
        Map<String, ArchiveVo> map = new HashMap<String, ArchiveVo>();
        for (String pkg : jsonObject.keySet()) {
            try {
                ArchiveVo archiveVo = JSONObject.parseObject(jsonObject.get(pkg).toString(), ArchiveVo.class);
                map.put(pkg, archiveVo);
            } catch (Exception e) {
                try {
                    ArchiveVo archiveVo = ArchiveParser.parserFromJSONObject((JSONObject) jsonObject.get(pkg));
                    map.put(pkg, archiveVo);
                } catch (Exception e2) {
                    LeapLogger.warn("error in boot init" + pkg, e2);
                }
            }
        }
        return map;
    }

    public static void main(String[] args) {
    }
}
