package cn.horry.photo_album;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.Request;
import org.kymjs.kjframe.utils.DensityUtils;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2015/12/8.
 */
public class SamplePagerAdapter extends PagerAdapter {
    private List<step> steps;
    private Activity aty;
    private KJHttp.Builder builder;
    private View[] views = new View[3];
    public SamplePagerAdapter(Activity aty, List<step> steps) {
        this.aty = aty;
        this.steps = steps;
        builder=new KJHttp.Builder();
    }
    @Override
    public int getCount() {
        return steps.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private View getView(int position){
        int i = position%3;
        if(views[i]==null)
        {
            views[i]=View.inflate(aty, R.layout.item_album, null);
        }
        return views[i];
    }
    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View root = View.inflate(aty, R.layout.item_album, null);
        TextView title = (TextView) root.findViewById(R.id.title);
        final PhotoView photoView = (PhotoView) root.findViewById(R.id.image);
        TextView description = (TextView) root.findViewById(R.id.description);
        photoView.setVisibility(View.GONE);
        title.setText(steps.get(position).getTitle());
        Bitmap imageCache=ImageUrlCache.getInstence().getImageCache().get(steps.get(position).getStep_pic());
        if(imageCache!=null){
            displayImage(photoView,imageCache);
        }else
        {
            builder.url(steps.get(position).getStep_pic()).useCache(true).httpMethod(Request.HttpMethod.GET).callback(new HttpCallBack() {
                @Override
                public void onSuccess(byte[] t) {
                    super.onSuccess(t);
                    displayImage(photoView,t,steps.get(position).getStep_pic());
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                }
            }).request();
        }
        description.setText(steps.get(position).getDescription());
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                aty.finish();
            }
        });
        container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return root;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    /**
     * 加载普通图片
     */
    private void displayImage(PhotoView photoView, byte[] res,String path) {
        photoView.setVisibility(View.VISIBLE);

        Bitmap bitmap = byteArrayToBitmap(res);
        if (bitmap == null) {
            photoView.setImageResource(R.mipmap.default_img_rect);
            photoView.setDrawingCacheEnabled(true);
            bitmap = photoView.getDrawingCache();
            photoView.setDrawingCacheEnabled(false);
        } else {
            photoView.setImageBitmap(bitmap);
        }
        ImageUrlCache.getInstence().addInImageCache(path, bitmap);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                aty.finish();
            }
        });
    }
    private void displayImage(PhotoView photoView, Bitmap bitmap) {
        photoView.setVisibility(View.VISIBLE);
        if (bitmap == null) {
            photoView.setImageResource(R.mipmap.default_img_rect);
        } else {
            photoView.setImageBitmap(bitmap);
        }
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                aty.finish();
            }
        });
    }
    private Bitmap byteArrayToBitmap(byte[] data) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        int mMaxWidth = DensityUtils.getScreenW(aty);
        int mMaxHeight = DensityUtils.getScreenH(aty);
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, option);
        int actualWidth = option.outWidth;
        int actualHeight = option.outHeight;

        // 计算出图片应该显示的宽高
        int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                actualHeight, actualWidth);

        option.inJustDecodeBounds = false;
        option.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                desiredWidth, desiredHeight);
        Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0,
                data.length, option);

        Bitmap bitmap;
        // 做缩放
        if (tempBitmap != null
                && (tempBitmap.getWidth() > desiredWidth || tempBitmap
                .getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
                    desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }

        return bitmap;
    }

    /**
     * 框架会自动将大于设定值的bitmap转换成设定值，所以需要这个方法来判断应该显示默认大小或者是设定值大小。<br>
     * 本方法会根据maxPrimary与actualPrimary比较来判断，如果无法判断则会根据辅助值判断，辅助值一般是主要值对应的。
     * 比如宽为主值则高为辅值
     *
     * @param maxPrimary      需要判断的值，用作主要判断
     * @param maxSecondary    需要判断的值，用作辅助判断
     * @param actualPrimary   真实宽度
     * @param actualSecondary 真实高度
     * @return 获取图片需要显示的大小
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary,
                                           int actualPrimary, int actualSecondary) {
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * 关于本方法的判断，可以查看我的博客：http://kymjs.com/code/2014/12/05/02/
     */
    static int findBestSampleSize(int actualWidth, int actualHeight,
                                  int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }
}
