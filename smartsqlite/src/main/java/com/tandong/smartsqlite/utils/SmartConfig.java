/*
 * Copyright (C) 2017 whatjay.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.tandong.smartsqlite.utils;

import java.util.List;

/**
 * 配置文件
 *
 * @author Tandong
 * @date 2017-8-20
 */

public class SmartConfig {
    public static List<String> classes = null;//数据库表集合
    public static String sqlStructure = "";
    public static String DB_NAME = "";//你数据库文件名，必填
    public static String ENTITY_PACKAGE = "entity";//你的实体类所在包的文件夹名，默认entity文件夹，根据项目实际位置和名称修改设置
    public static int DB_VERSION = 1;//可设置，可不设置，因为SmartSQLite支持全自动升级、删除表、新增字段（不支持修改字段，删除字段，因为SQLite暂不支持，如想修改或删除字段，请重新删除创建表）
    public static boolean LOG = false;
}
