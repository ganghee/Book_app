package com.mobitant.bookapp.item;

import com.google.gson.annotations.SerializedName;

/**
 * 도서 정보를 저장하는 객체
 */
@org.parceler.Parcel
public class BookInfoItem {
    public int seq;
    @SerializedName("member_seq") public int memberSeq;
    public String name;
    public String publisher;
    public String description;
    public int keep_cnt;
    @SerializedName("book_info_filename") public String fileName;
    @SerializedName("reg_date") public String regDate;
    @SerializedName("mod_date") public String modDate;
    @SerializedName("is_keep") public boolean isKeep;

    @Override
    public String toString() {
        return "BookInfoItem{" +
                "seq=" + seq +
                ", memberSeq=" + memberSeq +
                ", name='" + name + '\'' +
                ", publisher='" + publisher + '\'' +
                ", description='" + description + '\'' +
                ", keep_cnt=' " + keep_cnt + '\'' +
                ", book_info_filename='" + fileName + '\'' +
                ", regDate='" + regDate + '\'' +
                ", modDate='" + modDate + '\'' +
                ", isKeep=" + isKeep +
                '}';
    }

    public int keep_cnt() {
        return keep_cnt;
    }
}
