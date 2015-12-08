package cn.horry.photo_album;

import android.graphics.Bitmap;
import android.util.LruCache;


/**
 * Created by Administrator on 2015/12/8.
 */
public class ImageUrlCache {
    private static ImageUrlCache _ImageUrlCache = null;
    private  LruCache<String,Bitmap> imageCache  = null;
    private ImageUrlCache(){
        initImageCache();
    }

    public static ImageUrlCache getInstence(){
        if(_ImageUrlCache==null)
        {
            _ImageUrlCache=new ImageUrlCache();
        }
        return _ImageUrlCache;
    }



    /**
     * 初始化图片缓存
     */
    private void initImageCache() {
        int runMemory = (int) Runtime.getRuntime().maxMemory();//获得最大内存
        int cacheMemory = runMemory / 5;
        imageCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {

                return value.getHeight() * value.getRowBytes();
            }
        };
    }
    public  LruCache<String, Bitmap> getImageCache() {
        return imageCache;
    }

    public  void setImageCache(LruCache<String, Bitmap> imageCache) {
        this.imageCache = imageCache;
    }
    public Bitmap getBimapWithPath(String path){
        return imageCache.get(path);
    }
    public void addInImageCache(String path,Bitmap bitmap)
    {
        imageCache.put(path,bitmap);
    }
}
