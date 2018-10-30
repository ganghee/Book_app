package com.mobitant.bookapp.best;

import com.google.gson.annotations.SerializedName;

/**
 * 도서 순위 정보를 저장하는 객체
 */
@org.parceler.Parcel
public class BestItem {
    public int seq;
    public String term;
    public String kind;
    @SerializedName("book_rank") public int rank;
    @SerializedName("book_title") public String bookName;
    @SerializedName("book_author") public String author;
    @SerializedName("book_url") public String url;

    @Override
    public String toString() {
        return "BookInfoItem{" +
                "seq=" + seq +
                ", term='" + term + '\'' +
                ", kind='" + kind + '\'' +
                ", book_rank='" + rank + '\'' +
                ", book_title='" + bookName + '\'' +
                ", book_author='" + author + '\'' +
                ", book_url='" + url + '\'' +
                '}';
    }

}
