//package com.she.core.loopview;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.Typeface;
//import android.os.Handler;
//import android.util.AttributeSet;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//
//
//import com.she.core.R;
//
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by sg on 2016/8/18.
// */
//public class LoopView<T> extends View {
//
//    private float scaleX = 1.05F;
//
//    private static final int DEFAULT_TEXT_SIZE = (int) (Resources.getSystem().getDisplayMetrics().density * 15);
//
//    private static final float DEFAULT_LINE_SPACE = 2f;
//
//    private static final int DEFAULT_VISIBIE_ITEMS = 9;
//
//    public enum ACTION {
//        CLICK, FLING, DAGGLE
//    }
//
//    private Context context;
//
//    Handler handler;
//    private GestureDetector flingGestureDetector;
//    OnItemSelectedListener onItemSelectedListener;
//
//    // Timer mTimer;
//    ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
//    private ScheduledFuture<?> mFuture;
//
//    private Paint paintOuterText;
//    private Paint paintCenterText;
//    private Paint paintIndicator;
//    List<String> items;
//    List<ItemData<T>> itemDatas;
//
//    int textSize;
//    int textSizeCenter;
//    int maxTextHeight;
//
//    int outerTextColor;
//
//    int centerTextColor;
//    int dividerColor;
//
//    float lineSpacingMultiplier;
//    boolean isLoop;
//
//    int firstLineY;
//    int secondLineY;
//
//    int totalScrollY;
//    int initPosition;
//    private int selectedItem;
//    int preCurrentIndex;
//    int change;
//
//    int itemsVisibleCount;
//
//    String[] drawingStrings;
//
//    int measuredHeight;
//    int measuredWidth;
//
//    int halfCircumference;
//    int radius;
//
//    private int mOffset = 0;
//    private float previousY;
//    long startTime = 0;
//
//    private Rect tempRect = new Rect();
//
//    private int paddingLeft, paddingRight;
//
//    /**
//     * set text line space, must more than 1
//     *
//     * @param lineSpacingMultiplier
//     */
//    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
//        if (lineSpacingMultiplier > 1.0f) {
//            this.lineSpacingMultiplier = lineSpacingMultiplier;
//        }
//    }
//
//    /**
//     * set outer text color
//     *
//     * @param centerTextColor
//     */
//    public void setCenterTextColor(int centerTextColor) {
//        this.centerTextColor = centerTextColor;
//        paintCenterText.setColor(centerTextColor);
//    }
//
//    /**
//     * set center text color
//     *
//     * @param outerTextColor
//     */
//    public void setOuterTextColor(int outerTextColor) {
//        this.outerTextColor = outerTextColor;
//        paintOuterText.setColor(outerTextColor);
//    }
//
//    /**
//     * set divider color
//     *
//     * @param dividerColor
//     */
//    public void setDividerColor(int dividerColor) {
//        this.dividerColor = dividerColor;
//        paintIndicator.setColor(dividerColor);
//    }
//
//    public LoopView(Context context) {
//        super(context);
//        initLoopView(context, null);
//    }
//
//    public LoopView(Context context, AttributeSet attributeset) {
//        super(context, attributeset);
//        initLoopView(context, attributeset);
//    }
//
//    public LoopView(Context context, AttributeSet attributeset, int defStyleAttr) {
//        super(context, attributeset, defStyleAttr);
//        initLoopView(context, attributeset);
//    }
//
//    private void initLoopView(Context context, AttributeSet attributeset) {
//        this.context = context;
//        handler = new MessageHandler(this);
//        flingGestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
//        flingGestureDetector.setIsLongpressEnabled(false);
//
//        TypedArray typedArray = context.obtainStyledAttributes(attributeset, R.styleable.wheelView);
//        textSize = typedArray.getInteger(R.styleable.wheelView_wh_textsize, DEFAULT_TEXT_SIZE);
//        textSizeCenter = typedArray.getInteger(R.styleable.wheelView_wh_textsizecenter, DEFAULT_TEXT_SIZE);
//        textSize = (int) (Resources.getSystem().getDisplayMetrics().density * textSize);
//        textSizeCenter = (int) (Resources.getSystem().getDisplayMetrics().density * textSizeCenter);
//        lineSpacingMultiplier = typedArray.getFloat(R.styleable.wheelView_wh_lineSpace, DEFAULT_LINE_SPACE);
//        centerTextColor = typedArray.getInteger(R.styleable.wheelView_wh_centerTextColor, 0xffec6f1a); //????????????????????????ff313131
//        outerTextColor = typedArray.getInteger(R.styleable.wheelView_wh_outerTextColor, 0xffafafaf);
//        dividerColor = typedArray.getInteger(R.styleable.wheelView_wh_dividerTextColor, 0xffc5c5c5);
//        itemsVisibleCount =
//                typedArray.getInteger(R.styleable.wheelView_wh_itemsVisibleCount, DEFAULT_VISIBIE_ITEMS);
//        if (itemsVisibleCount % 2 == 0) {
//            itemsVisibleCount = DEFAULT_VISIBIE_ITEMS;
//        }
//        isLoop = typedArray.getBoolean(R.styleable.wheelView_wh_isLoop, true);
//        typedArray.recycle();
//
//        drawingStrings = new String[itemsVisibleCount];
//
//        totalScrollY = 0;
//        initPosition = -1;
//
//        initPaints();
//    }
//
//    /**
//     * visible item count, must be odd number
//     *
//     * @param visibleNumber
//     */
//    public void setItemsVisibleCount(int visibleNumber) {
//        if (visibleNumber % 2 == 0) {
//            return;
//        }
//        if (visibleNumber != itemsVisibleCount) {
//            itemsVisibleCount = visibleNumber;
//            drawingStrings = new String[itemsVisibleCount];
//        }
//    }
//
//    private void initPaints() {
//        paintOuterText = new Paint();
//        paintOuterText.setColor(outerTextColor);
//        paintOuterText.setAntiAlias(true);
//        paintOuterText.setTypeface(Typeface.MONOSPACE);
//        paintOuterText.setTextSize(textSize);
//
//        paintCenterText = new Paint();
//        paintCenterText.setColor(centerTextColor);
//        paintCenterText.setAntiAlias(true);
//        paintCenterText.setTextScaleX(scaleX);
//        paintCenterText.setTypeface(Typeface.MONOSPACE);
//        paintCenterText.setTextSize(textSizeCenter);
//
//        paintIndicator = new Paint();
//        paintIndicator.setColor(dividerColor);
//        paintIndicator.setAntiAlias(true);
//
//    }
//
//    private void remeasure() {
//        if (items == null || items.size() <0) {
//            if (itemDatas == null || itemDatas.size() <= 0) {
//                return;
//            }
////            return;
//        }
////        if (itemDatas == null && itemDatas.size() > 0) {
////            return;
////        }
//
//        measuredWidth = getMeasuredWidth();
//
//        measuredHeight = getMeasuredHeight();
//
//        if (measuredWidth == 0 || measuredHeight == 0) {
//            return;
//        }
//
//        paddingLeft = getPaddingLeft();
//        paddingRight = getPaddingRight();
//
//        measuredWidth = measuredWidth - paddingRight;
//
//        paintCenterText.getTextBounds("\u661F\u671F", 0, 2, tempRect); // ??????
//        maxTextHeight = tempRect.height();
//        halfCircumference = (int) (measuredHeight * Math.PI / 2) + 5;
//
//        maxTextHeight = (int) (halfCircumference / (lineSpacingMultiplier * (itemsVisibleCount - 1)));
//
//        radius = measuredHeight / 2;
//        firstLineY = (int) ((measuredHeight - lineSpacingMultiplier * maxTextHeight) / 2.0F) - 4;
//        secondLineY = (int) ((measuredHeight + lineSpacingMultiplier * maxTextHeight) / 2.0F) + 4;
//        if (initPosition == -1) {
//            if (isLoop) {
//                if (items == null || items.size() <0) {
//                    initPosition = (itemDatas.size() + 1) / 2;
//                }else
//                initPosition = (items.size() + 1) / 2;
//            } else {
//                initPosition = 0;
//            }
//        }
//
//        preCurrentIndex = initPosition;
//    }
//
//    void smoothScroll(ACTION action) {
//        cancelFuture();
//        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
//            float itemHeight = lineSpacingMultiplier * maxTextHeight;
//            mOffset = (int) ((totalScrollY % itemHeight + itemHeight) % itemHeight);
//            if ((float) mOffset > itemHeight / 2.0F) {
//                mOffset = (int) (itemHeight - (float) mOffset);
//            } else {
//                mOffset = -mOffset;
//            }
//        }
//        mFuture =
//                mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS);
//    }
//
//    protected final void scrollBy(float velocityY) {
//        cancelFuture();
//        // change this number, can change fling speed
//        int velocityFling = 10;
//        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, velocityFling,
//                TimeUnit.MILLISECONDS);
//    }
//
//    public void cancelFuture() {
//        if (mFuture != null && !mFuture.isCancelled()) {
//            mFuture.cancel(true);
//            mFuture = null;
//        }
//    }
//
//    /**
//     * set not loop
//     */
//    public void setNotLoop() {
//        isLoop = false;
//    }
//
//    /**
//     * set text size in dp
//     *
//     * @param size
//     */
//    public final void setTextSize(float size) {
//        if (size > 0.0F) {
//            textSize = (int) (context.getResources().getDisplayMetrics().density * size);
//            paintOuterText.setTextSize(textSize);
//            paintCenterText.setTextSize(textSizeCenter);
//        }
//    }
//
//    /**
//     * set text size in dp
//     *
//     * @param size
//     */
//    public final void setTextSizeCenter(float size) {
//        if (size > 0.0F) {
//            textSizeCenter = (int) (context.getResources().getDisplayMetrics().density * size);
//            paintOuterText.setTextSize(textSize);
//            paintCenterText.setTextSize(textSizeCenter);
//        }
//    }
//
//    public final void setInitPosition(int initPosition) {
//        if (initPosition < 0) {
//            this.initPosition = 0;
//        } else {
//            if (items != null && items.size() > initPosition) {
//                this.initPosition = initPosition;
//            }
//        }
//    }
//
//    public final void setListener(OnItemSelectedListener OnItemSelectedListener) {
//        onItemSelectedListener = OnItemSelectedListener;
//    }
//
//    public final void setItems(List<String> items) {
//        this.items = items;
//        remeasure();
//        invalidate();
//    }
//
//    public final void setItemDatas(List<ItemData<T>> itemDatas) {
//        this.itemDatas = itemDatas;
//        remeasure();
//        invalidate();
//    }
//
//    public final int getSelectedItem() {
//        return selectedItem;
//    }
//    //
//    // protected final void scrollBy(float velocityY) {
//    // Timer timer = new Timer();
//    // mTimer = timer;
//    // timer.schedule(new InertiaTimerTask(this, velocityY, timer), 0L, 20L);
//    // }
//
//    protected final void onItemSelected() {
//        if (onItemSelectedListener != null) {
//            postDelayed(new OnItemSelectedRunnable(this), 200L);
//        }
//    }
//
//    /**
//     * link https://github.com/weidongjian/androidWheelView/issues/10
//     *
//     * @param scaleX
//     */
//    public void setScaleX(float scaleX) {
//        this.scaleX = scaleX;
//    }
//
//    /**
//     * set current item position
//     *
//     * @param position
//     */
//    public void setCurrentPosition(int position) {
//        if (items == null || items.isEmpty()) {
//            if (itemDatas == null || itemDatas.isEmpty()){
//                return;
//            }
//
//        }
//        int size = items.size();
//        if (itemDatas != null && itemDatas.size()>0){
//            size = itemDatas.size();
//        }
//        if (position >= 0 && position < size && position != selectedItem) {
//            initPosition = position;
//            totalScrollY = 0;
//            mOffset = 0;
//            invalidate();
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        if (items == null) {
//            if(itemDatas == null){
//                return;
//            }
////            return;
//        }
//
//        change = (int) (totalScrollY / (lineSpacingMultiplier * maxTextHeight));
//        if(itemDatas != null && itemDatas.size()>0){
//            preCurrentIndex = initPosition + change % itemDatas.size();
//        }else
//        preCurrentIndex = initPosition + change % items.size();
//
//        if (!isLoop) {
//            if (preCurrentIndex < 0) {
//                preCurrentIndex = 0;
//            }
//            if (items != null && preCurrentIndex > items.size() - 1) {
//                preCurrentIndex = items.size() - 1;
//            }
//            if (itemDatas != null && itemDatas.size() > 0) {
//                if (preCurrentIndex > itemDatas.size() - 1) {
//                    preCurrentIndex = itemDatas.size() - 1;
//                }
//            }
//        } else {
//            if (preCurrentIndex < 0) {
//                if(items != null){
//                    preCurrentIndex = items.size() + preCurrentIndex;
//                }
//
//                if (itemDatas != null && itemDatas.size() > 0) {
//                    preCurrentIndex = itemDatas.size() + preCurrentIndex;
//                }
//            }
//            if(items != null){
//                if (preCurrentIndex > items.size() - 1) {
//                    preCurrentIndex = preCurrentIndex - items.size();
//                }
//            }
//
//            if (itemDatas != null && itemDatas.size() > 0) {
//                if (preCurrentIndex > itemDatas.size() - 1) {
//                    preCurrentIndex = preCurrentIndex - itemDatas.size();
//                }
//            }
//        }
//
//        int j2 = (int) (totalScrollY % (lineSpacingMultiplier * maxTextHeight));
//        // put value to drawingString
//        int k1 = 0;
//        while (k1 < itemsVisibleCount) {
//            int l1 = preCurrentIndex - (itemsVisibleCount / 2 - k1);
//            if (isLoop) {
//                while (l1 < 0) {
//                    if(items != null){
//                        l1 = l1 + items.size();
//                    }
////                    l1 = l1 + items.size();
//                    if (itemDatas != null && itemDatas.size() > 0) {
//                        l1 = l1 + itemDatas.size();
//                    }
//                }
//                if(items !=null){
//                    while (l1 > items.size() - 1) {
//                        l1 = l1 - items.size();
//                    }
//                    drawingStrings[k1] = items.get(l1);
//                }
//                if (itemDatas != null && itemDatas.size() > 0) {
//                    while (l1 > itemDatas.size() - 1) {
//                        l1 = l1 - itemDatas.size();
//                    }
//                }
//
//
//                if (itemDatas != null && itemDatas.size() > 0) {
//                    drawingStrings[k1] = itemDatas.get(l1).getItemName();
//                }
//
//            } else if (l1 < 0) {
//                drawingStrings[k1] = "";
//                if (itemDatas != null && itemDatas.size() > 0) {
//                    drawingStrings[k1] = "";
//                }
//            } else if(items==null){
//                if (l1 > itemDatas.size() - 1) {
//                    if (itemDatas != null && itemDatas.size() > 0) {
//                        drawingStrings[k1] = "";
//                    }
//                }else {
//                    if (itemDatas != null && itemDatas.size() > 0) {
//                        drawingStrings[k1] = itemDatas.get(l1).getItemName();
//                    }
//                }
//            }else if (l1 > items.size() - 1) {
//                drawingStrings[k1] = "";
//                if (itemDatas != null && itemDatas.size() > 0) {
//                    drawingStrings[k1] = "";
//                }
//            } else {
//                drawingStrings[k1] = items.get(l1);
////                drawingStrings[k1] = itemDatas.get(l1).getItemName();
//                if (itemDatas != null && itemDatas.size() > 0) {
//                    drawingStrings[k1] = itemDatas.get(l1).getItemName();
//                }
//            }
//            k1++;
//        }
//        canvas.drawLine(paddingLeft, firstLineY, measuredWidth, firstLineY, paintIndicator);
//        canvas.drawLine(paddingLeft, secondLineY, measuredWidth, secondLineY, paintIndicator);
//
//        int i = 0;
//        while (i < itemsVisibleCount) {
//            canvas.save();
//            float itemHeight = maxTextHeight * lineSpacingMultiplier;
//            double radian = ((itemHeight * i - j2) * Math.PI) / halfCircumference;
//            if (radian >= Math.PI || radian <= 0) {
//                canvas.restore();
//            } else {
//                int translateY = (int) (radius - Math.cos(radian) * radius - (Math.sin(radian) * maxTextHeight) / 2D);
//                canvas.translate(0.0F, translateY);
//                canvas.scale(1.0F, (float) Math.sin(radian));
//                if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
//                    // first divider
//                    canvas.save();
//                    canvas.clipRect(0, 0, measuredWidth, firstLineY - translateY);
//                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintOuterText, tempRect),
//                            maxTextHeight, paintOuterText);
//                    canvas.restore();
//                    canvas.save();
//                    canvas.clipRect(0, firstLineY - translateY, measuredWidth, (int) (itemHeight));
//                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintCenterText, tempRect),
//                            maxTextHeight, paintCenterText);
//                    canvas.restore();
//                } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
//                    // second divider
//                    canvas.save();
//                    canvas.clipRect(0, 0, measuredWidth, secondLineY - translateY);
//                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintCenterText, tempRect),
//                            maxTextHeight, paintCenterText);
//
//                    canvas.restore();
//                    canvas.save();
//                    canvas.clipRect(0, secondLineY - translateY, measuredWidth, (int) (itemHeight));
//                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintOuterText, tempRect),
//                            maxTextHeight, paintOuterText);
//                    canvas.restore();
//                } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
//                    // center item
//                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
//                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintCenterText, tempRect),
//                            maxTextHeight, paintCenterText);
//                    if(items!= null){
//                        selectedItem = items.indexOf(drawingStrings[i]);
//                    }
//                    if(itemDatas!=null){
////                        selectedItem = itemDatas.indexOf(drawingStrings[i]);
//                        for(int j =0;j<itemDatas.size();j++){
//                            if(itemDatas.get(j).getItemName().equals(drawingStrings[i])){
//                                selectedItem = j;
//                            }
//                        }
//                    }
////                    selectedItem = items.indexOf(drawingStrings[i]);
//                } else {
//                    // other item
//                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
//                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintOuterText, tempRect),
//                            maxTextHeight, paintOuterText);
//                }
//                canvas.restore();
//            }
//            i++;
//        }
//
//
//    }
//
//    // text start drawing position
//    private int getTextX(String a, Paint paint, Rect rect) {
//        paint.getTextBounds(a, 0, a.length(), rect);
//        int textWidth = rect.width();
//        textWidth *= scaleX;
//        return (measuredWidth - paddingLeft - textWidth) / 2 + paddingLeft;
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        remeasure();
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        boolean eventConsumed = flingGestureDetector.onTouchEvent(event);
//        float itemHeight = lineSpacingMultiplier * maxTextHeight;
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                startTime = System.currentTimeMillis();
//                cancelFuture();
//                previousY = event.getRawY();
//                if (getParent() != null) {
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                }
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                float dy = previousY - event.getRawY();
//                previousY = event.getRawY();
//
//                totalScrollY = (int) (totalScrollY + dy);
//
//                if (!isLoop) {
//                    float top = -initPosition * itemHeight;
//                    float bottom = 0;
//                    if(items!= null){
//                        bottom = (items.size() - 1 - initPosition) * itemHeight;
//                    }
//                    if(itemDatas != null){
//                        bottom = (itemDatas.size() - 1 - initPosition) * itemHeight;
//                    }
////                    float bottom = (items.size() - 1 - initPosition) * itemHeight;
//
//                    if (totalScrollY < top) {
//                        totalScrollY = (int) top;
//                    } else if (totalScrollY > bottom) {
//                        totalScrollY = (int) bottom;
//                    }
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//            default:
//                if (!eventConsumed) {
//                    float y = event.getY();
//                    double l = Math.acos((radius - y) / radius) * radius;
//                    int circlePosition = (int) ((l + itemHeight / 2) / itemHeight);
//
//                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;
//                    mOffset = (int) ((circlePosition - itemsVisibleCount / 2) * itemHeight - extraOffset);
//
//                    if ((System.currentTimeMillis() - startTime) > 120) {
//                        smoothScroll(ACTION.DAGGLE);
//                    } else {
//                        smoothScroll(ACTION.CLICK);
//                    }
//                }
//                if (getParent() != null) {
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }
//                break;
//        }
//
//        invalidate();
//        return true;
//    }
//
//
//}
