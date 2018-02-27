package org.popkit.leap.monitor.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.elpa.entity.RoundRun;
import org.popkit.leap.elpa.entity.RoundStatus;
import org.popkit.leap.elpa.utils.PelpaUtils;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Author: aborn.jiang
 * Email : aborn.jiang@foxmail.com.com
 * Date  : 07-07-2016
 * Time  : 9:43 AM
 */
public class BadgeUtils {

    public static void main(String[] args) {
        int a = 1234;
        int b = 22234;
        int c = 232234;
        int d = 1232234;
        System.out.println(a + ":" + toCountString(a));
        System.out.println(b + ":" + toCountString(b));
        System.out.println(c + ":" + toCountString(c));
        System.out.println(d + ":" + toCountString(d));
    }

    public static void buildVersionBadge(String pkgName, String version) {
        if (StringUtils.isBlank(pkgName) || StringUtils.isBlank(version)) {
            return;
        }

        String packagePath = PelpaUtils.getHtmlPath() + "packages/";
        String badgeFileName = packagePath + pkgName + "-badge.svg";
        String badgeContent = getVersionBadge(version);
        File badgeFile = new File(badgeFileName);

        try {
            FileUtils.writeStringToFile(badgeFile, badgeContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getVersionBadge(String version) {
        if (version.length() < 6) {
            return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"112\" height=\"20\"><linearGradient " +
                    "id=\"b\" x2=\"0\" y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" " +
                    "stop-opacity=\".1\"/><stop offset=\"1\" stop-opacity=\".1\"/></linearGradient><mask " +
                    "id=\"a\"><rect width=\"112\" height=\"20\" rx=\"3\" fill=\"#fff\"/></mask><g " +
                    "mask=\"url(#a)\"><path fill=\"#555\" d=\"M0 0h73v20H0z\"/><path fill=\"#007ec6\" " +
                    "d=\"M73 0h39v20H73z\"/><path fill=\"url(#b)\" d=\"M0 0h112v20H0z\"/></g><g fill=\"#fff\" " +
                    "text-anchor=\"middle\" font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\"><text " +
                    "x=\"36.5\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">popkit-elpa</text><text x=\"36.5\" " +
                    "y=\"14\">popkit-elpa</text><text x=\"91.5\" y=\"15\" fill=\"#010101\"" +
                    " fill-opacity=\".3\">" + version + "</text><text x=\"91.5\" y=\"14\">" + version + "</text></g></svg>";

        } else {
            return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"170\" height=\"20\"><linearGradient " +
                    "id=\"b\" x2=\"0\" y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" " +
                    "stop-opacity=\".1\"/><stop offset=\"1\" stop-opacity=\".1\"/></linearGradient><mask" +
                    " id=\"a\"><rect width=\"170\" height=\"20\" rx=\"3\" fill=\"#fff\"/></mask><g " +
                    "mask=\"url(#a)\"><path fill=\"#555\" d=\"M0 0h73v20H0z\"/><path fill=\"#007ec6\" " +
                    "d=\"M73 0h97v20H73z\"/><path fill=\"url(#b)\" d=\"M0 0h170v20H0z\"/></g><g fill=\"#fff\" " +
                    "text-anchor=\"middle\" font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\"><text " +
                    "x=\"36.5\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">popkit-elpa</text><text x=\"36.5\" " +
                    "y=\"14\">popkit-elpa</text><text x=\"120.5\" y=\"15\" fill=\"#010101\" " +
                    "fill-opacity=\".3\">" + version + "</text><text x=\"120.5\" y=\"14\">" + version + "</text></g></svg>";
        }
    }

    public static String toCountString(int count) {
        return NumberFormat.getNumberInstance(Locale.US).format(count);
    }

    public static String getDownloadCount(int count) {
        String countString = toCountString(count);  // 12,345
        if (count < 100000) {
            return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"118\" height=\"20\"><linearGradient id=\"b\"" +
                    " x2=\"0\" y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/><stop offset=\"1\" " +
                    "stop-opacity=\".1\"/></linearGradient><mask id=\"a\"><rect width=\"118\" height=\"20\" rx=\"3\"" +
                    " fill=\"#fff\"/></mask><g mask=\"url(#a)\"><path fill=\"#555\" d=\"M0 0h69v20H0z\"/><path fill=\"#4c1\" " +
                    "d=\"M69 0h49v20H69z\"/><path fill=\"url(#b)\" d=\"M0 0h118v20H0z\"/></g><g fill=\"#fff\" text-anchor=\"middle\" " +
                    "font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\"><text x=\"34.5\" y=\"15\" fill=\"#010101\"" +
                    " fill-opacity=\".3\">downloads</text><text x=\"34.5\" y=\"14\">downloads</text><text x=\"92.5\" " +
                    "y=\"15\" fill=\"#010101\" fill-opacity=\".3\">" + countString + "</text><text x=\"92.5\"" +
                    " y=\"14\">" + countString + "</text></g></svg>";
        } else {
            return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"124\" height=\"20\"><linearGradient id=\"b\" " +
                    "x2=\"0\" y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/><stop offset=\"1\" " +
                    "stop-opacity=\".1\"/></linearGradient><mask id=\"a\"><rect width=\"124\" height=\"20\" rx=\"3\" " +
                    "fill=\"#fff\"/></mask><g mask=\"url(#a)\"><path fill=\"#555\" d=\"M0 0h69v20H0z\"/><path fill=\"#4c1\" " +
                    "d=\"M69 0h55v20H69z\"/><path fill=\"url(#b)\" d=\"M0 0h124v20H0z\"/></g><g fill=\"#fff\" text-anchor=\"middle\" " +
                    "font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\"><text x=\"34.5\" y=\"15\" fill=\"#010101\" " +
                    "fill-opacity=\".3\">downloads</text><text x=\"34.5\" y=\"14\">downloads</text><text x=\"95.5\" y=\"15\"" +
                    " fill=\"#010101\" fill-opacity=\".3\">" + countString + "</text><text x=\"95.5\"" +
                    " y=\"14\">" + countString + "</text></g></svg>";
        }
    }

    public static String getLastUpdateTime(Date date) {
        String time = "unknown"; //"2016.07.07 09:13";
        if (date != null) {
            time = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(date);
        }

        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"176\" height=\"20\"><linearGradient id=\"b\" x2=\"0\" " +
                "y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/><stop offset=\"1\" " +
                "stop-opacity=\".1\"/></linearGradient><mask id=\"a\"><rect width=\"176\" height=\"20\" rx=\"3\"" +
                " fill=\"#fff\"/></mask><g mask=\"url(#a)\"><path fill=\"#555\" d=\"M0 0h65v20H0z\"/><path fill=\"#007ec6\"" +
                " d=\"M65 0h111v20H65z\"/><path fill=\"url(#b)\" d=\"M0 0h176v20H0z\"/></g><g fill=\"#fff\" text-anchor=\"middle\" " +
                "font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\"><text x=\"32.5\" y=\"15\" fill=\"#010101\" " +
                "fill-opacity=\".3\">最近更新于</text><text x=\"32.5\" " +
                "y=\"14\">最近更新于</text><text x=\"119.5\" y=\"15\" fill=\"#010101\" " +
                "fill-opacity=\".3\">" + time + "</text><text x=\"119.5\" y=\"14\">" +
                time + "</text></g></svg>\n";
    }

    public static String getCurrentStatus(RoundRun roundRun) {
        RoundStatus roundStatus = roundRun.getStatus();
        if (roundStatus == RoundStatus.RUNNING) {
            return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"108\" height=\"20\"><linearGradient id=\"b\" x2=\"0\" y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/><stop offset=\"1\" stop-opacity=\".1\"/></linearGradient><mask id=\"a\"><rect width=\"108\" height=\"20\" rx=\"3\" fill=\"#fff\"/></mask><g mask=\"url(#a)\"><path fill=\"#555\" d=\"M0 0h55v20H0z\"/><path fill=\"#9f9f9f\" d=\"M55 0h53v20H55z\"/><path fill=\"url(#b)\" d=\"M0 0h108v20H0z\"/></g><g fill=\"#fff\" text-anchor=\"middle\" font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\"><text x=\"27.5\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">当前状态</text><text x=\"27.5\" y=\"14\">当前状态</text><text x=\"80.5\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">building</text><text x=\"80.5\" y=\"14\">building</text></g></svg>\n";
        } else {
            int time = roundRun.getRoundId();
            if (time < 10) {
                return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"126\" height=\"20\"><linearGradient id=\"b\" x2=\"0\" " +
                        "y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/><stop offset=\"1\" " +
                        "stop-opacity=\".1\"/></linearGradient><mask id=\"a\"><rect width=\"126\" height=\"20\" " +
                        "rx=\"3\" fill=\"#fff\"/></mask><g mask=\"url(#a)\"><path fill=\"#555\" d=\"M0 0h55v20H0z\"/><path fill=\"#4c1\" " +
                        "d=\"M55 0h71v20H55z\"/><path fill=\"url(#b)\" d=\"M0 0h126v20H0z\"/></g><g fill=\"#fff\" " +
                        "text-anchor=\"middle\" font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\"" +
                        " font-size=\"11\"><text x=\"27.5\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">当前状态</text><text x=\"27.5\"" +
                        " y=\"14\">当前状态</text><text x=\"89.5\" y=\"15\" fill=\"#010101\"" +
                        " fill-opacity=\".3\">finished@" + time + "</text><text x=\"89.5\" " +
                        "y=\"14\">finished@" + time + "</text></g></svg>";
            } else if (time < 100) {
                return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"132\" height=\"20\"><linearGradient " +
                        "id=\"b\" x2=\"0\" y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/><stop " +
                        "offset=\"1\" stop-opacity=\".1\"/></linearGradient><mask id=\"a\"><rect width=\"132\" " +
                        "height=\"20\" rx=\"3\" fill=\"#fff\"/></mask><g mask=\"url(#a)\"><path fill=\"#555\" " +
                        "d=\"M0 0h55v20H0z\"/><path fill=\"#4c1\" d=\"M55 0h77v20H55z\"/><path fill=\"url(#b)\" " +
                        "d=\"M0 0h132v20H0z\"/></g><g fill=\"#fff\" text-anchor=\"middle\" " +
                        "font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\"><text " +
                        "x=\"27.5\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">当前状态</text><text x=\"27.5\" " +
                        "y=\"14\">当前状态</text><text x=\"92.5\" y=\"15\" fill=\"#010101\" " +
                        "fill-opacity=\".3\">finished@" + time + "</text><text x=\"92.5\" " +
                        "y=\"14\">finished@" + time + "</text></g></svg>\n";
            } else if (time < 1000) {
                return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"140\" height=\"20\"><linearGradient " +
                        "id=\"b\" x2=\"0\" y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/><stop " +
                        "offset=\"1\" stop-opacity=\".1\"/></linearGradient><mask id=\"a\"><rect width=\"140\" " +
                        "height=\"20\" rx=\"3\" fill=\"#fff\"/></mask><g mask=\"url(#a)\"><path fill=\"#555\" " +
                        "d=\"M0 0h55v20H0z\"/><path fill=\"#4c1\" d=\"M55 0h85v20H55z\"/><path fill=\"url(#b)\" " +
                        "d=\"M0 0h140v20H0z\"/></g><g fill=\"#fff\" text-anchor=\"middle\" font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" " +
                        "font-size=\"11\"><text x=\"27.5\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">当前状态</text><text x=\"27.5\" " +
                        "y=\"14\">当前状态</text><text x=\"96.5\" y=\"15\" fill=\"#010101\" " +
                        "fill-opacity=\".3\">finished@" + time + "</text><text x=\"96.5\" " +
                        "y=\"14\">finished@" + time + "</text></g></svg>\n";
            } else {
                return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"146\" height=\"20\"><linearGradient id=\"b\" " +
                        "x2=\"0\" y2=\"100%\"><stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/><stop offset=\"1\" " +
                        "stop-opacity=\".1\"/></linearGradient><mask id=\"a\"><rect width=\"146\" height=\"20\" rx=\"3\" " +
                        "fill=\"#fff\"/></mask><g mask=\"url(#a)\"><path fill=\"#555\" d=\"M0 0h55v20H0z\"/><path fill=\"#4c1\" " +
                        "d=\"M55 0h91v20H55z\"/><path fill=\"url(#b)\" d=\"M0 0h146v20H0z\"/></g><g fill=\"#fff\" text-anchor=\"middle\" " +
                        "font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\"><text x=\"27.5\" y=\"15\" fill=\"#010101\" " +
                        "fill-opacity=\".3\">当前状态</text><text x=\"27.5\" y=\"14\">当前状态</text><text x=\"99.5\" y=\"15\" fill=\"#010101\" " +
                        "fill-opacity=\".3\">finished@" + time + "</text><text x=\"99.5\" " +
                        "y=\"14\">finished@" + time + "</text></g></svg>";
            }
        }
    }
}
