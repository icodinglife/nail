package com.nail.core.registry;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guofeng.qin on 2018/02/09.
 */
public class Helper {
    public static String joinPath(String... strs) {
        List<String> pathList = new ArrayList<>();
        for (String str : strs) {
            if (!StringUtils.isEmpty(str)) {
                pathList.add(str);
            }
        }

        return StringUtils.join(pathList, '/');
    }
}
