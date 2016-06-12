package org.popkit.leap.elpa.utils;

import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.RecipeDo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:12:02
 */
public class RecipeParser {
    private RecipeParser(){}

    public static void main(String[] args) {
        String origin = "(wiki;afsf";
        String origin2 = "(wiki\";a\"b;e\";a\"fsf";
        System.out.println("origin:" + origin);
        System.out.println("after:" + trimComments(origin));
        System.out.println("origin:" + origin2);
        System.out.println("after:" + trimComments(origin2));
    }

    public static String trimComments(String origin) {
        int index = findSeparatorPosition(origin);
        if (index > 0) {
            return origin.substring(0, index);
        }

        return origin;
    }

    public static int findSeparatorPosition(String origin) {
        int count = 0;
        int idx = -1;
        for (int i=0; i < origin.length(); i++) {
            char c = origin.charAt(i);
            if (c == '"' && count > 0) {
                count --;
                continue;
            }

            if (c== '"' && count == 0) {
                count ++;
                continue;
            }

            if (c== ';' && count == 0) {
                if (i > idx) {
                    idx = i;
                }
            }
        }
        return idx;
    }

    public static String readFileToStringWithoutComments(File file) {
        BufferedReader br = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(file));
            String sCurrentLine;
            boolean needContinue = true;
            while ((sCurrentLine = br.readLine()) != null && needContinue) {
                if (StringUtils.isNotBlank(sCurrentLine) && sCurrentLine.trim().startsWith(";")) {
                    continue;
                }

                if (sCurrentLine.contains(";")) {
                    stringBuilder.append(trimComments(sCurrentLine)).append(" ");
                } else {
                    stringBuilder.append(sCurrentLine).append(" ");
                }
            }
        } catch (IOException e) {
            LeapLogger.error("error", e);
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return stringBuilder.toString().trim();
    }

    public static RecipeDo parsePkgRecipe(File recipeFile) {
        if (recipeFile.exists() && recipeFile.isFile()) {
            try {
                //FileUtils.readFileToString(recipeFile, "UTF-8");
                String content = readFileToStringWithoutComments(recipeFile);
                return parse(content);
            } catch (Exception e) {

            }
        }
        return null;
    }

    public static RecipeDo parsePkgRecipe(String pkgName) {
        String recipe = PelpaUtils.getRecipeFilePath() + pkgName;
        File recipeFile = new File(recipe);
        return parsePkgRecipe(recipeFile);
    }

    public static RecipeDo parse(String origin) {
        String sub = null;
        try {
            sub = origin.substring(origin.indexOf('(') + 1, origin.lastIndexOf(')'));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(sub)) {
            return null;
        }

        String[] suArr = sub.split("\\s+");
        RecipeDo recipeDo = new RecipeDo();
        recipeDo.setPkgName(suArr[0].trim());

        String keyValueStringPairString = sub.substring(sub.indexOf(suArr[0]) + suArr[0].length()).trim();
        String[] keyValueStringPair =  keyValueStringPairString.split(":");
        for (String keyValueString : keyValueStringPair) {
            if (StringUtils.isBlank(keyValueString)) {
                continue;
            }

            String[] keyValuePair = keyValueString.split("\\s+");
            if (keyValuePair.length <= 1) {
                continue;
            }
            String key = keyValuePair[0].trim();
            String value = keyValueString.substring(keyValueString.indexOf(keyValuePair[0]) + keyValuePair[0].length());
            if ("repo".equals(key)) {
                recipeDo.update(key, PelpaUtils.unwrap(value));
            } else if ("files".endsWith(key)) {
                String fileString = extraFileListString(keyValueStringPairString);
                recipeDo.update(key, fileString);
            } else {
                recipeDo.update(key, value.trim());
            }
        }
        String url = extraUrl(keyValueStringPairString);
        if (url != null) {
            recipeDo.setUrl(url);
        }
        return recipeDo;
    }

    private static String extraUrl(String keyValueStringPairString) {
        if (!keyValueStringPairString.contains(":url")) {
            return null;
        }

        String key = ":url";
        int index = keyValueStringPairString.indexOf(key);
        String otherString = keyValueStringPairString.substring(index + key.length());
        int startIndex = -1;
        int endIndex;
        int i = -1;
        for (char c : otherString.toCharArray()) {
            i++;
            if (c == '"' && startIndex == -1) {
                startIndex = i;
                continue;
            }
            if (c == '"' && startIndex != -1) {
                endIndex = i;
                if (endIndex > startIndex) {
                    return otherString.substring(startIndex + 1, endIndex);
                } else {
                    return null;
                }
            }

        }
        return null;
    }


    private static String extraFileListString(String keyValueStringPairString) {
        int index = keyValueStringPairString.indexOf(":files");
        return index > 0 ?
                extraPairContent(keyValueStringPairString.substring(index)).replaceAll("\"", "")
                : StringUtils.EMPTY;
    }

    public static String extraPairContent(String origin) {
        boolean gotfirstLeft = false;
        int leftIndex = -1;
        int rightIndex = -1;
        int match = 0;
        for (int i=0; i<origin.length(); i++) {
            if (origin.charAt(i) == '(') {
                if (!gotfirstLeft) {
                    gotfirstLeft = true;
                    leftIndex = i;
                } else {
                    match ++;
                }
            } else if (origin.charAt(i) == ')') {
                if (match == 0) {
                    rightIndex = i;
                    break;
                } else {
                    match --;
                }
            }
        }

        if (leftIndex >= 0  && rightIndex >= 0 && rightIndex > leftIndex) {
            return origin.substring(leftIndex + 1, rightIndex);
        }

        return StringUtils.EMPTY;
    }

    public static int findAnotherBracket(int leftBracketPos, String content) {
        int flagInt = 0;
        for (int i=leftBracketPos + 1; i<content.length(); i++) {
            if ('(' == content.charAt(i)) {
                flagInt ++;
            }

            if (')' == content.charAt(i) && flagInt == 0) {
                return i;
            }

            if (')' == content.charAt(i) && flagInt > 0) {
                flagInt --;
            }
        }
        return -1;
    }
}
