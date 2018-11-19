package github.cweijan.ultimate.test.bean;

import github.cweijan.ultimate.annotation.Column;
import github.cweijan.ultimate.annotation.Primary;
import github.cweijan.ultimate.annotation.Table;

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

    private Lib lib;

    @Override
    public String toString(){

        return "Admin{" + "id=" + id + ", message='" + message + '\'' + ", date=" + date + ", test='" + test + '\'' + '}';
    }

    public Date getDate(){

        return date;
    }

    public void setDate(Date date){

        this.date = date;
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
