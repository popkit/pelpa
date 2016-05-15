package org.popkit.leap.elpa.services;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ArchiveVo;
import org.popkit.leap.elpa.entity.PelpaContents;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.springframework.stereotype.Service;

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


    private static void init() {
        String htmlPath = PelpaUtils.getHtmlPath();

        if (StringUtils.isBlank(htmlPath)) {
            htmlPath = "/Users/aborn/github/popkit-elpa/html/";
        }
        File archiveFile = new File(htmlPath + PelpaContents.ARCHIVE_JSON_FILE_NAME);
        //File recipesFile = new File(htmlPath + PelpaContents.RECIPES_JSON_FILE_NAME);

        try {
            String archiveJSON = FileUtils.readFileToString(archiveFile, "UTF-8");
            Map<String, ArchiveVo> archiveVoMap = convert2archivemap(JSONObject.parseObject(archiveJSON));
            if (null != archiveVoMap) {
                //
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
                LeapLogger.warn("parse archive exception, pkg:" + pkg, e);
            }
        }
        return map;
    }

    public static void main(String[] args) {
        init();
    }
}
