package org.jesusgift.clienttest.Model;/* *
 * Developed By : Victor Vincent
 * Created On : 09/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

public class ImageHolder {
    public Map<String, RequestBody> requestBodyMap;
    public List<String> idList;

    public ImageHolder(Map<String, RequestBody> requestBodyMap) {
        this.requestBodyMap = requestBodyMap;
    }

    public ImageHolder(Map<String, RequestBody> requestBodyMap, List<String> idList) {
        this.requestBodyMap = requestBodyMap;
        this.idList = idList;
    }
}
