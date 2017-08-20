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

import android.util.Log;

/**
 * 框架日志控制类
 *
 * @author Tandong
 * @date 2017-8-20
 */

public class SmartLog {
    public static void i(String tag, String text) {
        if (SmartConfig.LOG) {
            Log.i(tag, text);
        }
    }
}
