package org.popkit.leap.elpa.services;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.popkit.leap.elpa.entity.ArchiveVo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:15:45
 */
public class LocalCache {
    private static final ConcurrentHashMap<String, ArchiveVo> archive = new ConcurrentHashMap<String, ArchiveVo>();

    public static void update(String pkgName, ArchiveVo archiveVo) {
        archive.put(pkgName, archiveVo);
    }

    public static String getArchiveJSON() {
        JSONObject jsonObject = new JSONObject();
        if (MapUtils.isNotEmpty(archive)) {
            for (String pkgName : archive.keySet()) {
                jsonObject.put(pkgName, archive.get(pkgName));
            }
        }
        return jsonObject.toJSONString();
    }
}
