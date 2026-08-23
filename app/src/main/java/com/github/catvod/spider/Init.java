/*
 * @Author: catvod
 * @Date: 2025-03-10 12:25:31
 * @LastEditTime: 2025-03-11 02:41:27
 * @LastEditors: bgcode
 * @Description: 描述
 * @FilePath: /bgcode/app/src/main/java/com/github/catvod/spider/Init.java
 * 本项目采用GPL 许可证，欢迎任何人使用、修改和分发。
 */
package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.SpiderDebug;

public class Init {
    public static void init(Context context) {
        SpiderDebug.log("自定义爬虫代码加载成功！");
    }
}
