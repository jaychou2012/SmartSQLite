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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.tandong.smartsqlite.base.DBEntity;
import com.tandong.smartsqlite.base.EntityColumn;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 框架工具类
 *
 * @author Tandong
 * @date 2017-8-20
 */
public class Utils {

    public static String getSQL(String className) {
        Class classEntity = null;
        try {
            classEntity = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String sql = classEntity.getSimpleName() + ":";
        Field[] fields = classEntity.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getName().equals("serialVersionUID")) {
                break;
            } else if (field.getName().contains("$change")) {
                break;
            }
            if (i != fields.length) {
                sql = sql + field.getName() + " " + convertType(field.getGenericType()) + ",";
            } else {
                sql = sql + field.getName() + " " + field.getGenericType();
            }
            SmartLog.i("info", "属性：" + field.getName() + "  " + field.getType() + "  " + className + "  ");
        }
        String tableName = sql.split(":")[0];
        String tables = sql.split(":")[1].endsWith(",") ? sql.split(":")[1].substring(0, sql.split(":")[1].length() - 1) : sql.split(":")[1];
        return tableName + ":" + tables;
    }

    public static String convertType(Type type) {
        if (type.toString().contains("java.lang.String")) {
            return "String";
        } else {
            return type.toString();
        }
    }

    public static String convertSQLType(String type) {
        if (type.contains("java.lang.String")) {
            return "VARCHAR";
        } else if (type.contains("int")) {
            return "INTEGER";
        } else if (type.contains("long")) {
            return "VARCHAR";
        } else if (type.contains("double")) {
            return "VARCHAR";
        } else if (type.contains("float")) {
            return "VARCHAR";
        } else {
            return "VARCHAR";
        }
    }

    public static DBEntity getEntity(Class classEntity) {
        Field[] fields = classEntity.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        String className = classEntity.getSimpleName();
        DBEntity dbEntity = new DBEntity();
        dbEntity.setClassName(className);
        List<EntityColumn> entityColumns = new ArrayList<EntityColumn>();
        for (Field field :
                fields) {
            if (!field.getName().equals("$change") && !field.getName().equals("serialVersionUID")) {
                EntityColumn entityColumn = new EntityColumn();
                entityColumn.setType(field.getGenericType().toString());
                SmartLog.i("info", "数据类型：" + field.getGenericType());
                entityColumn.setName(field.getName());
                entityColumns.add(entityColumn);
            }
        }
        dbEntity.setEntityColumns(entityColumns);
        return dbEntity;
    }

    public static Object getByReflect(String tableName, List listobj) throws Exception {
        Class<?> model = Class.forName(tableName);
        Object object = new Object();
        if (model != null) {
            Field[] field = model.getDeclaredFields();
            String[] modelName = new String[field.length];
            String[] modelType = new String[field.length];
            object = model.newInstance();
            Method m = null;
            for (int i = 1; i < field.length; i++) {
                String name = field[i].getName();
                Object value = null;
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                String type = field[i].getGenericType().toString();
                if (type.equals("class java.lang.String")) {
                    m = model.getDeclaredMethod("set" + name, String.class);
                    if (listobj.get(i - 1) instanceof Double) {
                        Double d = (Double) listobj.get(i - 1);
                        value = String.valueOf(d);
                    } else {
                        value = (String) listobj.get(i - 1);
                    }
                }
                if (type.equals("class java.lang.Integer")) {
                    m = model.getDeclaredMethod("set" + name, Integer.class);
                    Double d = (Double) listobj.get(i - 1);
                    value = Integer.valueOf(d.intValue());
                }
                if (type.equals("class java.lang.Short")) {
                    m = model.getDeclaredMethod("set" + name, Short.class);
                    value = (Short) listobj.get(i - 1);
                }
                if (type.equals("class java.lang.Float")) {
                    m = model.getDeclaredMethod("set" + name, Float.class);
                    value = (Float) listobj.get(i - 1);
                }
                if (type.equals("class java.lang.Double")) {
                    m = model.getDeclaredMethod("set" + name, Double.class);
                    value = (Double) listobj.get(i - 1);
                }
                if (type.equals("class java.lang.Boolean")) {
                    m = model.getDeclaredMethod("set" + name, Boolean.class);
                    value = (Boolean) listobj.get(i - 1);
                }
                if (m != null) {
                    m.invoke(object, value);
                }
            }

        }
        return object;
    }

    public static String getSQLLanguage(String sql) {
        String tableName = sql.split(":")[0];
        String tables = sql.split(":")[1];
        return "CREATE TABLE IF NOT EXISTS " + tableName + "(" + tables + ")";
    }

    public static void updateSqliteDatabase(Context context, SQLiteDatabase sqLiteDatabase) {

    }

    /**
     * @param structure 上一个版本的数据库结构
     */
    public static void mapSQL(Context context, String structure) {//获取到的上一个版本的数据库结构
        ArrayList<DBEntity> splist = mapChangeSQLColumn(structure);//上一版本
        ArrayList<DBEntity> curList = mapChangeSQLColumn(SmartConfig.sqlStructure);//当前版本
        if (structure.equals("")) {//全部创建
            SmartLog.i("info", "SQL创建");
            SmartSQLite.getInstance(context).createTables(curList);
        } else if (splist.size() < curList.size()) {//有新增表
            SmartLog.i("info", "SQL有新增表");
            ArrayList<ArrayList<DBEntity>> lists = getNewSQLTable(splist, curList);
            //获取新增表
            ArrayList<DBEntity> list = lists.get(0);
            SmartSQLite.getInstance(context).createTables(list);
            //是否改字段
            ArrayList<DBEntity> noList = lists.get(1);//没有新增的表
            //字段有变化的表
            ArrayList<ArrayList<DBEntity>> changeLists = getChangeSQLColumn(splist, noList);
            //字段变化的表（已经字段变化的信息返回）
            ArrayList<DBEntity> changeSQLColumnInfo = getChangeSQLColumnInfo(changeLists.get(0), changeLists.get(1));
            if (changeSQLColumnInfo.size() > 0) {
                SmartLog.i("info", "SQL无新增表字段变化" + changeSQLColumnInfo.get(0).getSql());
                SmartSQLite.getInstance(context).alterTables(changeSQLColumnInfo);
            }
        } else if (splist.size() > curList.size()) {//有删除表
            SmartLog.i("info", "SQL有删除表");
            //获取删除表
            ArrayList<ArrayList<DBEntity>> lists = getDelSQLTable(splist, curList);
            ArrayList<DBEntity> list = lists.get(0);
            SmartSQLite.getInstance(context).delTables(list);
            //是否改字段(获取没有删除的上一版本表)
            ArrayList<DBEntity> noList = lists.get(1);
            ArrayList<ArrayList<DBEntity>> changeLists = getChangeSQLColumn(noList, curList);
            //字段变化的表（已经字段变化的信息返回）
            ArrayList<DBEntity> changeSQLColumnInfo = getChangeSQLColumnInfo(changeLists.get(0), changeLists.get(1));
            if (changeSQLColumnInfo.size() > 0) {
                SmartLog.i("info", "SQL无新增表字段变化" + changeSQLColumnInfo.get(0).getSql());
                SmartSQLite.getInstance(context).alterTables(changeSQLColumnInfo);
            }
        } else {//无新增表
            //是否改字段
            //字段有变化的表
            SmartLog.i("info", "SQL无新增表");
            ArrayList<ArrayList<DBEntity>> changeLists = getChangeSQLColumn(splist, curList);
            //字段变化的表（已经字段变化的信息返回）
            ArrayList<DBEntity> changeSQLColumnInfo = getChangeSQLColumnInfo(changeLists.get(0), changeLists.get(1));
            if (changeSQLColumnInfo.size() > 0) {
                SmartLog.i("info", "SQL无新增表字段变化字段" + changeSQLColumnInfo.get(0).getEntityColumns().size());
                SmartSQLite.getInstance(context).alterTables(changeSQLColumnInfo);
            }
        }
    }

    public static ArrayList<DBEntity> mapChangeSQLColumn(String structure) {
        String[] sqlStructure = structure.split("/");
        ArrayList<DBEntity> lists = new ArrayList<DBEntity>();
        if (structure.equals("")) {
            return lists;
        }
        for (int i = 0; i < sqlStructure.length; i++) {
            String sql = sqlStructure[i];
            DBEntity dbEntity = new DBEntity();
            dbEntity.setClassName(sql.split(":")[0]);
            List<EntityColumn> columnsList = new ArrayList<EntityColumn>();
            for (int j = 0; j < sql.split(":")[1].split(",").length; j++) {
                String column = sql.split(":")[1].split(",")[j];
                EntityColumn entityColumn = new EntityColumn();
                entityColumn.setName(column.split(" ")[0]);
                entityColumn.setType(column.split(" ")[1]);
                columnsList.add(entityColumn);
            }
            dbEntity.setEntityColumns(columnsList);
            dbEntity.setSql(sql);
            lists.add(dbEntity);
        }
        return lists;
    }

    public static ArrayList<ArrayList<DBEntity>> getNewSQLTable(ArrayList<DBEntity> old, ArrayList<DBEntity> current) {
        ArrayList<ArrayList<DBEntity>> allLists = new ArrayList<ArrayList<DBEntity>>();
        ArrayList<DBEntity> lists = new ArrayList<DBEntity>();
        ArrayList<DBEntity> noLists = new ArrayList<DBEntity>();
        String tables = "";
        for (int i = 0; i < old.size(); i++) {//历史现有表名拼接
            tables = tables + "," + old.get(i).getClassName();
        }
        for (int i = 0; i < current.size(); i++) {//与当前表名比较
            if (!tables.contains(current.get(i).getClassName())) {
                lists.add(current.get(i));
            } else {//无新增的表
                noLists.add(current.get(i));
            }
        }
        allLists.add(lists);
        allLists.add(noLists);
        return allLists;
    }

    /**
     * 获取删除表列表
     *
     * @param old
     * @param current
     * @return
     */
    public static ArrayList<ArrayList<DBEntity>> getDelSQLTable(ArrayList<DBEntity> old, ArrayList<DBEntity> current) {
        ArrayList<ArrayList<DBEntity>> allLists = new ArrayList<ArrayList<DBEntity>>();
        ArrayList<DBEntity> lists = new ArrayList<DBEntity>();
        ArrayList<DBEntity> noLists = new ArrayList<DBEntity>();
        String tables = "";
        for (int i = 0; i < current.size(); i++) {//当前现有表名拼接
            tables = tables + "," + current.get(i).getClassName();
        }
        for (int i = 0; i < old.size(); i++) {//与历史表名比较
            if (!tables.contains(old.get(i).getClassName())) {
                lists.add(old.get(i));//已被删除
            } else {//无新增的表
                noLists.add(old.get(i));//无变化表
            }
        }
        allLists.add(lists);
        allLists.add(noLists);
        return allLists;
    }

    public static ArrayList<ArrayList<String>> getNewSQLTable(String structure) {//获取新增表名和无变化表名
        ArrayList<ArrayList<String>> allLists = new ArrayList<ArrayList<String>>();
        ArrayList<String> lists = new ArrayList<String>();
        ArrayList<String> noLists = new ArrayList<String>();
        String tables = "";
        for (int i = 0; i < structure.split("/").length; i++) {//历史现有表名拼接
            tables = tables + "," + structure.split("/")[i].split(":")[0];
        }
        for (int i = 0; i < SmartConfig.sqlStructure.split("/").length; i++) {//与当前表名比较
            if (!tables.contains(SmartConfig.sqlStructure.split("/")[i].split(":")[0])) {
                lists.add(SmartConfig.sqlStructure.split("/")[i]);
            } else {//无新增的表
                noLists.add(SmartConfig.sqlStructure.split("/")[i]);
            }
        }
        allLists.add(lists);
        allLists.add(noLists);
        return allLists;
    }

    /**
     * 获取有变化字段的表
     *
     * @param old    上一版本的表
     * @param noList 当前版本无新增需判断是否有字段修改的表
     * @return
     */
    public static ArrayList<ArrayList<DBEntity>> getChangeSQLColumn(ArrayList<DBEntity> old, ArrayList<DBEntity> noList) {
        ArrayList<ArrayList<DBEntity>> allLists = new ArrayList<ArrayList<DBEntity>>();
        ArrayList<DBEntity> oldlists = new ArrayList<DBEntity>();
        ArrayList<DBEntity> curlists = new ArrayList<DBEntity>();
        for (int i = 0; i < noList.size(); i++) {
            if (!noList.get(i).getSql().equals(old.get(i).getSql()) && noList.get(i).getEntityColumns().size() > old.get(i).getEntityColumns().size()) {//字段有变化并且不是删除字段而是新增字段
                oldlists.add(old.get(i));
                curlists.add(noList.get(i));
            }
        }
        allLists.add(oldlists);
        allLists.add(curlists);
        return allLists;
    }

    /**
     * @param old     上一版本无变化表
     * @param curList 当前版本无变化表
     * @return
     */
    public static ArrayList<DBEntity> getChangeSQLColumnInfo(ArrayList<DBEntity> old, ArrayList<DBEntity> curList) {//获取有变化字段的表
        ArrayList<DBEntity> lists = new ArrayList<DBEntity>();
        ArrayList<DBEntity> curListDBEntity = curList;
        for (int i = 0; i < curListDBEntity.size(); i++) {
            for (int j = 0; j < old.get(i).getEntityColumns().size(); j++) {
                SmartLog.i("info", "移除数据：" + curListDBEntity.get(i).getEntityColumns().contains(old.get(i).getEntityColumns().get(j)));
                curListDBEntity.get(i).getEntityColumns().remove(0);//剩余新增字段,old.get(i).getEntityColumns().get(j)
            }
            if (curListDBEntity.get(i).getEntityColumns().size() > 0) {
                lists.add(curListDBEntity.get(i));
                SmartLog.i("info", "移除数据添加：" + curListDBEntity.get(i).getEntityColumns().size() + "  " + curListDBEntity.get(i).getEntityColumns().get(0).getName());
            }
        }
        return lists;
    }

}
