package org.jesusgift.clienttest.Model;/* *
 * Developed By : Victor Vincent
 * Created On : 09/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @Expose
    private String status;

    @Expose
    private String msg;

    @SerializedName("data")
    @Expose
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public String getStatus() {
        return status;
    }
}
