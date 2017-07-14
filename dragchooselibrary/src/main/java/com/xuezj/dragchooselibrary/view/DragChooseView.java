package com.xuezj.dragchooselibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xuezj.dragchooselibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuezj on 2017/7/7.
 */

public class DragChooseView extends View {
    private Context mContext;
    private BitmapDrawable drawablePressed, drawableEnabled;
    private Paint paint;
    private int defaultWidth = 200;
    private int defaultHeight = 80;
    private int backgroundColor = 0xFFefeff4;
    private int borderGray;
    private int defaultColor = 0xFFFFFFFF;
    private int defaultTextColor = 0xFFa0a0a0;
    private int selectTextColor = 0xFF007DF7;
    private int radius = 15;
    private int height = radius * 2 * 8 / 17;
    private int removeWidth = 1;
    private float textSize;
    private boolean moveFlag = false;
    private Bitmap pressedBitmap;
    private List<String> strings = new ArrayList<>();
    private int[] textColors;
    private int shiftDownHeight = 0;
    private OnChooseItemListener onChooseItemListener;
    private float x2 = 0;
    private boolean sss = false;
    private int counts = 4;
    private int defaultIndex = 0;

    public DragChooseView(Context context) {
        super(context);
        mContext = context;
        paint = new Paint();
    }

    public DragChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public DragChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setCustomAttributes(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DragChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        setCustomAttributes(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultwidthSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultHeightSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }


    private int getDefaultHeightSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = (int) (textSize + radius * 6 + shiftDownHeight);
                break;
            case MeasureSpec.EXACTLY:
                if (specSize < (textSize + radius * 6 + shiftDownHeight)) {
                    result = (int) (textSize + radius * 6 + shiftDownHeight);
                } else {
                    result = specSize;
                }
                break;
        }
        return result;
    }

    private int getDefaultwidthSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    private void setCustomAttributes(AttributeSet attrs) {
        paint = new Paint();
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.drag_choose_view);
        drawablePressed = (BitmapDrawable) a.getDrawable(R.styleable.drag_choose_view_focused);
        drawableEnabled = (BitmapDrawable) a.getDrawable(R.styleable.drag_choose_view_enabled);
        if (drawableEnabled != null)
            pressedBitmap = drawableEnabled.getBitmap();
        else {
            pressedBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.choose_drawable));

        }
        backgroundColor = a.getColor(R.styleable.drag_choose_view_background_color, backgroundColor);
        borderGray = a.getColor(R.styleable.drag_choose_view_border_color, defaultColor);
        defaultTextColor = a.getColor(R.styleable.drag_choose_view_text_default_color, defaultTextColor);
        selectTextColor = a.getColor(R.styleable.drag_choose_view_text_select_color, selectTextColor);

        textSize = a.getDimension(R.styleable.drag_choose_view_text_size, 20);
        radius = a.getInt(R.styleable.drag_choose_view_radius, radius);
        counts = a.getInt(R.styleable.drag_choose_view_counts, counts);
        if(counts<2)
            counts=2;
        if (counts>8)
            counts=8;
        height = radius * 2 * 8 / 17;
        textColors = new int[counts];
        for (int i = 0; i < counts; i++) {
            if (i == 0)
                textColors[i] = selectTextColor;
            else
                textColors[i] = defaultTextColor;
        }
