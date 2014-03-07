/*
 * Copyright (C) 2012 GM Inc.
 * All Rights Reserved.
 * GM Confidential Restricted.
 */

package com.amalbit.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.regex.Pattern;

public class IndexerBar extends View {

    private static final String PATTERN = "^[A-Za-z]+$";

    private static final String HASH_MARK = "#";

    private static final int TEXT_SIZE = 15;

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    private String[] mIndexer = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"
            , "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private int mChoose = -1;

    private Paint mPaint = new Paint();

    boolean mShowBkg = false;

    public IndexerBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IndexerBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexerBar(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowBkg) {
            canvas.drawColor(getContext().getResources().getColor(R.color.indexer_bar_bg_color));
        }
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();

        float density = dm.density;
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / mIndexer.length;
        for (int i = 0; i < mIndexer.length; i++) {
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(density * TEXT_SIZE);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setAntiAlias(true);
            if (i == mChoose) {
                mPaint.setColor(getContext().getResources().getColor(
                        R.color.indexer_bar_selected_color));
                mPaint.setFakeBoldText(true);
            }
            float xPos = width / 2 - mPaint.measureText(mIndexer[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(mIndexer[i], xPos, yPos, mPaint);
            mPaint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = mChoose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int characterIndex = (int) (y / getHeight() * mIndexer.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mShowBkg = true;
                if (oldChoose != characterIndex && listener != null) {
                    if (characterIndex >= 0 && characterIndex < mIndexer.length) {
                        listener.onTouchingLetterChanged(mIndexer[characterIndex]);
                        mChoose = characterIndex;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != characterIndex && listener != null) {
                    if (characterIndex >= 0 && characterIndex < mIndexer.length) {
                        listener.onTouchingLetterChanged(mIndexer[characterIndex]);
                        mChoose = characterIndex;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mShowBkg = false;
                mChoose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    public static String getAlpha(String str) {
        if (str == null) {
            return HASH_MARK;
        }

        if (str.trim().length() == 0) {
            return HASH_MARK;
        }

        String firstChar = str.trim().substring(0, 1);
        Pattern pattern = Pattern.compile(PATTERN);
        if (pattern.matcher(firstChar).matches()) {
            return firstChar.toUpperCase();
        } else {
            return HASH_MARK;
        }
    }

}
