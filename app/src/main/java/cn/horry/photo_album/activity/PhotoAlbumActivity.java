package cn.horry.photo_album.activity;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.http.HttpCallBack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.horry.photo_album.API.HttpApi;
import cn.horry.photo_album.R;
import cn.horry.photo_album.adapter.SamplePagerAdapter;
import cn.horry.photo_album.entity.step;
import cn.horry.photo_album.utils.ImageUrlCache;
import cn.horry.photo_album.widget.myViewPager;

public class PhotoAlbumActivity extends AppCompatActivity {
    private final static String ALBUM_PATH
            = Environment.getExternalStorageDirectory() + "/Album/";
    private String mSaveMessage;
    private Bitmap mBitmap;
    private String mFileName;
    private int index=0;
    private List<step> steps = new ArrayList<step>();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==-1)
            {
                initView();
            }
        }
    };
    private myViewPager viewPager;
    private TextView page;
    private View download;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);
        initData();
    }
    private void initData(){
        HttpApi.getData(new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    steps = new Gson().fromJson(jsonObject.getJSONObject("data").getString("steps"), new TypeToken<List<step>>() {
                    }.getType());
                    handler.sendEmptyMessage(-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }

    private void initView(){
        viewPager = (myViewPager) findViewById(R.id.view_pager);
        page = (TextView) findViewById(R.id.page);
        download = findViewById(R.id.download);
        if (steps.size() < 2) {
            page.setVisibility(View.GONE);
        } else {
            page.setText(String.format("%d/%d", index + 1, steps.size()));
        }
        viewPager.setAdapter(new SamplePagerAdapter(this, steps));
        viewPager.setCurrentItem(index);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page.setText(String.format("%d/%d", position + 1, steps.size()));
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmap = ImageUrlCache.getInstence().getBimapWithPath(steps.get(index).getStep_pic());
                mFileName =new Date().getTime()+ ".jpg";
                new Thread(saveFileRunnable).start();
            }
        });

    }
    private Runnable saveFileRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                saveFile(mBitmap, mFileName);
                mSaveMessage = "图片保存成功";
            } catch (IOException e) {
                mSaveMessage = "图片保存失败";
                e.printStackTrace();
            }
            messageHandler.sendEmptyMessage(-1);
        }

    };
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==-1)
            {
                Toast.makeText(PhotoAlbumActivity.this,mSaveMessage,Toast.LENGTH_LONG).show();
            }

        }
    };
}