//        pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.sliderwifion);
        a.recycle();
    }
    public void setdefaultSelectedItem(int index){
        if(index<0||index>counts){

        }else{
            this.defaultIndex=index;
        }


    }
    public void setTextData(String... strings) {
        List<String> s = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            s.add(strings[i]);
        }
        int j = s.size();
        if (s.size() < counts) {
            for (int i = 0; i < counts - j; i++) {
                s.add("");
            }
        } else {

            for (int i = 0; i < j - counts; i++)
                s.remove(s.size() - 1);

        }
        if (s.size() != 0) {
            shiftDownHeight = 10;
        }
        this.strings = s;
    }

    public void addOnChooseItemListener(OnChooseItemListener onChooseItemListener) {

        this.onChooseItemListener = onChooseItemListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        defaultWidth = getWidth();
        defaultHeight = getHeight();

        if (removeWidth == 1)
            removeWidth = defaultWidth*(defaultIndex* 2 + 1) / (counts * 2);
        long rWidth = Math.round(Math.sqrt(Math.pow(radius, 2) - Math.pow(height / 2, 2)));
        toDrawCircle(canvas);
        toDrawSquares(canvas, rWidth);


        Bitmap newBm = Bitmap.createScaledBitmap(pressedBitmap, radius * 3, radius * 3, true);
        canvas.drawBitmap(newBm, removeWidth - newBm.getWidth() / 2, (defaultHeight / 2 + shiftDownHeight) - newBm.getWidth() / 2, null);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < strings.size(); i++) {
            paint.setColor(textColors[i]);  //设置画笔颜色
            canvas.drawText(strings.get(i), defaultWidth * (i * 2 + 1) / (counts * 2), defaultHeight / 2 - radius * 2, paint);
        }

    }


    private void toDrawCircle(Canvas canvas) {
        for (int i = 0; i < counts; i++) {
            paint.setColor(backgroundColor);  //设置画笔颜色
            paint.setStyle(Paint.Style.FILL);//设置填充样式
            canvas.drawCircle(defaultWidth * (i * 2 + 1) / (counts * 2), defaultHeight / 2 + shiftDownHeight, radius, paint);
            paint.setColor(borderGray);  //设置画笔颜色
            paint.setStyle(Paint.Style.STROKE);//设置填充样式
            paint.setStrokeWidth(2);
            canvas.drawCircle(defaultWidth * (i * 2 + 1) / (counts * 2), defaultHeight / 2 + shiftDownHeight, radius + 2, paint);
        }
    }

    private void toDrawSquares(Canvas canvas, long rWidth) {
        for (int i = 0; i < counts - 1; i++) {
            paint.setColor(backgroundColor);  //设置画笔颜色
            paint.setStyle(Paint.Style.FILL);//设置填充样式
            paint.setStrokeWidth(1);
            canvas.drawRect(defaultWidth * (i * 2 + 1) / (counts * 2) + rWidth, (defaultHeight / 2 + shiftDownHeight) - height / 2,
                    defaultWidth * (i * 2 + 3) / (counts * 2) - rWidth, (defaultHeight / 2 + shiftDownHeight) + height / 2, paint);// 长方形
            paint.setColor(borderGray);  //设置画笔颜色
            paint.setStrokeWidth(2);
            canvas.drawLine(defaultWidth * (i * 2 + 1) / (counts * 2) + rWidth, (defaultHeight / 2 + shiftDownHeight) - (height / 2 + 1),
                    defaultWidth * (i * 2 + 3) / (counts * 2) - rWidth, (defaultHeight / 2 + shiftDownHeight) - (height / 2 + 1), paint);
            canvas.drawLine(defaultWidth * (i * 2 + 1) / (counts * 2) + rWidth, (defaultHeight / 2 + shiftDownHeight) + (height / 2 + 1),
                    defaultWidth * (i * 2 + 3) / (counts * 2) - rWidth, (defaultHeight / 2 + shiftDownHeight) + (height / 2 + 1), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float x1 = event.getX();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN://按下
                sss = false;
                if (toOnTouchDown(x, y) < 0) {
                    moveFlag = false;
                    x2 = x1;
                } else {
                    moveFlag = true;
                    removeWidth = (int) x;
                    if (drawablePressed != null)
                        pressedBitmap = drawablePressed.getBitmap();
                    else {
                        pressedBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.choose_drawable));
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE://移动
                if (moveFlag) {
                    removeWidth = (int) x;
                    if (removeWidth >= defaultWidth / (counts * 2) && removeWidth <= defaultWidth * (counts * 2 - 1) / (counts * 2)) {
                        toColor(toOnTouchMove(removeWidth));
                        invalidate();
                    }
                } else {
                    if (Math.abs(x2 - x1) > radius * 2 + 25) {
                        sss = true;
                    }
                }


                break;
            case MotionEvent.ACTION_UP://松开
                if (!sss) {
                    if (drawableEnabled != null)
                        pressedBitmap = drawableEnabled.getBitmap();
                    else {
                        pressedBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.choose_drawable));
                    }
                    int i = toOnTouchUp(x);
                    removeWidth = defaultWidth * (i * 2 + 1) / (counts * 2);
                    toColor(i);
                    if (onChooseItemListener != null) {
                        if (strings.size() == 0)
                            onChooseItemListener.chooseItem(i, null);
                        else
                            onChooseItemListener.chooseItem(i, strings.get(i));
                    }
                    invalidate();
                }

                break;
        }
        return true;
    }

    private int toOnTouchUp(float x) {
        int j = 0;
        for (int i = 0; i < counts; i++) {
            if (x > defaultWidth * i / counts && x <= defaultWidth * (i + 1) / counts)
                j = i;
        }
        return j;
    }

    private int toOnTouchMove(int removeWidth) {
        int j = 0;
        for (int i = 0; i < counts; i++) {
            if (removeWidth > defaultWidth * i / counts && removeWidth <= defaultWidth * (i + 1) / counts)
                j = i;
        }

        return j;
    }

    private int toOnTouchDown(float x, float y) {
        for (int i = 0; i < counts; i++) {
            if (x >= (defaultWidth * (i * 2 + 1) / (counts * 2) - pressedBitmap.getWidth() / 2)
                    && x <= (defaultWidth * (i * 2 + 1) / (counts * 2) + pressedBitmap.getWidth() / 2)
                    && y <= (defaultHeight / 2 + shiftDownHeight + pressedBitmap.getWidth() / 2)
                    && y >= (defaultHeight / 2 + shiftDownHeight - pressedBitmap.getWidth() / 2)
                    && removeWidth == (defaultWidth * (i * 2 + 1) / (counts * 2))) {
                return i;
            }
        }

        return -1;
    }

    private void toColor(int index) {
        for (int i = 0; i < textColors.length; i++) {
            textColors[i] = defaultTextColor;
        }
        textColors[index] = selectTextColor;
    }

    public interface OnChooseItemListener {
        void chooseItem(int index, String text);
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(radius * 4, radius * 4,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, radius * 4, radius * 4);
        drawable.draw(canvas);
        return bitmap;
    }
}
