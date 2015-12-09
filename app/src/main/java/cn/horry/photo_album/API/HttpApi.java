package cn.horry.photo_album.API;

import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.Request;

/**
 * Created by Administrator on 2015/12/9.
 */
public class HttpApi extends API {
    public static void getData(HttpCallBack httpCallBack){
        builder.httpMethod(Request.HttpMethod.GET).url(test).useCache(true).callback(httpCallBack).request();
    }
}
