package com.example.traveltao.frescotest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SimpleDraweeView mSimpleDraweeView;
    private Uri uri;

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        addListener();

        uri = Uri.parse("https://ss0.bdstatic" +
                ".com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec" +
                "=1542255750&di=88b483c92e7595c72307f65ff0fa26a3&src=http://imgsrc.baidu" +
                ".com/imgad/pic/item/0b46f21fbe096b6355c3f37a07338744eaf8acc6.jpg");
    }

    private void initView() {
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        mSimpleDraweeView = findViewById(R.id.simple_view);
    }

    private void addListener() {
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                //自定义图片显示效果
                List<Drawable> backgroundsList = new ArrayList<>();
                List<Drawable> overlaysList = new ArrayList<>();
                for (int i = 0; i < 4 ; i++) {
                    backgroundsList.add(getResources().getDrawable(R.drawable
                            .ic_launcher_background));
                    overlaysList.add(getResources().getDrawable(R.drawable.ic_launcher_background));
                }
                GenericDraweeHierarchyBuilder builder =
                        new GenericDraweeHierarchyBuilder(getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setFadeDuration(300)
                        //.setPlaceholderImage(R.drawable.ic_launcher_foreground)
                        //.setBackgrounds(backgroundsList)
                        //.setOverlays(overlaysList)
                        .setProgressBarImage(new ProgressBarDrawable())
                        .build();
                hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);

                mSimpleDraweeView.setHierarchy(hierarchy);
                mSimpleDraweeView.setImageURI(uri);
                break;
            case R.id.button2:
                Uri uri2 = Uri.parse("https://qq.yh31.com/tp/zjbq/201810281602569026.gif");
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(uri2)
                        //.setControllerListener(controllerListener)
                        .setAutoPlayAnimations(true)//动画下载完后自动播放
                        .build();
                mSimpleDraweeView.setController(controller);
                break;
            case R.id.button3:
                //渐进式加载图片,模糊到清晰, 缩放
                int width = 50, height = 50;
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setPostprocessor(redMeshPostprocessor)//后处理
                        .setAutoRotateEnabled(true)//是否支持自动旋转
                        .setProgressiveRenderingEnabled(true)//是否支持渐进式加载
                        //.setResizeOptions(new ResizeOptions(width, height))//图片缩放选项
                        //.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                        // 最低请求级别
                        .build();
                DraweeController controller3 = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setTapToRetryEnabled(true)
                        .setOldController(mSimpleDraweeView.getController())//节省不必要的内存分配
                        .build();
                mSimpleDraweeView.setController(controller3);
                break;
            case R.id.button4:
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                boolean inMemoryCache = imagePipeline.isInBitmapMemoryCache(uri);
                Toast.makeText(this, "已经Bitmap缓存？" + inMemoryCache, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //动画下载监听
    private ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(
                String id,
                @Nullable ImageInfo imageInfo,
                @Nullable Animatable anim) {
            if (anim != null) {
                // 其他控制逻辑
                anim.start();
            }
        }
    };


    /**
     * 后处理,修改图片
     * 不要重写多于 1 个的 process 方法。这么做可能造成无法预测的结果。
     */
    private Postprocessor redMeshPostprocessor = new BasePostprocessor() {
        @Override
        public String getName() {
            return "redMeshPostprocessor";
        }

        @Override
        public void process(Bitmap bitmap) {
            //给图片加了红色网格
            for (int x = 0; x < bitmap.getWidth(); x += 2) {
                for (int y = 0; y < bitmap.getHeight(); y += 2) {
                    bitmap.setPixel(x, y, Color.RED);
                }
            }
        }

        /*@Override
        public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
            //水平翻转图片，出错
            for (int x = 0; x < destBitmap.getWidth(); x++) {
                for (int y = 0; y < destBitmap.getHeight(); y++) {
                    destBitmap.setPixel(destBitmap.getWidth() - x, y, sourceBitmap.getPixel(x, y));
                }
            }
        }*/

        /*@Override
        public CloseableReference<Bitmap> process(Bitmap sourceBitmap,
                                                  PlatformBitmapFactory bitmapFactory) {
            //将源图片复制为 1 / 4 大小，有问题
            CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(
                    sourceBitmap.getWidth() / 2,
                    sourceBitmap.getHeight() / 2);
            try {
                Bitmap destBitmap = bitmapRef.get();
                for (int x = 0; x < destBitmap.getWidth(); x+=2) {
                    for (int y = 0; y < destBitmap.getHeight(); y+=2) {
                        destBitmap.setPixel(x, y, sourceBitmap.getPixel(x, y));
                    }
                }
                return CloseableReference.cloneOrNull(bitmapRef);
            } finally {
                CloseableReference.closeSafely(bitmapRef);
            }
        }*/
    };
}
