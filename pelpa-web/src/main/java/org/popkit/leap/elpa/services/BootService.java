package org.popkit.leap.elpa.services;

import org.popkit.leap.elpa.entity.PelpaContents;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

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
        File archiveFile = new File(htmlPath + PelpaContents.ARCHIVE_JSON_FILE_NAME);
        File recipesFile = new File(htmlPath + PelpaContents.RECIPES_JSON_FILE_NAME);
    }
}
