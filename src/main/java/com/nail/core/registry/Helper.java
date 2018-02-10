package com.nail.core.registry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

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

    public static String parseNodeName(String path) {
        if (StringUtils.isEmpty(path)) {
            throw new RuntimeException("Path Must Not Be Null");
        }
        String[] paths = path.split("/");
        if (paths == null || paths.length <= 0) {
            throw new RuntimeException("Path Parse Error");
        }
        return paths[paths.length - 1];
    }

    public static Pair<String, String> parseService(String path) {
        if (StringUtils.isEmpty(path)) {
            throw new RuntimeException("Path Must Not Be Null");
        }
        String[] paths = path.split("/");
        if (paths == null || paths.length <= 1) {
            throw new RuntimeException("Path ParseError");
        }
        int len = paths.length;
        return Pair.of(paths[len - 2], paths[len - 1]);
    }

    public static void main(String[] args) {
        String path = "/a/b/c/d";
        System.out.println(parseNodeName(path));
        Pair<String, String> pair = parseService(path);
        System.out.println(pair.getLeft() + "," + pair.getRight());
    }
}
