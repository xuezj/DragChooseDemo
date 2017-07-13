package com.xuezj.dragchooselibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
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
    private int height = radius*2*8/17;
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

    /**
     * 获取默认的宽高值
     */
    public int getDefaultHeightSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.d("MyView", "HeightspecMode:" + specMode);
        Log.d("MyView", "HeightspecSize:" + specSize);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = (int) (textSize + radius * 6 + shiftDownHeight);
                Log.d("MyView", shiftDownHeight + "Heightresult:" + (textSize));
                Log.d("MyView", "++++++++++++++++++++++++++++++++++++");
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

    public int getDefaultwidthSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.d("MyView", "widthspecMode:" + specMode);
        Log.d("MyView", "widthspecSize:" + specSize);

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
        drawablePressed = (BitmapDrawable) a.getDrawable(R.styleable.drag_choose_view_pressed);
        drawableEnabled = (BitmapDrawable) a.getDrawable(R.styleable.drag_choose_view_enabled);
        if (drawableEnabled != null)
            pressedBitmap = drawableEnabled.getBitmap();
        else{
            pressedBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.choose_drawable));
//            .decodeResource(getResources(), R.drawable.choose_drawable)
//                    .BitmapFactory.decodeResource(getResources(), R.drawable.choose_drawable);
        }
        backgroundColor = a.getColor(R.styleable.drag_choose_view_background_color, backgroundColor);
        borderGray = a.getColor(R.styleable.drag_choose_view_border_color, defaultColor);
        defaultTextColor = a.getColor(R.styleable.drag_choose_view_text_default_color, defaultTextColor);
        selectTextColor = a.getColor(R.styleable.drag_choose_view_text_select_color, selectTextColor);
//        borderGray=0xFFcfcfd3;
        textSize = a.getDimension(R.styleable.drag_choose_view_text_size, 20);
        radius=a.getInt(R.styleable.drag_choose_view_radius,radius);
        height = radius*2*8/17;
        textColors = new int[4];
        textColors[0] = selectTextColor;
        textColors[1] = defaultTextColor;
        textColors[2] = defaultTextColor;
        textColors[3] = defaultTextColor;
