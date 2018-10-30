package com.mobitant.bookapp;

import android.app.Application;
import android.os.StrictMode;

import com.mobitant.bookapp.best.BestItem;
import com.mobitant.bookapp.item.BookInfoItem;
import com.mobitant.bookapp.item.MemberInfoItem;

/**
 * 앱 전역에서 사용할 수 있는 클래스
 */
public class MyApp extends Application {
    private MemberInfoItem memberInfoItem;
    private BookInfoItem bookInfoItem;
    private BestItem bestItem;

    @Override
    public void onCreate() {
        super.onCreate();

        // FileUriExposedException 문제를 해결하기 위한 코드
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public MemberInfoItem getMemberInfoItem() {
        if (memberInfoItem == null) memberInfoItem = new MemberInfoItem();

        return memberInfoItem;
    }

    public void setMemberInfoItem(MemberInfoItem item) {
        this.memberInfoItem = item;
    }

    public int getMemberSeq() {
        return memberInfoItem.seq;
    }

    public void setBookInfoItem(BookInfoItem bookInfoItem) {
        this.bookInfoItem = bookInfoItem;
    }

    public void setBestItem(BestItem bestItem) {
        this.bestItem = bestItem;
    }

    public BookInfoItem getBookInfoItem() {
        return bookInfoItem;
    }
    public BestItem getBestItem() {
        return bestItem;
    }
}
