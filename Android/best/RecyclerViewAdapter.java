package com.mobitant.bookapp.best;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobitant.bookapp.R;
import com.mobitant.bookapp.lib.MyLog;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    Intent intent;
    Context mContext ;
    private ArrayList<BestItem> itemList;

    public RecyclerViewAdapter(Context mContext, ArrayList<BestItem> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v ;
        v = LayoutInflater.from(mContext).inflate(R.layout.fragment_best_item,parent,false);
        MyViewHolder vHolder = new MyViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final BestItem item = itemList.get(position);
        MyLog.d(TAG, "getView " + item);

        holder.rank.setText(item.rank+"");
        holder.bookName.setText(item.bookName);
        holder.author.setText(item.author);

        holder.bookName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(item.url));
                mContext.startActivity(intent);
            }
        });
    }
    /**
     * @param newItem 새로운 아이템
     */
    public void setItem(BestItem newItem) {
        for (int i = 0; i < itemList.size(); i++) {
            BestItem item = itemList.get(i);

            if (item.rank == newItem.rank && item.kind == newItem.kind) {
                itemList.set(i, newItem);
                notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * 현재 아이템 리스트에 새로운 아이템 리스트를 추가한다.
     * @param itemList 새로운 아이템 리스트
     */
    public void addItemList(ArrayList<BestItem> itemList) {
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        MyLog.d(TAG,"-----------------this.itemList.size()---------------"+this.itemList.size());
        return this.itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView rank;
        TextView bookName;
        TextView author;
        LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.rank);
            bookName = itemView.findViewById(R.id.bookName);
            author = itemView.findViewById(R.id.author);
            linearLayout = itemView.findViewById(R.id.linerLayout);
        }
    }

}
