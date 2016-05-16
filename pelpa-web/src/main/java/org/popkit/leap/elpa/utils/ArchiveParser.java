package org.popkit.leap.elpa.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.popkit.leap.elpa.entity.ArchiveVo;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:14:39
 */
public class ArchiveParser {

    public static ArchiveVo parserFromJSONObject(JSONObject jsonObject) {
        if (jsonObject == null) {
            throw new NullPointerException();
        }

        ArchiveVo archiveVo = new ArchiveVo();
        if (jsonObject.containsKey("desc")) {
            archiveVo.setDesc(jsonObject.getString("desc"));
        }

        if (jsonObject.containsKey("props")) {
            JSONObject propsJson = jsonObject.getJSONObject("props");
            if (propsJson.containsKey("url")) {
                archiveVo.setPropsUrl(propsJson.getString("url"));
            }

            if (propsJson.containsKey("keywords")) {
                JSONArray jsonArray = jsonObject.getJSONArray("keywords");
                ListIterator<Object> iterator = jsonArray.listIterator();
                List<String> result = new ArrayList<String>();
                while (iterator.hasNext()) {
                    result.add(iterator.next().toString());
                }
                archiveVo.setKeywords(result);
            }
        }

        if (jsonObject.containsKey("ver")) {
            JSONArray jsonArray = jsonObject.getJSONArray("ver");
            ListIterator<Object> iterator = jsonArray.listIterator();
            List<Integer> result = new ArrayList<Integer>();
            while (iterator.hasNext()) {
                result.add(Integer.parseInt(iterator.next().toString()));
            }
            archiveVo.setVer(result);
        }

        if (jsonObject.containsKey("type")) {
            archiveVo.setType(jsonObject.getString("type"));
        }

        if (jsonObject.containsKey("deps")) {
            archiveVo.setDeps(jsonObject.getJSONObject("deps"));
        }

        return archiveVo;
    }
}
