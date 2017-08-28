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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tandong.smartsqlite.base.DBEntity;
import com.tandong.smartsqlite.base.DataEntity;
import com.tandong.smartsqlite.base.EntityColumn;
import com.tandong.smartsqlite.key.TableNameInDB;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * 框架核心数据处理类
 *
 * @author Tandong
 * @date 2017-8-20
 */

public class SmartSQLite<T> {
    private static SmartSQLite dbManager;
    private SmartHelper smartHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public static synchronized SmartSQLite getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new SmartSQLite(context);
        }
        return dbManager;
    }

    public SmartSQLite(Context context) {
        this.context = context;
        smartHelper = new SmartHelper(context);
        if (SmartConfig.DB_PATH.equals("")) {
            sqLiteDatabase = smartHelper.getWritableDatabase();
        } else {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(SmartConfig.DB_PATH + SmartConfig.DB_NAME, null);
        }
    }

    public void createTables(ArrayList<DBEntity> list) {
        //开启事务
        sqLiteDatabase.beginTransaction();
        try {
            for (int i = 0; i < list.size(); i++) {
                String sql = "CREATE TABLE IF NOT EXISTS " + list.get(i).getClassName() + " (tables)";
                String tables = "";
                for (int j = 0; j < list.get(i).getEntityColumns().size(); j++) {
                    if (j == list.get(i).getEntityColumns().size() - 1) {
                        if (list.get(i).getEntityColumns().get(j).getName().equals("key_id")) {
                            tables = tables + list.get(i).getEntityColumns().get(j).getName() + " " + Utils.convertSQLType(list.get(i).getEntityColumns().get(j).getType()) + " PRIMARY KEY AUTOINCREMENT";
                        } else {
                            tables = tables + list.get(i).getEntityColumns().get(j).getName() + " " + Utils.convertSQLType(list.get(i).getEntityColumns().get(j).getType());
                        }
                    } else {
                        if (list.get(i).getEntityColumns().get(j).getName().equals("key_id")) {
                            tables = tables + list.get(i).getEntityColumns().get(j).getName() + " " + Utils.convertSQLType(list.get(i).getEntityColumns().get(j).getType()) + " PRIMARY KEY AUTOINCREMENT,";
                        } else {
                            tables = tables + list.get(i).getEntityColumns().get(j).getName() + " " + Utils.convertSQLType(list.get(i).getEntityColumns().get(j).getType()) + ",";
                        }
                    }
                }
                SmartLog.i("info", "SQL:" + sql.replace("tables", tables));
                sqLiteDatabase.execSQL(sql.replace("tables", tables));
            }
            //设置事务标志为成功，当结束事务时就会提交事务
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            //结束事务
            sqLiteDatabase.endTransaction();
        }
    }

    public void alterTables(ArrayList<DBEntity> list) {
        //开启事务
        sqLiteDatabase.beginTransaction();
        try {
            for (int i = 0; i < list.size(); i++) {
                String sql = "ALTER TABLE " + list.get(i).getClassName() + " ADD ";
                for (int j = 0; j < list.get(i).getEntityColumns().size(); j++) {
                    SmartLog.i("info", "数据类型SQL：" + sql + list.get(i).getEntityColumns().get(j).getName() + " " + Utils.convertSQLType(list.get(i).getEntityColumns().get(j).getType()));
                    sqLiteDatabase.execSQL(sql + list.get(i).getEntityColumns().get(j).getName() + " " + Utils.convertSQLType(list.get(i).getEntityColumns().get(j).getType()));
                }
            }
            //设置事务标志为成功，当结束事务时就会提交事务
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            //结束事务
            sqLiteDatabase.endTransaction();
        }
    }

    public void delTables(ArrayList<DBEntity> list) {
        //开启事务
        sqLiteDatabase.beginTransaction();
        try {
            for (int i = 0; i < list.size(); i++) {
                String sql = "DROP TABLE " + list.get(i).getClassName();
                sqLiteDatabase.execSQL(sql);
            }
            //设置事务标志为成功，当结束事务时就会提交事务
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            //结束事务
            sqLiteDatabase.endTransaction();
        }
    }

    public void delTable(Class table) {
        String sql = "DROP TABLE " + table.getSimpleName();
        sqLiteDatabase.execSQL(sql);
    }

    public void addData(String className, List<DataEntity> dataEntities) {
        String sql = "insert into " + className;
        Object[] objects = new Object[dataEntities.size()];
        String key = "(";
        String values = "(";
        for (int i = 0; i < dataEntities.size(); i++) {
            if (i == dataEntities.size() - 1) {
                key = key + dataEntities.get(i).getName() + ")";
                values = values + "?)";
            } else {
                key = key + dataEntities.get(i).getName() + ",";
                values = values + "?,";
            }
            objects[i] = dataEntities.get(i).getObject();
        }
        sql = sql + key + " values" + values;
        SmartLog.i("info", "SQL:" + sql + "," + values + "  " + objects);
        sqLiteDatabase.execSQL(sql, objects);
    }

    public void delData(String className, String key, String object) {
        sqLiteDatabase.delete(className, key + "=?", new String[]{object});
    }

    public void updateData(String className, String key, String object, List<DataEntity> dataEntity) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < dataEntity.size(); i++) {
            if (Utils.convertSQLType(dataEntity.get(i).getType()).equals("INTEGER")) {
                values.put(dataEntity.get(i).getName(), (Integer) dataEntity.get(i).getObject());
            } else if (Utils.convertSQLType(dataEntity.get(i).getType()).equals("VARCHAR")) {
                values.put(dataEntity.get(i).getName(), dataEntity.get(i).getObject().toString());
            }
        }
        sqLiteDatabase.update(className, values, key + "=?", new String[]{object});
    }

    public void getAllTables() {
        Cursor cursor = sqLiteDatabase.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            SmartLog.i("info", name);
        }
    }

    public List<T> getDatas(Class<T> table) {
        List<T> list = new ArrayList<T>();
        String name = table.getSimpleName();
        boolean tableName = table.isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = table.getAnnotations();
            name = ((TableNameInDB) annotationses[0]).value();
        }
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + name, null);
        DBEntity dbEntity = Utils.getEntity(table);
        while (cursor.moveToNext()) {
            T object = null;
            try {
                object = table.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < dbEntity.getEntityColumns().size(); i++) {
                try {
                    EntityColumn entityColumn = dbEntity.getEntityColumns().get(i);
                    Field field = table.getDeclaredField(entityColumn.getName());
                    field.setAccessible(true);
                    if (Utils.convertSQLType(entityColumn.getType()).equals("VARCHAR")) {
                        if (entityColumn.getType().equals("long")) {
                            field.set(object, cursor.getLong(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("boolean")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())).equals("1") ? true : false);
                        } else {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())));
                        }
                    } else {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(entityColumn.getName())));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            list.add(object);
        }
        return list;
    }

    public void save(Context context, Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        String className = object.getClass().getSimpleName();
        List<DataEntity> dataEntities = new ArrayList<DataEntity>();
        boolean tableName = object.getClass().isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = object.getClass().getAnnotations();
            className = ((TableNameInDB) annotationses[0]).value();
        }
        for (Field field :
                fields) {
            try {
                if (!field.getName().equals("$change") && !field.getName().equals("serialVersionUID") && !field.getName().equals("key_id")) {
                    DataEntity dataEntity = new DataEntity();
                    dataEntity.setName(field.getName());
                    dataEntity.setObject(field.get(object));
                    dataEntities.add(dataEntity);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        SmartSQLite.getInstance(context).addData(className, dataEntities);
    }

    public void delete(Context context, Object obj, String key) {
        Field[] fields = obj.getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        String className = obj.getClass().getSimpleName();
        boolean tableName = obj.getClass().isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = obj.getClass().getAnnotations();
            className = ((TableNameInDB) annotationses[0]).value();
        }
        String object = null;
        for (Field field :
                fields) {
            if (field.getName().equals(key)) {
                try {
                    object = field.get(obj).toString();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        SmartSQLite.getInstance(context).delData(className, key, object);
    }

    public void update(Context context, Object obj, String key) {
        Field[] fields = obj.getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        List<DataEntity> dataEntities = new ArrayList<DataEntity>();
        String className = obj.getClass().getSimpleName();
        boolean tableName = obj.getClass().isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = obj.getClass().getAnnotations();
            className = ((TableNameInDB) annotationses[0]).value();
        }
        String object = null;
        for (Field field :
                fields) {
            if (field.getName().equals(key)) {
                try {
                    object = field.get(obj).toString();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (!field.getName().equals("$change") && !field.getName().equals("serialVersionUID")) {
                    DataEntity dataEntity = new DataEntity();
                    dataEntity.setName(field.getName());
                    dataEntity.setObject(field.get(obj));
                    dataEntity.setType(field.getGenericType().toString());
                    dataEntities.add(dataEntity);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        SmartSQLite.getInstance(context).updateData(className, key, object, dataEntities);
    }

    public List<Object> queryDatas(Class table, String key, String value) {
        List<Object> list = new ArrayList<Object>();
        String name = table.getSimpleName();
        boolean tableName = table.isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = table.getClass().getAnnotations();
            name = ((TableNameInDB) annotationses[0]).value();
        }
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + name + " where "
                + key + "=?", new String[]{value});
        DBEntity dbEntity = Utils.getEntity(table);
        while (cursor.moveToNext()) {
            Object object = null;
            try {
                object = table.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < dbEntity.getEntityColumns().size(); i++) {
                try {
                    EntityColumn entityColumn = dbEntity.getEntityColumns().get(i);
                    Field field = table.getDeclaredField(entityColumn.getName());
                    field.setAccessible(true);
                    if (Utils.convertSQLType(entityColumn.getType()).equals("VARCHAR")) {
                        if (entityColumn.getType().equals("long")) {
                            field.set(object, cursor.getLong(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("boolean")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())).equals("1") ? true : false);
                        } else {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())));
                        }
                    } else {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(entityColumn.getName())));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            list.add(object);
        }
        return list;
    }

    public List<Object> queryDatas(Class table, String key, String value,String orderBy) {
        List<Object> list = new ArrayList<Object>();
        String name = table.getSimpleName();
        boolean tableName = table.isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = table.getClass().getAnnotations();
            name = ((TableNameInDB) annotationses[0]).value();
        }
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + name + " where "
                + key + "=? order by ?", new String[]{value,orderBy});
        DBEntity dbEntity = Utils.getEntity(table);
        while (cursor.moveToNext()) {
            Object object = null;
            try {
                object = table.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < dbEntity.getEntityColumns().size(); i++) {
                try {
                    EntityColumn entityColumn = dbEntity.getEntityColumns().get(i);
                    Field field = table.getDeclaredField(entityColumn.getName());
                    field.setAccessible(true);
                    if (Utils.convertSQLType(entityColumn.getType()).equals("VARCHAR")) {
                        if (entityColumn.getType().equals("long")) {
                            field.set(object, cursor.getLong(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("boolean")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())).equals("1") ? true : false);
                        } else {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())));
                        }
                    } else {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(entityColumn.getName())));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            list.add(object);
        }
        return list;
    }

    public List<Object> queryBlurryDatas(Class table, String key, String likeValue) {
        List<Object> list = new ArrayList<Object>();
        String name = table.getSimpleName();
        boolean tableName = table.isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = table.getClass().getAnnotations();
            name = ((TableNameInDB) annotationses[0]).value();
        }
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + name + " where "
                + key + " like ?", new String[]{"%" + likeValue + "%"});
        DBEntity dbEntity = Utils.getEntity(table);
        while (cursor.moveToNext()) {
            Object object = null;
            try {
                object = table.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < dbEntity.getEntityColumns().size(); i++) {
                try {
                    EntityColumn entityColumn = dbEntity.getEntityColumns().get(i);
                    Field field = table.getDeclaredField(entityColumn.getName());
                    field.setAccessible(true);
                    if (Utils.convertSQLType(entityColumn.getType()).equals("VARCHAR")) {
                        if (entityColumn.getType().equals("long")) {
                            field.set(object, cursor.getLong(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("boolean")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())).equals("1") ? true : false);
                        } else {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())));
                        }
                    } else {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(entityColumn.getName())));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            list.add(object);
        }
        return list;
    }

    public List<Object> queryBlurryDatas(Class table, String key, String likeValue, String orderBy) {
        List<Object> list = new ArrayList<Object>();
        String name = table.getSimpleName();
        boolean tableName = table.isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = table.getClass().getAnnotations();
            name = ((TableNameInDB) annotationses[0]).value();
        }
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + name + " where "
                + key + " like ? order by ?", new String[]{"%" + likeValue + "%", orderBy});
        DBEntity dbEntity = Utils.getEntity(table);
        while (cursor.moveToNext()) {
            Object object = null;
            try {
                object = table.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < dbEntity.getEntityColumns().size(); i++) {
                try {
                    EntityColumn entityColumn = dbEntity.getEntityColumns().get(i);
                    Field field = table.getDeclaredField(entityColumn.getName());
                    field.setAccessible(true);
                    if (Utils.convertSQLType(entityColumn.getType()).equals("VARCHAR")) {
                        if (entityColumn.getType().equals("long")) {
                            field.set(object, cursor.getLong(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("boolean")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())).equals("1") ? true : false);
                        } else {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())));
                        }
                    } else {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(entityColumn.getName())));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            list.add(object);
        }
        return list;
    }

    public List<Object> queryPagingDatas(Class table, String[] key, String[] value, int pageNumber, int pageSize) {
        List<Object> list = new ArrayList<Object>();
        String keys = "";
        for (int i = 0; i < key.length; i++) {
            if (i == key.length - 1) {
                keys = keys + key[i] + "=?";
            } else {
                keys = keys + key[i] + "=?,";
            }
        }
        String values = "";
        for (int i = 0; i < value.length; i++) {
            if (i == value.length - 1) {
                values = values + value[i];
            } else {
                values = values + value[i] + ",";
            }
        }
        String name = table.getSimpleName();
        boolean tableName = table.isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = table.getClass().getAnnotations();
            name = ((TableNameInDB) annotationses[0]).value();
        }
        Cursor cursor = sqLiteDatabase.query(name, null,
                keys,
                new String[]{values}, null,
                null,
                null,
                pageNumber + "," + pageSize);
        DBEntity dbEntity = Utils.getEntity(table);
        while (cursor.moveToNext()) {
            Object object = null;
            try {
                object = table.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < dbEntity.getEntityColumns().size(); i++) {
                try {
                    EntityColumn entityColumn = dbEntity.getEntityColumns().get(i);
                    Field field = table.getDeclaredField(entityColumn.getName());
                    field.setAccessible(true);
                    if (Utils.convertSQLType(entityColumn.getType()).equals("VARCHAR")) {
                        if (entityColumn.getType().equals("long")) {
                            field.set(object, cursor.getLong(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("boolean")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())).equals("1") ? true : false);
                        } else {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())));
                        }
                    } else {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(entityColumn.getName())));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            list.add(object);
        }
        return list;
    }

    public List<Object> queryPagingDatas(Class table, String[] key, String[] value, int pageNumber, int pageSize,String orderBy) {
        List<Object> list = new ArrayList<Object>();
        String keys = "";
        for (int i = 0; i < key.length; i++) {
            if (i == key.length - 1) {
                keys = keys + key[i] + "=?";
            } else {
                keys = keys + key[i] + "=?,";
            }
        }
        String values = "";
        for (int i = 0; i < value.length; i++) {
            if (i == value.length - 1) {
                values = values + value[i];
            } else {
                values = values + value[i] + ",";
            }
        }
        String name = table.getSimpleName();
        boolean tableName = table.isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = table.getClass().getAnnotations();
            name = ((TableNameInDB) annotationses[0]).value();
        }
        Cursor cursor = sqLiteDatabase.query(name, null,
                keys,
                new String[]{values}, null,
                null,
                orderBy,
                pageNumber + "," + pageSize);
        DBEntity dbEntity = Utils.getEntity(table);
        while (cursor.moveToNext()) {
            Object object = null;
            try {
                object = table.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < dbEntity.getEntityColumns().size(); i++) {
                try {
                    EntityColumn entityColumn = dbEntity.getEntityColumns().get(i);
                    Field field = table.getDeclaredField(entityColumn.getName());
                    field.setAccessible(true);
                    if (Utils.convertSQLType(entityColumn.getType()).equals("VARCHAR")) {
                        if (entityColumn.getType().equals("long")) {
                            field.set(object, cursor.getLong(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("boolean")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())).equals("1") ? true : false);
                        } else {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())));
                        }
                    } else {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(entityColumn.getName())));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            list.add(object);
        }
        return list;
    }

    public List<T> queryBlurryPagingDatas(Class<T> table, String key, String likeValue, int pageNumber, int pageSize) {
        List<T> list = new ArrayList<T>();
        String name = table.getSimpleName();
        boolean tableName = table.isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = table.getAnnotations();
            name = ((TableNameInDB) annotationses[0]).value();
        }
        Cursor cursor = sqLiteDatabase.query(name, null,
                key + " like ?",
                new String[]{"%" + likeValue + "%"}, null,
                null,
                null,
                pageNumber * pageSize + "," + pageSize);
        DBEntity dbEntity = Utils.getEntity(table);
        while (cursor.moveToNext()) {
            T object = null;
            try {
                object = table.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < dbEntity.getEntityColumns().size(); i++) {
                try {
                    EntityColumn entityColumn = dbEntity.getEntityColumns().get(i);
                    Field field = table.getDeclaredField(entityColumn.getName());
                    field.setAccessible(true);
                    if (Utils.convertSQLType(entityColumn.getType()).equals("VARCHAR")) {
                        if (entityColumn.getType().equals("long")) {
                            field.set(object, cursor.getLong(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("boolean")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())).equals("1") ? true : false);
                        } else {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())));
                        }
                    } else {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(entityColumn.getName())));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            list.add(object);
        }
        return list;
    }

    public List<T> queryBlurryPagingDatas(Class<T> table, String key, String likeValue, int pageNumber, int pageSize,String orderBy) {
        List<T> list = new ArrayList<T>();
        String name = table.getSimpleName();
        boolean tableName = table.isAnnotationPresent(TableNameInDB.class);
        if (tableName) {
            Annotation[] annotationses = table.getAnnotations();
            name = ((TableNameInDB) annotationses[0]).value();
        }
        Cursor cursor = sqLiteDatabase.query(name, null,
                key + " like ?",
                new String[]{"%" + likeValue + "%"}, null,
                null,
                orderBy,
                pageNumber * pageSize + "," + pageSize);
        DBEntity dbEntity = Utils.getEntity(table);
        while (cursor.moveToNext()) {
            T object = null;
            try {
                object = table.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < dbEntity.getEntityColumns().size(); i++) {
                try {
                    EntityColumn entityColumn = dbEntity.getEntityColumns().get(i);
                    Field field = table.getDeclaredField(entityColumn.getName());
                    field.setAccessible(true);
                    if (Utils.convertSQLType(entityColumn.getType()).equals("VARCHAR")) {
                        if (entityColumn.getType().equals("long")) {
                            field.set(object, cursor.getLong(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(entityColumn.getName())));
                        } else if (entityColumn.getType().equals("boolean")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())).equals("1") ? true : false);
                        } else {
                            field.set(object, cursor.getString(cursor.getColumnIndex(entityColumn.getName())));
                        }
                    } else {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(entityColumn.getName())));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            list.add(object);
        }
        return list;
    }

    public List<Object> getEntityDatas(Context context, Class table) {
        return SmartSQLite.getInstance(context).getDatas(table);
    }

    public static void initSmartSQLite(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SmartConfig.sp_key, MODE_PRIVATE);
        String sqlStructure = "";
        for (int i = 0; i < SmartConfig.classes.size(); i++) {
            String sql = Utils.getSQL(context.getPackageName() + "." + SmartConfig.ENTITY_PACKAGE + "." + SmartConfig.classes.get(i));
            Log.i("info", "SQL:" + sql);
            if (i == 0) {
                sqlStructure = sqlStructure + sql;
            } else {
                sqlStructure = sqlStructure + "/" + sql;
            }
        }
        SmartConfig.sqlStructure = sqlStructure;
        Utils.mapSQL(context, sharedPreferences.getString(SmartConfig.sp_key, ""));
        sharedPreferences.edit().putString(SmartConfig.sp_key, sqlStructure).commit();
    }

    public void executeSQL(String sql) {
        sqLiteDatabase.execSQL(sql);
    }

    public boolean isDbOpen() {
        if (sqLiteDatabase != null) {
            return sqLiteDatabase.isOpen();
        } else {
            return false;
        }
    }

    public void closeDB() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }
}
