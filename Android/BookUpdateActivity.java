package com.mobitant.bookapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobitant.bookapp.item.BookInfoItem;
import com.mobitant.bookapp.lib.BitmapLib;
import com.mobitant.bookapp.lib.FileLib;
import com.mobitant.bookapp.lib.GoLib;
import com.mobitant.bookapp.lib.MyLog;
import com.mobitant.bookapp.lib.MyToast;
import com.mobitant.bookapp.lib.RemoteLib;
import com.mobitant.bookapp.lib.StringLib;
import com.mobitant.bookapp.remote.RemoteService;
import com.mobitant.bookapp.remote.ServiceGenerator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookUpdateActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = this.getClass().getSimpleName();
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    boolean isSavingImage = false;
    EditText book_name;
    EditText book_publisher;
    EditText book_description;
    Button btn_cancel;
    Button btn_update;
    TextView currentLength;
    ImageView imageRegister;
    BookInfoItem bookinfoItem;
    Context context;
    File imageFile;
    String imageFilename;
    ImageView infoImage;
    int memberSeq;
    int seq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_update);

        context=this;
        memberSeq = ((MyApp)getApplication()).getMemberSeq();

        Intent intent = getIntent();
        bookinfoItem = new BookInfoItem();

        seq = intent.getIntExtra("book_seq",bookinfoItem.seq);
        String name = intent.getStringExtra("book_name");
        String publisher = intent.getStringExtra("book_publisher");
        String description = intent.getStringExtra("book_description");
        String image = intent.getStringExtra("image");

        book_name = (EditText) findViewById(R.id.edit_name);
        book_publisher = (EditText) findViewById(R.id.edit_publisher);
        book_description = (EditText) findViewById(R.id.edit_description);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_update = (Button) findViewById(R.id.btn_update);
        currentLength = (TextView) findViewById(R.id.text_cnt);
        imageRegister = (ImageView) findViewById(R.id.book_image_register);
        infoImage = (ImageView) findViewById(R.id.imageView);

        book_name.setText(name);
        book_publisher.setText(publisher);
        book_description.setText(description);
        Picasso.with(context)
                .load(Uri.parse(image))
                .resize(1000,1000)
                .centerInside()
                .error(R.drawable.book_default) // default image to load
                .into(infoImage);

        imageRegister = (ImageView) findViewById(R.id.book_image_register);
        imageRegister.setOnClickListener((View.OnClickListener) this);
        btn_cancel.setOnClickListener((View.OnClickListener) this);
        btn_update.setOnClickListener((View.OnClickListener) this);

        book_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                currentLength.setText("글자수:"+String.valueOf(s.length()));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentLength.setText("글자수:"+String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setToolbar();
        setBookImageFile();

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
            actionBar.setTitle(R.string.book_update);
        }
    }
    /**
     * 오른쪽 상단 메뉴를 구성한다.
     * 닫기 메뉴만이 설정되어 있는 menu_close.xml를 지정한다.
     *
     * @param menu 메뉴 객체
     * @return 메뉴를 보여준다면 true, 보여주지 않는다면 false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_close, menu);
        return true;
    }
    /**
     * 왼쪽 화살표 메뉴(android.R.id.home)를 클릭했을 때와
     * 오른쪽 상단 닫기 메뉴를 클릭했을 때의 동작을 지정한다.
     * 여기서는 모든 버튼이 액티비티를 종료한다.
     *
     * @param item 메뉴 아이템 객체
     * @return 메뉴를 처리했다면 true, 그렇지 않다면 false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_close:
                finish();
                break;
        }

        return true;
    }
    /**
     * 이미지를 촬영하고 그 결과를 받을 수 있는 액티비티를 시작한다.
     */
    private void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범으로부터 이미지를 선택할 수 있는 액티비티를 시작한다.
     */
    private void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onClick(View v) {
        bookinfoItem.name = book_name.getText().toString();
        bookinfoItem.publisher = book_publisher.getText().toString();
        bookinfoItem.description = book_description.getText().toString();
        bookinfoItem.memberSeq = memberSeq;
        bookinfoItem.seq = seq;

        if (v.getId() == R.id.btn_cancel) {
            finish();
        } else if (v.getId() == R.id.btn_update) {
            MyLog.d(TAG, "-------------onClick()---------------"+imageFile.length());
            save();
        } else if (v.getId() == R.id.book_image_register) {
            showImageDialog(context);
        }
    }
    /**
     * 사용자가 입력한 정보를 확인하고 저장한다.
     */
    private void save() {
        if (StringLib.getInstance().isBlank(bookinfoItem.name)) {
            MyToast.s(context, "책 이름을 입력하세요");
            return;
        }

        if (StringLib.getInstance().isBlank(bookinfoItem.publisher)) {
            MyToast.s(context, "출판사를 입력하세요");
            return;
        }
        MyLog.d(TAG, "-------------save()---------------"+imageFile.length());
        updateBookInfo();
    }
    /**
     * 사용자가 입력한 정보를 서버에 저장한다.
     */
    private void updateBookInfo() {
        MyLog.d(TAG, "서버로 전송할 데이터 "+bookinfoItem.toString());

        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);
        MyLog.d(TAG, "" +"RemoteService 객체 만들기 성공");
        Call<String> call = remoteService.updateBookInfo(bookinfoItem);
        MyLog.d(TAG, "" +"Call 객체 만들기 성공");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    MyLog.d(TAG, "서버 응답 성공 ");
                    String seqString = response.body();
                    try {
                        bookinfoItem.seq = Integer.parseInt(seqString);
                        MyLog.d(TAG, "서버 응답 성공@@@@@@ "+ bookinfoItem.seq);

                        GoLib.getInstance().goMainActivity(context);

                    } catch (Exception e) {
                        MyLog.d(TAG,"도서 수정 실패");
                        return;
                    }
                    MyLog.d(TAG, "--------------insertBookInfo()--------------"+imageFile.length());
                    saveImage();

                } else { // 등록 실패
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    MyLog.d(TAG, "fail " + statusCode + errorBody.toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity");
            }
        });
    }
    /**
     * 사용자가 선택한 도서 이미지의 파일을 설정한다.
     * 파일 이름을 설정하고 내용은 아직 설정하지 않는다.
     */
    private void setBookImageFile() {
        imageFilename = String.valueOf(System.currentTimeMillis());
        MyLog.d(TAG, "imageFileNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNName " +
                imageFilename);
        imageFile = FileLib.getInstance().getImageFile(context, imageFilename);
        MyLog.d(TAG, "imageFile5555555555555555555555555555555" + imageFile);
        MyLog.d(TAG, "imageFile.length() " + imageFile.length());
    }
    /**
     * 다른 액티비티를 실행한 결과를 처리하는 메소드
     * @param requestCode 액티비티를 실행하면서 전달한 요청 코드
     * @param resultCode 실행한 액티비티가 설정한 결과 코드
     * @param data 결과 데이터
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FROM_CAMERA) {
                Picasso.with(context).load(imageFile).fit().centerInside().into(infoImage);

            } else if (requestCode == PICK_FROM_ALBUM && data != null) {
                Uri dataUri = data.getData();

                if (dataUri != null) {
                    Picasso.with(context).load(dataUri).fit().centerInside().into(infoImage);

                    //Target 인터페이스를 이용해서 SD카드에 이미지 저장하기
                    Picasso.with(context).load(dataUri).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            BitmapLib.getInstance().saveBitmapToFileThread(imageUploadHandler,
                                    imageFile, bitmap);
                            isSavingImage = true;
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
                }
            }
        }
    }
    /**
     * 이미지를 서버에 업로드한다.
     */
    private void saveImage() {
        if (isSavingImage) {
            MyToast.s(context, "이미지를 준비중입니다. 잠시 후에 다시 시도해주세요");
            return;
        }
        if (imageFile.length() == 0) {
            MyToast.s(context, "이미지를 선택해주세요");
            return;
        }

        RemoteLib.getInstance().uploadBookImage(bookinfoItem.seq, imageFile, finishHandler);

        bookinfoItem.fileName = imageFilename + ".png";
        isSavingImage = false;
    }
    /**
     * 이미지를 어떤 방식으로 선택할지에 대해 다이얼로그를 보여준다.
     * @param context 컨텍스트 객체
     */
    public void showImageDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.title_book_image_register)
                .setSingleChoiceItems(R.array.camera_album_category, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    getImageFromCamera();
                                } else {
                                    getImageFromAlbum();
                                }

                                dialog.dismiss();
                            }
                        }).show();
    }
    Handler imageUploadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isSavingImage = false;
            Picasso.with(context).invalidate(RemoteService.IMAGE_URL + bookinfoItem.fileName);
        }
    };

    Handler finishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            finish();
        }
    };
}
