package com.example.hoang.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Gift implements Parcelable {
    private String gifId;
    private String gifName;
    private String giftContent;

    protected Gift(Parcel in) {
        gifId = in.readString();
        gifName = in.readString();
        giftContent = in.readString();
        giftLink = in.readString();
        giftTime = in.readLong();
        imgUrl = in.readString();
        gifExpire = in.readLong();
    }

    public Gift() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gifId);
        dest.writeString(gifName);
        dest.writeString(giftContent);
        dest.writeString(giftLink);
        dest.writeLong(giftTime);
        dest.writeString(imgUrl);
        dest.writeLong(gifExpire);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Gift> CREATOR = new Creator<Gift>() {
        @Override
        public Gift createFromParcel(Parcel in) {
            return new Gift(in);
        }

        @Override
        public Gift[] newArray(int size) {
            return new Gift[size];
        }
    };

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    private String giftLink;
    private long giftTime;
    private String imgUrl;

    public String getGifId() {
        return gifId;
    }

    public void setGifId(String gifId) {
        this.gifId = gifId;
    }

    public String getGifName() {
        return gifName;
    }

    public void setGifName(String gifName) {
        this.gifName = gifName;
    }

    public String getGiftContent() {
        return giftContent;
    }

    public void setGiftContent(String giftContent) {
        this.giftContent = giftContent;
    }

    public String getGiftLink() {
        return giftLink;
    }

    public void setGiftLink(String giftLink) {
        this.giftLink = giftLink;
    }

    public long getGiftTime() {
        return giftTime;
    }

    public void setGiftTime(long giftTime) {
        this.giftTime = giftTime;
    }

    public long getGifExpire() {
        return gifExpire;
    }

    public void setGifExpire(long gifExpire) {
        this.gifExpire = gifExpire;
    }

    private long gifExpire;

}
