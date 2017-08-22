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
package com.tandong.smartsqlite.base;

import android.content.Context;

import com.tandong.smartsqlite.utils.SmartLog;
import com.tandong.smartsqlite.utils.SmartSQLite;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据操作方案一基础父类
 *
 * @author Tandong
 * @date 2017-8-20
 */
public class TableObject <T>{
    private ArrayList<DataEntity> dataEntities;

    public void save(Context context) {
        Field[] fields = getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        String className = getName();
        dataEntities = new ArrayList<DataEntity>();
        for (Field field :
                fields) {
            try {
                SmartLog.i("info", "属性：" + field.getName() + "  " + field.getType() + "  " + className + "  " + field.get(this));
                if (!field.getName().equals("$change") && !field.getName().equals("serialVersionUID")) {
                    DataEntity dataEntity = new DataEntity();
                    dataEntity.setName(field.getName());
                    dataEntity.setObject(field.get(this));
                    dataEntities.add(dataEntity);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        SmartSQLite.getInstance(context).addData(className, dataEntities);
    }

    public void delete(Context context, String key) {
        Field[] fields = getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        String className = getName();
        String object = null;
        for (Field field :
                fields) {
            if (field.getName().equals(key)) {
                try {
                    object = field.get(this).toString();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        SmartSQLite.getInstance(context).delData(className, key, object);
    }

    public void update(Context context, String key) {
        Field[] fields = getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        dataEntities = new ArrayList<DataEntity>();
        String className = getName();
        String object = null;
        for (Field field :
                fields) {
            if (field.getName().equals(key)) {
                try {
                    object = field.get(this).toString();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (!field.getName().equals("$change") && !field.getName().equals("serialVersionUID")) {
                    DataEntity dataEntity = new DataEntity();
                    dataEntity.setName(field.getName());
                    dataEntity.setObject(field.get(this));
                    dataEntity.setType(field.getGenericType().toString());
                    dataEntities.add(dataEntity);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        SmartSQLite.getInstance(context).updateData(className, key, object, dataEntities);
    }

    public List<T> getDatas(Context context, Class<T> table) {
        return SmartSQLite.getInstance(context).getDatas(table);
    }

    private String getName() {
        return getClass().getSimpleName();
    }
}
