package com.mobitant.bookapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobitant.bookapp.adapter.InfoListAdapter;
import com.mobitant.bookapp.custom.EndlessRecyclerViewScrollListener;
import com.mobitant.bookapp.item.BookInfoItem;
import com.mobitant.bookapp.lib.GoLib;
import com.mobitant.bookapp.lib.MyLog;
import com.mobitant.bookapp.remote.RemoteService;
import com.mobitant.bookapp.remote.ServiceGenerator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * 도서 정보를 보여주는 프레그먼트
 *
 */
public class BookListFragment extends Fragment implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    Context context;

    int memberSeq;

    RecyclerView bookList;
    TextView noDataText;

    TextView orderFavorite;
    TextView orderRecent;
    TextView orderKind;

    ImageView listType;

    InfoListAdapter infoListAdapter;
    StaggeredGridLayoutManager layoutManager;
    EndlessRecyclerViewScrollListener scrollListener;

    int listTypeValue = 2;
    String orderType = Constant.ORDER_TYPE_FAVORITE;
    /**
     * BookListFragment 인스턴스를 생성한다.
     * @return BookListFragment 인스턴스
     */
    public static BookListFragment newInstance() {
        BookListFragment f = new BookListFragment();
        return f;
    }
    /**
     * fragment_book_list.xml 기반으로 뷰를 생성한다.
     * @param inflater XML를 객체로 변환하는 LayoutInflater 객체
     * @param container null이 아니라면 부모 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     * @return 생성한 뷰 객체
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"************************onCreateView*********************");
        context = this.getActivity();
        memberSeq = ((MyApp)this.getActivity().getApplication()).getMemberSeq();
        View layout = inflater.inflate(R.layout.fragment_book_list, container, false);

        return layout;
    }
    /**
     * onCreateView() 메소드 뒤에 호출되며 화면 뷰들을 설정한다.
     * @param view onCreateView() 메소드에 의해 반환된 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"************************onViewCreate*********************");
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.nav_list);

        bookList = (RecyclerView) view.findViewById(R.id.list);
        noDataText = (TextView) view.findViewById(R.id.no_data);
        listType = (ImageView) view.findViewById(R.id.list_type);

        orderKind = (TextView) view.findViewById(R.id.order_kind);
        orderFavorite = (TextView) view.findViewById(R.id.order_favorite);
        orderRecent = (TextView) view.findViewById(R.id.order_recent);

        orderFavorite.setOnClickListener(this);
        orderRecent.setOnClickListener(this);
        orderKind.setOnClickListener(this);
        listType.setOnClickListener(this);


    }
    /**
     * 프래그먼트가 일시 중지 상태가 되었다가 다시 보여질 때 호출된다.
     * BookInfoActivity가 실행된 후,
     * 즐겨찾기 상태가 변경되었을 경우 이를 반영하는 용도로 사용한다.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"************************onResume*********************");

        MyApp myApp = ((MyApp) getActivity().getApplication());
        BookInfoItem currentInfoItem = myApp.getBookInfoItem();

        if (infoListAdapter != null && currentInfoItem != null) {
            infoListAdapter.setItem(currentInfoItem);
            myApp.setBookInfoItem(null);
        }
        if(orderFavorite.getTextColors().equals(R.color.bg_white)){
            orderType = Constant.ORDER_TYPE_FAVORITE;
        }else if(orderRecent.getTextColors().equals(R.color.bg_white)){
            orderType = Constant.ORDER_TYPE_RECENT;
        }
        setRecyclerView();
        listInfo(memberSeq, orderType, 0);
    }

    /**
     * 도서 정보를 스태거드그리드레이아웃으로 보여주도록 설정한다.
     * @param row 스태거드그리드레이아웃에 사용할 열의 개수
     */
    private void setLayoutManager(int row) {
        layoutManager = new StaggeredGridLayoutManager(row, StaggeredGridLayoutManager.VERTICAL);
        layoutManager
                .setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        bookList.setLayoutManager(layoutManager);
    }
    /**
     * 리사이클러뷰를 객체에 어댑터 뷰 설정
     * 스크롤 리스너를 추가한다.
     */
    private void setRecyclerView() {
        setLayoutManager(listTypeValue);

        infoListAdapter = new InfoListAdapter(context,
                R.layout.row_book_list, new ArrayList<BookInfoItem>(),orderType);
        bookList.setAdapter(infoListAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                listInfo(memberSeq, orderType, page);
            }
        };
        bookList.addOnScrollListener(scrollListener);
    }
    /**
     * 서버에서 도서 정보를 조회한다.
     * @param memberSeq 사용자 시퀀스
     * @param orderType 도서 정보 정렬 순서
     * @param currentPage 현재 페이지
     */
    private void listInfo(int memberSeq, final String orderType, final int currentPage) {
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);

        Call<ArrayList<BookInfoItem>> call = remoteService.listBookInfo(memberSeq,orderType, currentPage);
        call.enqueue(new Callback<ArrayList<BookInfoItem>>() {
            @Override
            public void onResponse(Call<ArrayList<BookInfoItem>> call,
                                   Response<ArrayList<BookInfoItem>> response) {
                ArrayList<BookInfoItem> list = response.body();

                if (response.isSuccessful() && list != null) {
                    MyLog.d(TAG,"**********************infoListAdapter********************* "+infoListAdapter);
                    infoListAdapter.addItemList(list);

                    if (infoListAdapter.getItemCount() == 0) {
                        noDataText.setVisibility(View.VISIBLE);
                    } else {
                        noDataText.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BookInfoItem>> call, Throwable t) {

            }
        });
    }
    /**
     * 각종 버튼에 대한 클릭 처리를 정의한다.
     * @param v 클릭한 뷰에 대한 정보
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.list_type) {
            changeListType();

        } else {
            if (v.getId() == R.id.order_favorite) {
                orderType = Constant.ORDER_TYPE_FAVORITE;
                setOrderBackground(R.drawable.bg_round_top,
                        R.color.transparent, R.color.transparent);
                setOrderTextColor(R.color.bg_white,
                        R.color.text_color_black, R.color.text_color_black);

            } else if (v.getId() == R.id.order_recent) {
                orderType = Constant.ORDER_TYPE_RECENT;
                setOrderBackground(R.color.transparent,
                        R.drawable.bg_round_top, R.color.transparent);
                setOrderTextColor(R.color.text_color_black,
                        R.color.bg_white, R.color.text_color_black);

            } else if (v.getId() == R.id.order_kind) {

                GoLib.getInstance().goBestActivity(context);
            }

            setRecyclerView();
            listInfo(memberSeq, orderType, 0);
        }
    }
    /**
     * 도서 정보 정렬 방식의 텍스트 색상을 설정한다.
     * @param color1 인기순 색상
     * @param color2 최근순 색상
     * @param color3 종류별 색상
     */
    private void setOrderTextColor(int color1, int color2, int color3) {
        orderFavorite.setTextColor(ContextCompat.getColor(context, color1));
        orderRecent.setTextColor(ContextCompat.getColor(context, color2));
        orderKind.setTextColor(ContextCompat.getColor(context, color3));
    }
    private void setOrderBackground(int color1, int color2, int color3){
        orderFavorite.setBackground(getResources().getDrawable(color1,null));
        orderRecent.setBackground(getResources().getDrawable(color2,null));
        orderKind.setBackground(getResources().getDrawable(color3,null));
    }
    /**
     * 리사이클러뷰의 리스트 형태를 변경한다.
     */
    private void changeListType() {
        if (listTypeValue == 1) {
            listTypeValue = 2;
            listType.setImageResource(R.drawable.ic_list2);
        } else {
            listTypeValue = 1;
            listType.setImageResource(R.drawable.ic_list3);

        }
        setLayoutManager(listTypeValue);
    }
}