//        pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.sliderwifion);
        a.recycle();
    }

    public void setTextData(String... strings) {
        List<String> s = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            s.add(strings[i]);
        }
        if (s.size() < 4) {
            for (int i = 0; i < 4 - s.size(); i++) {
                s.add("");
            }
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
        Log.d("MyView", "defaultWidth:" + defaultHeight);
        Log.i("tttttsss", defaultWidth + "ACTION_DOWN" + defaultHeight);

//        if (defaultHeight<120){
//            defaultHeight=120;
//        }
        if (removeWidth == 1)
            removeWidth = defaultWidth / 8;
        long rWidth = Math.round(Math.sqrt(Math.pow(radius, 2) - Math.pow(height / 2, 2)));
        //画圆和圆边线
        paint.setColor(backgroundColor);  //设置画笔颜色
        paint.setStyle(Paint.Style.FILL);//设置填充样式
        canvas.drawCircle(defaultWidth / 8, defaultHeight / 2 + shiftDownHeight, radius, paint);

        canvas.drawCircle(defaultWidth * 3 / 8, defaultHeight / 2 + shiftDownHeight, radius, paint);
        canvas.drawCircle(defaultWidth * 5 / 8, defaultHeight / 2 + shiftDownHeight, radius, paint);
        canvas.drawCircle(defaultWidth * 7 / 8, defaultHeight / 2 + shiftDownHeight, radius, paint);
        paint.setColor(borderGray);  //设置画笔颜色
        paint.setStyle(Paint.Style.STROKE);//设置填充样式
        paint.setStrokeWidth(2);
        canvas.drawCircle(defaultWidth / 8, defaultHeight / 2 + shiftDownHeight, radius + 2, paint);
        canvas.drawCircle(defaultWidth * 3 / 8, defaultHeight / 2 + shiftDownHeight, radius + 2, paint);
        canvas.drawCircle(defaultWidth * 5 / 8, defaultHeight / 2 + shiftDownHeight, radius + 2, paint);
        canvas.drawCircle(defaultWidth * 7 / 8, defaultHeight / 2 + shiftDownHeight, radius + 2, paint);


        //画横线
        paint.setColor(backgroundColor);  //设置画笔颜色
        paint.setStyle(Paint.Style.FILL);//设置填充样式
        paint.setStrokeWidth(1);
        canvas.drawRect(defaultWidth / 8 + rWidth, (defaultHeight / 2 + shiftDownHeight) - height / 2,
                defaultWidth * 3 / 8 - rWidth, (defaultHeight / 2 + shiftDownHeight) + height / 2, paint);// 长方形
        canvas.drawRect(defaultWidth * 3 / 8 + rWidth, (defaultHeight / 2 + shiftDownHeight) - height / 2,
                defaultWidth * 5 / 8 - rWidth, (defaultHeight / 2 + shiftDownHeight) + height / 2, paint);// 长方形
        canvas.drawRect(defaultWidth * 5 / 8 + rWidth, (defaultHeight / 2 + shiftDownHeight) - height / 2,
                defaultWidth * 7 / 8 - rWidth, (defaultHeight / 2 + shiftDownHeight) + height / 2, paint);// 长方形

        //画横线边
        paint.setColor(borderGray);  //设置画笔颜色
        paint.setStrokeWidth(2);
        canvas.drawLine(defaultWidth / 8 + rWidth, (defaultHeight / 2 + shiftDownHeight) - (height / 2 + 1),
                defaultWidth * 3 / 8 - rWidth, (defaultHeight / 2 + shiftDownHeight) - (height / 2 + 1), paint);
        canvas.drawLine(defaultWidth / 8 + rWidth, (defaultHeight / 2 + shiftDownHeight) + (height / 2 + 1),
                defaultWidth * 3 / 8 - rWidth, (defaultHeight / 2 + shiftDownHeight) + (height / 2 + 1), paint);

        canvas.drawLine(defaultWidth * 3 / 8 + rWidth, (defaultHeight / 2 + shiftDownHeight) - (height / 2 + 1),
                defaultWidth * 5 / 8 - rWidth, (defaultHeight / 2 + shiftDownHeight) - (height / 2 + 1), paint);
        canvas.drawLine(defaultWidth * 3 / 8 + rWidth, (defaultHeight / 2 + shiftDownHeight) + (height / 2 + 1),
                defaultWidth * 5 / 8 - rWidth, (defaultHeight / 2 + shiftDownHeight) + (height / 2 + 1), paint);

        canvas.drawLine(defaultWidth * 5 / 8 + rWidth, (defaultHeight / 2 + shiftDownHeight) - (height / 2 + 1),
                defaultWidth * 7 / 8 - rWidth, (defaultHeight / 2 + shiftDownHeight) - (height / 2 + 1), paint);
        canvas.drawLine(defaultWidth * 5 / 8 + rWidth, (defaultHeight / 2 + shiftDownHeight) + (height / 2 + 1),
                defaultWidth * 7 / 8 - rWidth, (defaultHeight / 2 + shiftDownHeight) + (height / 2 + 1), paint);

        Bitmap newbm = Bitmap.createScaledBitmap(pressedBitmap, radius * 3, radius * 3, true);
        //canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH/2, frame.right - MIDDLE_LINE_PADDING,slideTop + MIDDLE_LINE_WIDTH/2, paint);
        canvas.drawBitmap(newbm, removeWidth - newbm.getWidth() / 2, (defaultHeight / 2 + shiftDownHeight) - newbm.getWidth() / 2, null);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < strings.size(); i++) {
            paint.setColor(textColors[i]);  //设置画笔颜色

            //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center

            canvas.drawText(strings.get(i), defaultWidth * (i * 2 + 1) / 8, defaultHeight / 2 - radius * 2, paint);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float x1 = event.getX();
        //获取手指的操作--》按下、移动、松开
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN://按下
                sss = false;
                Log.i("ttttt", "ACTION_DOWN");
                Log.i("ttttts", removeWidth + "ACTION_DOWN" + defaultWidth / 8);
                Log.i("ttttts", (defaultWidth / 8 - pressedBitmap.getWidth() / 2) + "ACTION_DOWN" + x);
                Log.i("ttttts", (defaultWidth / 8 + pressedBitmap.getWidth() / 2) + "ACTION_DOWN" + x);
                Log.i("ttttts", (defaultHeight / 2 + shiftDownHeight + pressedBitmap.getWidth() / 2) + "ACTION_DOWN" + y);
                Log.i("ttttts", (defaultHeight / 2 + shiftDownHeight - pressedBitmap.getWidth() / 2) + "ACTION_DOWN" + y);
                if (x >= (defaultWidth / 8 - pressedBitmap.getWidth() / 2)
                        && x <= (defaultWidth / 8 + pressedBitmap.getWidth() / 2)
                        && y <= (defaultHeight / 2 + shiftDownHeight + pressedBitmap.getWidth() / 2)
                        && y >= (defaultHeight / 2 + shiftDownHeight - pressedBitmap.getWidth() / 2)
                        && removeWidth == defaultWidth / 8) {
                    moveFlag = true;
                    removeWidth = (int) x;
                    if (drawablePressed != null)
                        pressedBitmap = drawablePressed.getBitmap();
                    else{
                        pressedBitmap =drawableToBitmap(getResources().getDrawable(R.drawable.choose_drawable));;
                    }
                    Log.i("ttttts", "ACTION_DOWN");
                } else if (x >= defaultWidth * 3 / 8 - pressedBitmap.getWidth() / 2
                        && x <= defaultWidth * 3 / 8 + pressedBitmap.getWidth() / 2
                        && y <= defaultHeight / 2 + shiftDownHeight + pressedBitmap.getWidth() / 2
                        && y >= defaultHeight / 2 + shiftDownHeight - pressedBitmap.getWidth() / 2
                        && removeWidth == defaultWidth * 3 / 8) {
                    moveFlag = true;
                    removeWidth = (int) x;
                    if (drawablePressed != null)
                        pressedBitmap = drawablePressed.getBitmap();
                    else{
                        pressedBitmap =drawableToBitmap(getResources().getDrawable(R.drawable.choose_drawable));;
                    }
                } else if (x >= defaultWidth * 5 / 8 - pressedBitmap.getWidth() / 2
                        && x <= defaultWidth * 5 / 8 + pressedBitmap.getWidth() / 2
                        && y <= defaultHeight / 2 + shiftDownHeight + pressedBitmap.getWidth() / 2
                        && y >= defaultHeight / 2 + shiftDownHeight - pressedBitmap.getWidth() / 2
                        && removeWidth == defaultWidth * 5 / 8) {
                    moveFlag = true;
                    removeWidth = (int) x;
                    if (drawablePressed != null)
                        pressedBitmap = drawablePressed.getBitmap();
                    else{
                        pressedBitmap =drawableToBitmap(getResources().getDrawable(R.drawable.choose_drawable));;
                    }
                } else if (x >= defaultWidth * 7 / 8 - pressedBitmap.getWidth() / 2
                        && x <= defaultWidth * 7 / 8 + pressedBitmap.getWidth() / 2
                        && y <= defaultHeight / 2 + shiftDownHeight + pressedBitmap.getWidth() / 2
                        && y >= defaultHeight / 2 + shiftDownHeight - pressedBitmap.getWidth() / 2
                        && removeWidth == defaultWidth * 7 / 8) {
                    moveFlag = true;
                    removeWidth = (int) x;
                    if (drawablePressed != null)
                        pressedBitmap = drawablePressed.getBitmap();
                    else{
                        pressedBitmap =drawableToBitmap(getResources().getDrawable(R.drawable.choose_drawable));
                    }
                } else {
                    moveFlag = false;
                    x2 = x1;
                }

                break;

            case MotionEvent.ACTION_MOVE://移动
                if (moveFlag) {
                    removeWidth = (int) x;
                    if (removeWidth >= defaultWidth / 8 && removeWidth <= defaultWidth * 7 / 8) {
                        if (removeWidth <= defaultWidth / 4) {
                            toColor(0);
                        } else if (removeWidth > defaultWidth / 4 && removeWidth <= defaultWidth / 2) {
                            toColor(1);
                        } else if (removeWidth > defaultWidth / 2 && removeWidth <= defaultWidth * 3 / 4) {
                            toColor(2);
                        } else {
                            toColor(3);
                        }
                        invalidate();
                    }
                    Log.i("ttttt", (defaultWidth / 8 - 10) + "ACTION_MOVE" + removeWidth);
                } else {
                    if (Math.abs(x2 - x1) > radius * 2 + 25) {
                        sss = true;
                    }
                }


                break;
            case MotionEvent.ACTION_UP://松开
                Log.i("ttttt", "ACTION_UP");
                if (!sss) {
                    if (drawableEnabled != null)
                        pressedBitmap = drawableEnabled.getBitmap();
                    else{
                        pressedBitmap =drawableToBitmap(getResources().getDrawable(R.drawable.choose_drawable));
                    }
                    if (x <= defaultWidth / 4) {
                        removeWidth = defaultWidth / 8;
                        toColor(0);
                        if (onChooseItemListener != null) {
                            if (strings.size() == 0)
                                onChooseItemListener.chooseItem(0, null);
                            else
                                onChooseItemListener.chooseItem(0, strings.get(0));
                        }
                    } else if (x > defaultWidth / 4 && x <= defaultWidth / 2) {
                        removeWidth = defaultWidth * 3 / 8;
                        toColor(1);
                        if (onChooseItemListener != null) {
                            if (strings.size() == 0)
                                onChooseItemListener.chooseItem(1, null);
                            else
                                onChooseItemListener.chooseItem(1, strings.get(1));
                        }
                    } else if (x > defaultWidth / 2 && x <= defaultWidth * 3 / 4) {
                        removeWidth = defaultWidth * 5 / 8;
                        toColor(2);
                        if (onChooseItemListener != null) {
                            if (strings.size() == 0)
                                onChooseItemListener.chooseItem(2, null);
                            else
                                onChooseItemListener.chooseItem(2, strings.get(2));
                        }
                    } else {
                        removeWidth = defaultWidth * 7 / 8;
                        toColor(3);
                        if (onChooseItemListener != null) {
                            if (strings.size() == 0)
                                onChooseItemListener.chooseItem(3, null);
                            else
                                onChooseItemListener.chooseItem(3, strings.get(3));
                        }
                    }

                    invalidate();
                }

                break;
        }
        return true;
//        return super.onTouchEvent(event);
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
    public  Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(radius*4,radius*4,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, radius*4, radius*4);
        drawable.draw(canvas);
        return bitmap;
    }
}
