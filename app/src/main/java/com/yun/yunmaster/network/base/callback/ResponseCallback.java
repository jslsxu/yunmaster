package com.yun.yunmaster.network.base.callback;

import android.support.annotation.Nullable;

import com.yun.yunmaster.network.base.response.BaseObject;
import com.yun.yunmaster.network.base.response.BaseResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;

/**
 * Created by jslsxu on 2018/3/24.
 */

public abstract class ResponseCallback<T> {
    protected Request mRequest;

    public ResponseCallback() {

    }

    /**
     * 请求成功
     *
     * @param baseData 已解析的数据
     */
    public abstract void onSuccess(T baseData);


    /**
     * 请求失败
     * 可能是业务逻辑的失败，或者是http请求失败
     *
     * @param statusCode 0为无需特别处理。
     * @param failDate   失败的信息
     * @param error      具体的错误
     */
    public abstract void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error);

    /**
     * 通过反射获取包含具体泛型对象的Class<br>
     * 可以省去再次传递class的过程
     * 除非需要Gson解析的Class与GSON_TYPE不同，否则请不要重写此类
     *
     * @return Type
     */
    public Type getClazz() {
        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            if (type != null) {
                return type;
            } else {
                return BaseObject.class;
            }
        } catch (Exception e) {
            return BaseObject.class;
        }
    }

    public Request getRequest() {
        return mRequest;
    }

    public void setRequest(Request request) {
        this.mRequest = request;
    }
}

