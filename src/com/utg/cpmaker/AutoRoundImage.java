package com.utg.cpmaker;

/**
 * Created by VVS on 06.04.2015.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AutoRoundImage extends Drawable {
    private  Bitmap mBitmap;
    private  Paint mPaint;
    private  RectF mRectF;
    private  int mBitmapWidth;
    private  int mBitmapHeight;
    private  float fLeft, fTop, fRight, fBottom = 0;
    private  boolean bManual  = false;

    public AutoRoundImage(Bitmap bitmap) {
        mBitmap = bitmap;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        mBitmapWidth  = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();
        bManual  = false;
    }
    public AutoRoundImage(Bitmap bitmap, float cX, float cY, float radius, float scaleFactor) {
        mBitmap = bitmap;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        mBitmapWidth  = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();

        fLeft =(cX - radius) / scaleFactor;
        fTop =(cY + radius) / scaleFactor;
        fRight =(cX + radius) / scaleFactor;
        fBottom =(cY - radius) / scaleFactor;

        bManual  = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (bManual) {
            mRectF.set(fLeft, fTop, fRight, fBottom);
        }

        canvas.drawOval(mRectF, mPaint);
    }
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRectF.set(bounds);
    }
    @Override
    public void setAlpha(int alpha) {
        if (mPaint.getAlpha() != alpha) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }
    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }
    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }
    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }
    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }
    public void setAntiAlias(boolean aa) {
        mPaint.setAntiAlias(aa);
        invalidateSelf();
    }
    @Override
    public void setFilterBitmap(boolean filter) {
        mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }
    @Override
    public void setDither(boolean dither) {
        mPaint.setDither(dither);
        invalidateSelf();
    }
    public Bitmap getBitmap() {
        return mBitmap;
    }

}