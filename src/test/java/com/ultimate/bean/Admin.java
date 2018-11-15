package com.ultimate.bean;

import com.ultimate.annotation.Column;
import com.ultimate.annotation.Exclude;
import com.ultimate.annotation.Primary;
import com.ultimate.annotation.Table;

import java.util.Date;

@Table("rh_admin")
public class Admin{

    @Primary
    private int id;

    @Column
    private String message;

    @Column("create_date")
    private Date date;

//    @Exclude
    private String test;

    @Override
    public String toString(){

        return "Admin{" + "id=" + id + ", message='" + message + '\'' + ", test='" + test + '\'' + '}';
    }

    public int getId(){

        return id;
    }

    public void setId(int id){

        this.id = id;
    }

    public String getMessage(){

        return message;
    }

    public void setMessage(String message){

        this.message = message;
    }

    public String getTest(){

        return test;
    }

    public void setTest(String test){

        this.test = test;
    }
}
