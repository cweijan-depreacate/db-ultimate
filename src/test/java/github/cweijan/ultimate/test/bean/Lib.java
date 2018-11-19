package github.cweijan.ultimate.test.bean;

import github.cweijan.ultimate.annotation.Primary;
import github.cweijan.ultimate.annotation.Table;

@Table
public class Lib{

    @Primary
    private Integer id;
    private String message;
    private String test;

    public Integer getId(){

        return id;
    }

    public void setId(Integer id){

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
