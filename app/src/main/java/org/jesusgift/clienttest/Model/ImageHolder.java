package org.jesusgift.clienttest.Model;/* *
 * Developed By : Victor Vincent
 * Created On : 09/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import java.util.Map;

import okhttp3.RequestBody;

public class ImageHolder {
    public ImageHolder(Map<String, RequestBody> requestBodyMap) {
        this.requestBodyMap = requestBodyMap;
    }

    public Map<String, RequestBody> requestBodyMap;
}
