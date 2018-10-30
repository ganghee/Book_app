package com.mobitant.bookapp.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobitant.bookapp.Constant;
import com.mobitant.bookapp.MyApp;
import com.mobitant.bookapp.R;
import com.mobitant.bookapp.item.BookInfoItem;
import com.mobitant.bookapp.item.MemberInfoItem;
import com.mobitant.bookapp.lib.DialogLib;
import com.mobitant.bookapp.lib.GoLib;
import com.mobitant.bookapp.lib.MyLog;
import com.mobitant.bookapp.lib.StringLib;
import com.mobitant.bookapp.remote.RemoteService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 도서 정보 리스트의 아이템을 처리하는 어댑터
 * 즐겨찾기 기능 설정
 */

public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private final String order_type;
    private Context context;
    private int resource;
    private ArrayList<BookInfoItem> itemList;
    private MemberInfoItem memberInfoItem;
    /**
     * 어댑터 생성자
     */

    public InfoListAdapter(Context context, int resource,
                           ArrayList<BookInfoItem> itemList,String order_type) {
        this.context = context;
        this.resource = resource;
        this.itemList = itemList;
        this.order_type = order_type;
        memberInfoItem = ((MyApp) context.getApplicationContext()).getMemberInfoItem();

    }

    /**
     * 특정 아이템의 변경사항을 적용하기 위해 기본 아이템을 새로운 아이템으로 변경한다.
     * @param newItem 새로운 아이템
     */
    public void setItem(BookInfoItem newItem) {
        for (int i = 0; i < itemList.size(); i++) {
            BookInfoItem item = itemList.get(i);
            if (item.seq == newItem.seq) {
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
    public void addItemList(ArrayList<BookInfoItem> itemList) {
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    /**
     * 즐겨찾기 상태를 변경한다.
     * @param seq 도서 정보 시퀀스
     * @param keep 즐겨찾기 추가 유무
     * itemList를 재정렬 한다.
     */
    private void changeItemKeep(int seq,boolean keep){
        for(int i = 0; i < itemList.size(); i++){
            if(itemList.get(i).seq == seq){
                itemList.get(i).isKeep = keep;
                notifyItemChanged(i);
                break;
            }
        }
    }
   /* private void changeItemKeep(int seq, boolean keep) {
        for (int i=0; i < itemList.size()-1; i++) {
            if (itemList.get(i).seq == seq) {
                itemList.get(i).isKeep = keep;
                if(order_type==Constant.ORDER_TYPE_FAVORITE) {
                        Collections.sort(itemList, new Comparator<BookInfoItem>() {
                            @Override
                            public int compare(BookInfoItem s1, BookInfoItem s2) {
                                if (s1.keep_cnt() < s2.keep_cnt()) {
                                    return -1;
                                } else if (s1.keep_cnt() > s2.keep_cnt()) {
                                    return 1;
                                }
                                return 0;
                            }
                        });
                        Collections.reverse(itemList);
                }
                notifyItemChanged(i);
                break;
            }
        }
    }*/

    /**
     * 아이템 크기를 반환한다.
     * @return 아이템 크기
     */
    @Override
    public int getItemCount() {
MyLog.d(TAG,"--------------this.itemList.size()---------"+this.itemList.size());
        return this.itemList.size();
    }


    /**
     * 아이템을 보여주기 위한 뷰홀더 클래스
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView keep;
        TextView name;
        TextView description;
        ImageView me_image;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            keep = (ImageView) itemView.findViewById(R.id.keep);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            me_image = (ImageView) itemView.findViewById(R.id.me_image);
        }
    }
    /**
     * 뷰홀더(ViewHolder)를 생성하기 위해 자동으로 호출된다.
     * @param parent 부모 뷰그룹
     * @param viewType 새로운 뷰의 뷰타입
     * @return 뷰홀더 객체
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ViewHolder(v);
    }
    /**
     * 뷰홀더(ViewHolder)와 아이템을 리스트 위치에 따라 연동한다.
     * @param holder 뷰홀더 객체
     * @param position 리스트 위치
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BookInfoItem item = itemList.get(position);
        MyLog.d(TAG, "getView " + item);

        if (item.isKeep) {
            holder.keep.setImageResource(R.drawable.ic_keep_on);
        } else {
            holder.keep.setImageResource(R.drawable.ic_keep_off);
        }

        holder.name.setText(item.name);
        holder.description.setText(StringLib.getInstance().getSubString(context,
                item.description, Constant.MAX_LENGTH_DESCRIPTION));

        setImage(holder.image, item.fileName);
        Log.d(TAG,"book_info.member_seq"+item.memberSeq+"book_member.seq"+memberInfoItem.seq+"####################################################");

        if(item.memberSeq==memberInfoItem.seq){
            holder.me_image.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoLib.getInstance().goBookInfoActivity(context, item.seq);
            }
        });

        holder.keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.isKeep) {
                    DialogLib.getInstance().showKeepDeleteDialog(context,
                            keepDeleteHandler, memberInfoItem.seq, item.seq);
                } else {
                    DialogLib.getInstance().showKeepInsertDialog(context,
                            keepInsertHandler, memberInfoItem.seq, item.seq);
                }
            }
        });
    }

    /**
     * 이미지를 설정한다.
     * @param imageView  이미지를 설정할 뷰
     * @param fileName 이미지 파일이름
     */
    private void setImage(ImageView imageView, String fileName) {
        MyLog.d(TAG,"책이름 @@@@@@@@@@@@@@@@@@@@@@@@@"+fileName);
        if (StringLib.getInstance().isBlank(fileName)) {
            Picasso.with(context).load(R.drawable.book_default).into(imageView);
        } else {
            Picasso.with(context).load(RemoteService.IMAGE_URL + fileName).into(imageView);
        }
    }
    /**
     * 즐겨찾기 추가가 성공한 경우를 처리하는 핸들러
     */
    Handler keepInsertHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            changeItemKeep(msg.what, true);
        }
    };

    /**
     * 즐겨찾기 삭제가 성공한 경우를 처리하는 핸들러
     */
    Handler keepDeleteHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            changeItemKeep(msg.what,false);
        }
    };
}
