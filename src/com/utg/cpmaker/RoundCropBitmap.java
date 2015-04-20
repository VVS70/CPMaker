package com.utg.cpmaker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Color;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;

public class RoundCropBitmap {

    private RoundCropBitmap() {}

    public static Bitmap Create (Bitmap bitmap, float cX, float cY, float radius, float scaleFactor, boolean makeResize) {

        float fLeft   =(cX - radius) / scaleFactor;
        float fRight  =(cX + radius) / scaleFactor;
        float fBottom =(cY + radius) / scaleFactor;
        float fTop    =(cY - radius) / scaleFactor;

        int iLeft = (int) fLeft;
        int iTop = (int) fTop;
        int iWidth = (int) (fRight - fLeft);
        int iHeight = (int) (fBottom - fTop);

        Bitmap tmpBmp = Bitmap.createBitmap(bitmap, iLeft, iTop, iWidth, iHeight);
        Bitmap outputBitmap = Bitmap.createBitmap(iWidth, iHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(iLeft,iTop,iWidth,iHeight);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawCircle(cX, cY, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(tmpBmp, rect, rect, paint);
        tmpBmp.recycle();

        return outputBitmap;
    }

}
