package com.mobitant.bookapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mobitant.bookapp.item.BookInfoItem;
import com.mobitant.bookapp.lib.DialogLib;
import com.mobitant.bookapp.lib.GoLib;
import com.mobitant.bookapp.lib.MyLog;
import com.mobitant.bookapp.lib.StringLib;
import com.mobitant.bookapp.remote.RemoteService;
import com.mobitant.bookapp.remote.ServiceGenerator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 도서 정보를 보는 액티비티이다.
 */
public class BookInfoActivity extends AppCompatActivity
        implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    public static final String INFO_SEQ = "INFO_SEQ";

    Context context;

    int memberSeq;
    int bookInfoSeq;

    BookInfoItem item;

    View loadingText;
    ScrollView scrollView;
    ImageView keepImage;
    ImageView infoImage;

    private ArrayList<BookInfoItem> itemList;

    /**
     * 도서 정보를 보여주기 위해 사용자 시퀀스와 도서 정보 시퀀스를 얻고
     * 이를 기반으로 서버에서 도서 정보를 조회하는 메소드를 호출한다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        context = this;

        loadingText = findViewById(R.id.loading_layout);

        memberSeq = ((MyApp)getApplication()).getMemberSeq();
        bookInfoSeq = getIntent().getIntExtra(INFO_SEQ, 0);
        selectBookInfo(bookInfoSeq, memberSeq);

        setToolbar();
    }
    /**
     * 툴바를 설정한다.
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
    }

    /**
     * 오른쪽 상단 메뉴를 구성한다.
     * 닫기 메뉴만이 설정되어 있는 menu_close.xml를 지정한다.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_delete, menu);
        return true;
    }

    /**
     * 왼쪽 화살표 메뉴(android.R.id.home)를 클릭했을 때와
     * 오른쪽 상단 닫기 메뉴를 클릭했을 때의 동작을 지정한다.
     * 여기서는 모든 버튼이 액티비티를 종료한다.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home :
                finish();
                break;
            case R.id.action_update :
                String uri = RemoteService.IMAGE_URL + item.fileName;
                GoLib.getInstance().goBookUpdateActivity(context, item.seq,
                        item.name,item.publisher,item.description,uri);
                finish();
                break;
            case R.id.action_delete :
                showInfoDeleteDialog(memberSeq, bookInfoSeq);
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }
    public void showInfoDeleteDialog(final int memberSeq, final int bookInfoSeq) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.info_delete)
                .setMessage(R.string.info_delete_message)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,"memberSeq"+memberSeq+"item.seq"+bookInfoSeq);
                        info_delete(memberSeq,bookInfoSeq);
                    }
                })
                .show();
    }

    private void info_delete(int memberSeq,final int bookInfoSeq){
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);
        Log.d(TAG, "memberSeq" + memberSeq + "item.seq" + bookInfoSeq );
        Call<ArrayList<BookInfoItem>> call = remoteService.deleteBookInfo(memberSeq, bookInfoSeq);
        call.enqueue(new Callback<ArrayList<BookInfoItem>>() {
            @Override
            public void onResponse(Call<ArrayList<BookInfoItem>> call,
                                   Response<ArrayList<BookInfoItem>> response) {
                Log.d(TAG,"response.isSuccessful()"+response.isSuccessful());
                ArrayList<BookInfoItem> list = response.body();
                removeBookItemList(list);
                if (response.isSuccessful()) {
                    MyLog.d(TAG, "deleteBookInfo " + response);


                    //finish();
                } else { // 등록 실패
                    MyLog.d(TAG, "response error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BookInfoItem>> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                finish();
                // GoLib.getInstance().goMainActivity(context);
            }
        });
    }

    /**
     * 도서 정보 시퀀스와 일치하는 아이템을 리스트에서 삭제한다.
     */
    private void removeBookItemList(ArrayList<BookInfoItem> bookInfoItemArrayList) {
        for (int i=0; i < bookInfoItemArrayList.size(); i++) {
            if (bookInfoItemArrayList.get(i).memberSeq == memberSeq) {
                this.itemList.remove(i);
                break;
            }
        }
    }
    /**
     * 서버에서 도서 정보를 조회한다.
     * @param bookInfoSeq 도서 정보 시퀀스
     * @param memberSeq 사용자 시퀀스
     */
    private void selectBookInfo(int bookInfoSeq, int memberSeq) {
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);
        Call<BookInfoItem> call = remoteService.selectBookInfo(bookInfoSeq, memberSeq);

        call.enqueue(new Callback<BookInfoItem>() {
            @Override
            public void onResponse(Call<BookInfoItem> call, Response<BookInfoItem> response) {
                BookInfoItem infoItem = response.body();

                if (response.isSuccessful() && infoItem != null && infoItem.seq > 0) {
                    item = infoItem;
                    setView();
                    loadingText.setVisibility(View.GONE);
                } else {
                    loadingText.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.loading_text)).setText(R.string.loading_not);
                }
            }

            @Override
            public void onFailure(Call<BookInfoItem> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity");
                MyLog.d(TAG, t.toString());
            }
        });
    }
    /**
     * 서버에서 조회한 도서 정보를 화면에 설정한다.
     */
    private void setView() {
        getSupportActionBar().setTitle(item.name);

        infoImage =  findViewById(R.id.info_image);
        setImage(infoImage, item.fileName);


        scrollView = findViewById(R.id.scroll_view);

        TextView nameText =  findViewById(R.id.name);
        if (!StringLib.getInstance().isBlank(item.name)) {
            nameText.setText(item.name);
        }

        keepImage =  findViewById(R.id.keep);
        keepImage.setOnClickListener(this);
        if (item.isKeep) {
            keepImage.setImageResource(R.drawable.ic_keep_on);
        } else {
            keepImage.setImageResource(R.drawable.ic_keep_off);
        }


        TextView publisher = findViewById(R.id.publisher);
        if (!StringLib.getInstance().isBlank(item.publisher)) {
            publisher.setText("출판사: "+item.publisher);
        }

        TextView description = (TextView) findViewById(R.id.description);
        if (!StringLib.getInstance().isBlank(item.description)) {
            description.setText(item.description);
        } else {
            description.setText(R.string.no_text);
        }
    }
    /**
     * 즐겨찾기 버튼과 위치보기 버튼을 클릭했을 때의 동작을 정의한다.
     * @param v 클릭한 뷰에 대한 정보
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.keep) {
            if (item.isKeep) {
                DialogLib.getInstance()
                        .showKeepDeleteDialog(context, keepHandler, memberSeq, item.seq);
                keepImage.setImageResource(R.drawable.ic_keep_off);
            } else {
                DialogLib.getInstance()
                        .showKeepInsertDialog(context, keepHandler, memberSeq, item.seq);
                keepImage.setImageResource(R.drawable.ic_keep_on);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler keepHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            item.isKeep = !item.isKeep;

            if (item.isKeep) {
                keepImage.setImageResource(R.drawable.ic_keep_on);
            } else {
                keepImage.setImageResource(R.drawable.ic_keep_off);
            }
        }
    };
    /**
     * 도서 이미지를 화면에 보여준다.
     * @param imageView 도서 이미지를 보여줄 이미지뷰
     * @param fileName 서버에 저장된 도서 이미지의 파일 이름
     */
    private void setImage(ImageView imageView, String fileName) {
        if (StringLib.getInstance().isBlank(fileName)) {
            Picasso.with(context).load(R.drawable.book_default).into(imageView);
        } else {
            Picasso.with(context).load(RemoteService.IMAGE_URL + fileName).into(imageView);
        }
    }

    /**
     * 화면이 일시정지 상태로 될 때 호출되며 현재 아이템의 변경 사항을 저장한다.
     * 이는 BookListFragment나 BookKeepFragment에서 변경된 즐겨찾기 상태를 반영하는
     * 용로도 사용된다.
     */
    @Override
    protected void onPause() {
        super.onPause();
        ((MyApp) getApplication()).setBookInfoItem(item);
    }
}
