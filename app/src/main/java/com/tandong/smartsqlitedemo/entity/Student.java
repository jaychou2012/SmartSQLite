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

import com.tandong.smartsqlite.base.TableObject;

/**
 * 数据库实体类方案一，通过继承父类实现方式，推荐
 * (增加属性时请依次按属性顺序往下增加)
 *
 * @author Tandong
 * @date 2017-8-20
 */
public class Student extends TableObject {
    private int id;
    private String name;
    private boolean high;
    private long timeLong;
    private float timeFloat;
    private double timeDouble;

    public boolean isHigh() {
        return high;
    }

    public void setHigh(boolean high) {
        this.high = high;
    }

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

    public long getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(long timeLong) {
        this.timeLong = timeLong;
    }

    public float getTimeFloat() {
        return timeFloat;
    }

    public void setTimeFloat(float timeFloat) {
        this.timeFloat = timeFloat;
    }

    public double getTimeDouble() {
        return timeDouble;
    }

    public void setTimeDouble(double timeDouble) {
        this.timeDouble = timeDouble;
    }

}
