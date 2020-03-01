package com.zfg.chinamap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zfg.chinamap.bean.ProvincePneumoniaBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

public class MapView extends View {

    private int[] colorArray = new int[]{0xFF5C1701, 0xFFC4290B, 0xFFFE4E17, 0xFFFB8152, 0xFFFAB490, 0xFFFED8D2};
    private int LEVEL_0 = 0;
    private int LEVEL_1 = 1;
    private int LEVEL_2 = 2;
    private int LEVEL_3 = 3;
    private int LEVEL_4 = 4;
    private int LEVEL_5 = 5;

    private Context mContext;
    private List<ProvinceBean> provinceBeanList;
    //选中的省份
    private ProvinceBean provinceSelect;
    private Paint paint;

    private RectF rectF;
    private float scale = 1.0f;

    private List<ProvincePneumoniaBean> provincePneumoniaBeanList = new ArrayList<>();

    public MapView(Context context) {
        super(context);

        init(context);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        provinceBeanList = new ArrayList<>();
    }

    private Thread parseThread = new Thread() {

        @Override
        public void run() {

            InputStream inputStream = mContext.getResources().openRawResource(R.raw.china_high);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                //解析
                Document document = builder.parse(inputStream);
                Element rootElement = document.getDocumentElement();
                NodeList nodeList = rootElement.getElementsByTagName("path");
                float left = -1;
                float top = -1;
                float right = -1;
                float bottom = -1;
                List<ProvinceBean> list = new ArrayList<>();
                Log.i("testlog", "node size = " + nodeList.getLength() + ", bean size = " + provincePneumoniaBeanList.size());
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
//                    String pathData = element.getAttribute("android:pathData");
                    String pathData = element.getAttribute("d");
                    String provinceName = element.getAttribute("title");
                    @SuppressLint("RestrictedApi") Path path = PathParser.createPathFromPathData(pathData);
                    ProvinceBean bean = new ProvinceBean(path, provinceName);

                    //确诊病例等级，根据等级绘制不同的颜色
                    int level = 0;

                    for (int j = 0; j < provincePneumoniaBeanList.size(); j++) {

                        String provinceShortName = provincePneumoniaBeanList.get(j).getProvinceShortName();
                        int confirmedCount = provincePneumoniaBeanList.get(j).getConfirmedCount();
                        if (provinceShortName.equals(provinceName)) {
                            if (confirmedCount >= 10000) {
                                level = LEVEL_0;
                            } else if (confirmedCount >= 1000 && confirmedCount <= 9999) {
                                level = LEVEL_1;
                            } else if (confirmedCount >= 500 && confirmedCount <= 999) {
                                level = LEVEL_2;
                            } else if (confirmedCount >= 100 && confirmedCount <= 499) {
                                level = LEVEL_3;
                            } else if (confirmedCount >= 10 && confirmedCount <= 99) {
                                level = LEVEL_4;
                            } else if (confirmedCount >= 1 && confirmedCount <= 9) {
                                level = LEVEL_5;
                            }
                        }
                    }

                    bean.setDrawColor(colorArray[level]);
                    RectF rectf = new RectF();
                    path.computeBounds(rectf, true);
                    left = left == -1 ? rectf.left : Math.min(left, rectf.left);
                    right = right == -1 ? rectf.right : Math.max(right, rectf.right);
                    top = top == -1 ? rectf.top : Math.min(top, rectf.top);
                    bottom = bottom == -1 ? rectf.bottom : Math.max(bottom, rectf.bottom);

                    list.add(bean);
                }
                provinceBeanList = list;
                rectF = new RectF(left, top, right, bottom);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        requestLayout();
                        invalidate();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (rectF != null) {
            double mapWidth = rectF.width();
            scale = (float) (width / mapWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (null != provinceBeanList) {

            canvas.save();
            canvas.scale(scale, scale);

            for (ProvinceBean provinceBean : provinceBeanList) {

                if (provinceBean != provinceSelect) {
                    provinceBean.drawItem(canvas, paint, false);
                } else {
                    provinceSelect.drawItem(canvas, paint, true);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        handlerTouch(event.getX() / scale, event.getY() / scale);

        return super.onTouchEvent(event);
    }

    private void handlerTouch(float x, float y) {

        if (null == provinceBeanList) {
            return;
        }

        ProvinceBean mProvinceSelect = null;

        for (ProvinceBean provinceBean : provinceBeanList) {

            if (provinceBean.isTouch(x, y)) {

                mProvinceSelect = provinceBean;
            }
        }

        if (null != mProvinceSelect) {
            provinceSelect = mProvinceSelect;
            postInvalidate();
        }
    }

    public void setProvincePneumonia(List<ProvincePneumoniaBean> provincePneumoniaBeanList) {

        this.provincePneumoniaBeanList = provincePneumoniaBeanList;

        parseThread.start();
    }
}
