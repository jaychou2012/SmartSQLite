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

import java.util.List;

/**
 * 数据操作方案二基础接口
 *
 * @author Tandong
 * @date 2017-8-20
 */

public interface TableEntity {
    void save(Context context);

    void delete(Context context, String key);

    void update(Context context, String key);

    List<Object> getDatas(Context context, Class table);
}
