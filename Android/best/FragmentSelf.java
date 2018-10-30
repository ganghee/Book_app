package com.mobitant.bookapp.best;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobitant.bookapp.MyApp;
import com.mobitant.bookapp.R;
import com.mobitant.bookapp.lib.MyLog;
import com.mobitant.bookapp.remote.RemoteService;
import com.mobitant.bookapp.remote.ServiceGenerator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSelf extends Fragment {

    View v;
    private RecyclerView myrecyclerview;
    RecyclerViewAdapter recyclerViewAdapter;
    ViewPagerAdapter viewPagerAdapter;
    String term;
    String kind;
    private final String TAG = this.getClass().getSimpleName();
    public FragmentSelf() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_best,container,false);
        myrecyclerview = (RecyclerView) v.findViewById(R.id.container_recycler);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),new ArrayList<BestItem>());
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerViewAdapter);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();

        MyApp myApp = ((MyApp) getActivity().getApplication());
        BestItem currentInfoItem = myApp.getBestItem();

        if (recyclerViewAdapter != null && currentInfoItem != null) {
            recyclerViewAdapter.setItem(currentInfoItem);
            myApp.setBestItem(null);
        }





        term = "month";
        kind = "self";
        bestInfo(term, kind);

    }

    /**
     * 서버에서 도서 정보를 조회한다.
     * @param term 순위 기간
     * @param kind 순위 종류
     */
    private void bestInfo(String term, String kind) {
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);

        Call<ArrayList<BestItem>> call = remoteService.BestInfo(term, kind);
        call.enqueue(new Callback<ArrayList<BestItem>>() {
            @Override
            public void onResponse(Call<ArrayList<BestItem>> call,
                                   Response<ArrayList<BestItem>> response) {
                ArrayList<BestItem> list = response.body();

                if (response.isSuccessful() && list != null) {
                    MyLog.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  "+list);
                    MyLog.d(TAG,"******************************************* "+recyclerViewAdapter);
                    recyclerViewAdapter.addItemList(list);


                }
            }

            @Override
            public void onFailure(Call<ArrayList<BestItem>> call, Throwable t) {

            }
        });
    }
}
