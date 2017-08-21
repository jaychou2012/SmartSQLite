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
package com.tandong.smartsqlitedemo.entity;

import android.content.Context;

import com.tandong.smartsqlite.base.TableEntity;
import com.tandong.smartsqlite.utils.SmartSQLite;

import java.util.List;

/**
 * 数据库实体类方案二，通过接口实现方式
 * (增加属性时请依次按属性顺序往下增加)
 *
 * @author Tandong
 * @date 2017-8-20
 */

public class Teacher implements TableEntity {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void save(Context context) {
        SmartSQLite.getInstance(context).save(context, this);
    }

    @Override
    public void delete(Context context, String key) {
        SmartSQLite.getInstance(context).delete(context, this, key);
    }

    @Override
    public void update(Context context, String key) {
        SmartSQLite.getInstance(context).update(context, this, key);
    }

    @Override
    public List<Object> getDatas(Context context, Class table) {
        return SmartSQLite.getInstance(context).getEntityDatas(context, table);
    }
}
