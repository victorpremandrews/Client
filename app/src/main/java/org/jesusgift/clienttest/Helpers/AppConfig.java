package org.jesusgift.clienttest.Helpers;/* *
 * Developed By : Victor Vincent
 * Created On : 07/06/16
 * victorvprem@gmail.com
 * Kliotech Pvt Ltd.
 * */

public class AppConfig {

    public static final int DB_VERSION = 5;
    public static final String DB_NAME = "MediaStore.db";
    public static final String API_BASE_URL = "http://50.112.197.157/ClientService/";
    public static boolean IS_SERVICE_RUNNING = false;

    public static final String SERVICE_STOP_BROADCAST = "ClientServiceDestroyedPleaseStartAgain";
    public static final String MESSAGE_BODY = "MESSAGE_BODY";

    public static final String PREF_NAME = "ANON_CLIENT_SERVICE";
    public static final String PREF_LAST_MEDIA_ID = "LATEST_MEDIA_ID";

    //Configurable API Constants
    public static String API_PRIMARY_URL = "";
    public static int API_LOAD_TYPE = 0;


    //Load Types
    public static final int LOAD_LATEST_MEDIA = 0;
    public static final int LOAD_ALL_MEDIA = 1;

}
