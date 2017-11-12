package com.gyf.wyf;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by fuchuanyang@gmail.com on 8/2/16.
 */
public class JavaScriptObject {

    public interface OnCallAppListener {
        void callAppFunction(String cmd,String data);
    }

    private Context mContxt;

    private OnCallAppListener onCallAppListener;

    public PhotoCmd photoCmd;

    public JavaScriptObject(Context mContxt) {
        this.mContxt = mContxt;
    }

    @JavascriptInterface
    public void callAppFunction(String cmd, String data) {
        Log.w("YP", "callAppFunction cmd=" + cmd + ";data=" + data);
        this.photoCmd = new PhotoCmd(cmd, data);
        if (onCallAppListener != null) {
            onCallAppListener.callAppFunction(cmd, data);
        }
    }

    public OnCallAppListener getOnCallAppListener() {
        return onCallAppListener;
    }

    public void setOnCallAppListener(OnCallAppListener onCallAppListener) {
        this.onCallAppListener = onCallAppListener;
    }

}
