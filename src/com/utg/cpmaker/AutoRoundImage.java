package com.utg.cpmaker;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AutoRoundImage extends BitmapDrawable {
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
        mBitmapWidth  =  mBitmapHeight = (mBitmap.getWidth()>mBitmap.getHeight())?mBitmap.getWidth():mBitmap.getHeight();
        bManual  = false;
    }

    public AutoRoundImage(Bitmap bitmap, float cX, float cY, float radius, float scaleFactor, boolean makeResize) {

        final BitmapShader shader;

        mBitmap = bitmap;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        fLeft   =(cX - radius) / scaleFactor;
        fRight  =(cX + radius) / scaleFactor;
        fBottom =(cY + radius) / scaleFactor;
        fTop    =(cY - radius) / scaleFactor;
        if (makeResize) {
            int iLeft = (int) fLeft;
            int iTop = (int) fTop;
            int iWidth = (int) (fRight - fLeft);
            int iHeight = (int) (fBottom - fTop);

            if (iLeft < 0) {
                iLeft = 1;
            }
            if (iLeft > bitmap.getWidth()) {
                iLeft = bitmap.getWidth() - 2;
            }
            if (iTop < 0) {
                iTop = 1;
            }
            if (iTop > bitmap.getHeight()) {
                iTop = bitmap.getHeight() - 2;
            }
            if (iLeft + iWidth >= bitmap.getWidth()) {
                iWidth = bitmap.getWidth() - iLeft;
            }
            if (iTop + iHeight >= bitmap.getHeight()) {
                iHeight = bitmap.getHeight() - iTop;
            }

            Bitmap tmpBmp = Bitmap.createBitmap(bitmap, iLeft, iTop, iWidth, iHeight);
            shader = new BitmapShader(tmpBmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapWidth  = tmpBmp.getWidth();
            mBitmapHeight = tmpBmp.getHeight();
            tmpBmp.recycle();
        }
        else { // don't need resize

            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapWidth  = mBitmap.getWidth();
            mBitmapHeight = mBitmap.getHeight();
            bManual  = true;
        }
        mPaint.setShader(shader);
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

    private ColorFilter createDimFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f);
        float scale = 0.5f;
        colorMatrix.setScale(scale, scale, scale, 1f);
        return new ColorMatrixColorFilter(colorMatrix);
    }
}