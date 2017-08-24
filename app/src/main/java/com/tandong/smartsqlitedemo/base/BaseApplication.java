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
package com.tandong.smartsqlitedemo.base;

import android.app.Application;

import com.tandong.smartsqlite.utils.SmartConfig;
import com.tandong.smartsqlite.utils.SmartSQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局配置文件及初始化
 *
 * @author Tandong
 * @date 2017-8-20
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        SmartConfig.DB_NAME = "smartsqlite.db";//必填
        SmartConfig.DB_VERSION = 1;//选填，推荐写
        SmartConfig.ENTITY_PACKAGE = "entity";//默认为entity
        List<String> classNameList = new ArrayList<>();//数据库表集合，也就是实体类名称集合
        classNameList.add("Student");
        classNameList.add("Teacher");
        SmartConfig.classes = classNameList;//赋值
        SmartSQLite.initSmartSQLite(this);//自动初始化，全自动升级数据库，检测创建表，增加字段，删除表等
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SmartSQLite.getInstance(this).closeDB();
    }
}
