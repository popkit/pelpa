package org.popkit.leap.elpa.services;

import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.elpa.entity.DepsItem;
import org.popkit.leap.elpa.entity.PropsItem;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:22:47
 */
@Service
public class ArchiveContentsGenerator {

    public String generator() {
        String result = "";

        return result;
    }

    public static String wrapBracket(String orgin) {
        return "(" + orgin + ")";
    }

    public static String wrapQuote(String origin) {
        return "\"" + origin + "\"";
    }

    public static String buildProps(PropsItem propsItem) {
        return wrapBracket(wrapUrl(propsItem.getUrl())
        + " " + wrapKeywords(propsItem.getKeywords()));
    }

    public static String wrapUrl(String url) {
        return wrapBracket(
                "url: ." + wrapQuote(url)
        );
    }

    public static String wrapKeywords(List<String> keywords) {
        if (CollectionUtils.isEmpty(keywords)) {
            return wrapBracket(":keywords " + wrapQuote(""));
        }

        StringBuilder sb = new StringBuilder("");
        for (String keyword : keywords) {
            sb.append(" ").append(wrapQuote(keyword));
        }

        return wrapBracket(":keywords " + sb.toString());
    }

    public static String buildDeps(List<DepsItem> depsItemList) {
        if (CollectionUtils.isEmpty(depsItemList)) {
            return " nil ";
        }

        StringBuilder sb = new StringBuilder("");
        for (DepsItem depsItem : depsItemList) {
            sb.append(wrapDepsItem(depsItem)).append(" ");
        }

        return wrapBracket(sb.toString());
    }

    public static String wrapDepsItem(DepsItem depsItem) {
        return wrapBracket(
                depsItem.getName() + " " +
        wrapBracket(StringUtils.join(depsItem.getVersions(), " "))
        );
    }
}
