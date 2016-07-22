package com.kostya.ratingview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class RatingView extends View {

    private static final int DEFAULT_MAX_RATING = 5;
    private static final int DEFAULT_INNER_MARGIN = 14;
    private static final float DEFAULT_VIEW_SIZE = 50f;

    private Bitmap bitmap;
    private int viewSize;

    protected int maxRating;
    protected int fillColor;
    protected int defaultColor;
    protected int innerMargin;
    protected float rating;

    protected Paint defaultPaint;
    protected Paint fillPaint;
    private Rect drawRect = new Rect();

    protected PorterDuffXfermode srcATopPorterDuff = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    public RatingView(Context context) {
        super(context);
        init(context, null);
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public RatingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attributeSet) {
        setLayerType(LAYER_TYPE_SOFTWARE,  new Paint());
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultPaint.setStyle(Paint.Style.FILL);

        if (attributeSet != null) {
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.RatingView);
            maxRating = a.getInteger(R.styleable.RatingView_rv_max_rating, DEFAULT_MAX_RATING);
            innerMargin = (int) a.getDimension(R.styleable.RatingView_rv_inner_margin, DEFAULT_INNER_MARGIN);
            fillColor = a.getColor(R.styleable.RatingView_rv_fill_color, Color.YELLOW);
            defaultColor = a.getColor(R.styleable.RatingView_rv_default_color, Color.DKGRAY);
            viewSize = (int) a.getDimension(R.styleable.RatingView_rv_view_size, DEFAULT_VIEW_SIZE);
            a.recycle();
        } else {
            maxRating = DEFAULT_MAX_RATING;
            innerMargin = (int) Utils.pxFromDp(context, DEFAULT_INNER_MARGIN);
            fillColor = Color.YELLOW;
            defaultColor = Color.DKGRAY;
            viewSize = (int) Utils.pxFromDp(context, DEFAULT_VIEW_SIZE);
        }

        defaultPaint.setColor(defaultColor);
        fillPaint.setColor(fillColor);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_star);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (viewSize * maxRating) + (innerMargin * maxRating - 1);
        int height = viewSize;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect.set(0, 0, viewSize, viewSize);
        for(int i = 0; i < maxRating; i++) {
            if (i < (int)rating) {
                canvas.drawBitmap(bitmap, null, drawRect, fillPaint);
            } else if ((int)rating < (i + 1) && rating - i > 0) {
                float part = rating % 1;

                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas temp = new Canvas(mutableBitmap);

                fillPaint.setXfermode(srcATopPorterDuff);

                Rect fill = new Rect(0, 0, Math.round(mutableBitmap.getWidth() * part), mutableBitmap.getHeight());
                temp.drawRect(fill, fillPaint);

                canvas.drawBitmap(mutableBitmap, null, drawRect, null);
            } else {
                canvas.drawBitmap(bitmap, null, drawRect, defaultPaint);
            }
            drawRect.left = drawRect.right + innerMargin;
            drawRect.right = drawRect.left + viewSize;
        }
    }

    public void setRating(float rating) {
        if (rating > maxRating) throw new IllegalArgumentException("Rating can't be greater than max rating");
        if (rating < 0) throw new IllegalArgumentException("Rating can't be negative");

        this.rating = rating;
        invalidate();
    }
}