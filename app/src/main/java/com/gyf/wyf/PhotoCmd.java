package com.gyf.wyf;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by fuchuanyang@gmail.com on 8/3/16.
 */
public class PhotoCmd implements Parcelable , Serializable {

    public String cmd;
    public String data;
    public String imageBase64;
    private String url;

    public PhotoCmd() {
    }

    public PhotoCmd(String cmd, String data) {
        this.cmd = cmd;
        this.data = data;
    }


    public String getUrl() {

        if (url != null) {
            return url;
        }

        if (data == null) {
            return null;
        }

        String[] split = data.split("&=&");

        if (split.length > 1) {
            return split[1];
        }
        return null;
    }

    public String getRealData() {

        if (data == null) {
            return null;
        }

        String[] split = data.split("&=&");
        if (split.length > 0) {
            return split[0];
        }
        return null;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cmd);
        dest.writeString(this.data);
        dest.writeString(this.imageBase64);
        dest.writeString(this.url);
    }

    protected PhotoCmd(Parcel in) {
        this.cmd = in.readString();
        this.data = in.readString();
        this.imageBase64 = in.readString();
        this.url = in.readString();
    }

    public static final Creator<PhotoCmd> CREATOR = new Creator<PhotoCmd>() {
        @Override
        public PhotoCmd createFromParcel(Parcel source) {
            return new PhotoCmd(source);
        }

        @Override
        public PhotoCmd[] newArray(int size) {
            return new PhotoCmd[size];
        }
    };

    @Override
    public String toString() {
        return "PhotoCmd{" +
                "cmd='" + cmd + '\'' +
                ", data='" + data + '\'' +
                ", imageBase64='" + imageBase64 + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
