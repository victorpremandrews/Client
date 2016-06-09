package org.jesusgift.clienttest.Model;/* *
 * Developed By : Victor Vincent
 * Created On : 07/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("age")
    @Expose
    private String age;

    @SerializedName("sex")
    @Expose
    private String sex;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("father")
    @Expose
    private String father;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
