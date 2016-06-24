package org.jesusgift.clienttest.Model;/* *
 * Developed By : Victor Vincent
 * Created On : 23/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

public class MyResponse {

    private int status;
    private String msg;
    private Data data;

    public class Data {
        private String api_url;
        private int load_type;

        public String getApi_url() {
            return api_url;
        }

        public int getLoad_type() {
            return load_type;
        }
    }

    public Data getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }
}
