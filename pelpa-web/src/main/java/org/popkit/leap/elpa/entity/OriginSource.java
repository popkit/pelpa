package org.popkit.leap.elpa.entity;

import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.elpa.utils.PelpaUtils;

/**
 * Author: Aborn Jiang
 * Email : aborn.jiang@gmail.com
 * Date  : 07-01-2016
 * Time  : 9:33 AM
 */
public class OriginSource {
    private String name;
    private String root;   // http://elpa.gnu.org/packages/

    public OriginSource(String name, String root) {
        this.name = name;
        this.root = root;
    }

    public String getRomoteArchiveContents() {
        return this.root + "archive-contents";
    }

    public String getLocalFilePath() {
        String root = PelpaUtils.getWorkingRootDir();
        if (StringUtils.isBlank(root)) {
            return "/Users/aborn/github/pelpa/working/" + this.name;
        } else {
            return root + "archive-contents-" + this.name;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
