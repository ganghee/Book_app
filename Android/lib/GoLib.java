package com.mobitant.bookapp.lib;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mobitant.bookapp.best.BestActivity;
import com.mobitant.bookapp.BookInfoActivity;
import com.mobitant.bookapp.BookRegisterActivity;
import com.mobitant.bookapp.BookUpdateActivity;
import com.mobitant.bookapp.MainActivity;
import com.mobitant.bookapp.ProfileActivity;

import java.io.Serializable;

/**
 * 액티비티나 프래그먼트 실행 라이브러리
 */
public class GoLib {
    public final String TAG = GoLib.class.getSimpleName();
    private volatile static GoLib instance;

    public static GoLib getInstance() {
        if (instance == null) {
            synchronized (GoLib.class) {
                if (instance == null) {
                    instance = new GoLib();
                }
            }
        }
        return instance;
    }

    /**
     * 프래그먼트를 보여준다.
     * @param fragmentManager 프래그먼트 매니저
     * @param containerViewId 프래그먼트를 보여줄 컨테이너 뷰 아이디
     * @param fragment 프래그먼트
     */
    public void goFragment(FragmentManager fragmentManager, int containerViewId,
                           Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(containerViewId, fragment)
                .commit();
    }

    /**
     * 뒤로가기를 할 수 있는 프래그먼트를 보여준다.
     * @param fragmentManager 프래그먼트 매니저
     * @param containerViewId 프래그먼트를 보여줄 컨테이너 뷰 아이디
     * @param fragment 프래그먼트
     */
    public void goFragmentBack(FragmentManager fragmentManager, int containerViewId,
                               Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(containerViewId, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * 이전 프래그먼트를 보여준다.
     * @param fragmentManager 프래그먼트 매니저
     */
    public void goBackFragment(FragmentManager fragmentManager) {
        fragmentManager.popBackStack();
    }

    /**
     * 프로파일 액티비티를 실행한다.
     * @param context 컨텍스트
     */
    public void goProfileActivity(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 도서 정보 등록 액티비티를 실행한다.
     * @param context 컨텍스트
     */
    public void goBookRegisterActivity(Context context) {
        Intent intent = new Intent(context, BookRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 종류별 순위 도서 액티비티를 실행한다.
     * @param context 컨텍스트
     */
    public void goBestActivity(Context context) {
        Intent intent = new Intent(context, BestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 도서 정보 액티비티를 실행한다.
     * @param context 컨텍스트
     * @param infoSeq 도서 정보 일련번호
     */
    public void goBookInfoActivity(Context context, int infoSeq) {
        Intent intent = new Intent(context, BookInfoActivity.class);
        intent.putExtra(BookInfoActivity.INFO_SEQ, infoSeq);
        context.startActivity(intent);
    }

    public void goBookUpdateActivity(Context context, int infoSeq, String name,
                                     String publisher, String description, String image) {
        Intent intent = new Intent(context, BookUpdateActivity.class);
        intent.putExtra("book_seq", infoSeq);
        intent.putExtra("book_name", (Serializable) name);
        intent.putExtra("book_publisher", (Serializable) publisher);
        intent.putExtra("book_description", (Serializable) description);
        intent.putExtra("image",image);
        context.startActivity(intent);
    }
    /**
     * 메인 액티비티를 실행한다.
     * @param context 컨텍스트
     */
    public void goMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
