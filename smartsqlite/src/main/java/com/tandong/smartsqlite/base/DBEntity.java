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

import java.util.List;

/**
 * 数据基础实体结构
 *
 * @author Tandong
 * @date 2017-8-20
 */

public class DBEntity {
    private String className;
    private List<EntityColumn> entityColumns;
    private String sql;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns;
    }

    public void setEntityColumns(List<EntityColumn> entityColumns) {
        this.entityColumns = entityColumns;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
