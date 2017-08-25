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
package com.tandong.smartsqlitedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tandong.smartsqlite.utils.SmartSQLite;
import com.tandong.smartsqlitedemo.entity.Student;
import com.tandong.smartsqlitedemo.entity.Teacher;

import java.util.List;

/**
 * SmartSQLite演示基本操作
 *
 * @author Tandong
 * @date 2017-8-20
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Student student = new Student();
        student.setId(2);
        student.setName("名字名字4");
        student.setHigh(true);
        student.setTimeDouble(200);
        student.setTimeFloat(200.0f);
        student.setTimeLong(200);
        student.save(this);
//        student.update(this, "id");
//        student.delete(this,"id");
        List<Student> list = student.getDatas(this, Student.class);
//        List<Student> list = SmartSQLite.getInstance(this).getDatas(Student.class);
        for (int i = 0; i < list.size(); i++) {
            Student stu = list.get(i);
            Log.i("info", "信息：" + stu.getId() + "  " + stu.getName() + "  " + stu.isHigh() + "  " + stu.getTimeDouble() + "  " + stu.getTimeFloat() + "  " + stu.getTimeLong());
        }
        Teacher teacher = new Teacher();
        teacher.setId(2);
        teacher.setName("教师1");
        teacher.save(this);
        List<Teacher> listTeacher = teacher.getDatas(this, Teacher.class);
        for (int i = 0; i < listTeacher.size(); i++) {
            Teacher teach = listTeacher.get(i);
            Log.i("info", "信息：" + teach.getId() + "  " + teach.getName());
        }
        List<Student> listStu = SmartSQLite.getInstance(this).queryDatas(Student.class, "id", "0");
        SmartSQLite.getInstance(this).queryBlurryDatas(Student.class, "name", "名字");
        SmartSQLite.getInstance(this).queryPagingDatas(Student.class, new String[]{"name"}, new String[]{"名字"}, 0, 10);
        List<Teacher> listTea = SmartSQLite.getInstance(this).queryBlurryPagingDatas(Teacher.class, "name", "教师", 0, 10);
        for (int i = 0; i < listTea.size(); i++) {
            Teacher teach = listTea.get(i);
            Log.i("info", "信息分页：" + teach.getId() + "  " + teach.getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
