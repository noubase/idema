package com.noubase.core.crud.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rshuper on 24.07.15.
 */
public final class MongoUtil {

    @NotNull
    public static String extractDuplicatedField(@NotNull String message) {
        Assert.hasText(message, "Exception message cannot be empty.");

        String regex = Pattern.quote(".$") + "(.*?)" + Pattern.quote("dup");
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(message);
        if (m.find()) {
            String trim = m.group(1).trim();
            int last = trim.lastIndexOf("_");
            return last != -1 ? trim.substring(0, last) : trim;
        }
        return "";
    }
}
