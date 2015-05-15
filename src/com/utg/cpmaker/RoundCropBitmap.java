package com.utg.cpmaker;

import android.graphics.*;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class RoundCropBitmap {
    private static Bitmap outputBitmap;
    private RoundCropBitmap() {}

    public static String Create (String filePath,
                                 float cX,
                                 float cY,
                                 float radius,
                                 int imageScaleFactor,
                                 float scaleFactor,
                                 boolean makeResize) {

        float fLeft;
        float fRight;
        float fBottom;
        float fTop;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        int imageHeight = options.outHeight;
        int imageWidth  = options.outWidth;

        long heapPad=(long) Math.max(4*1024*1024,Runtime.getRuntime().maxMemory()*0.1);
        long allocNativeHeap = Debug.getNativeHeapAllocatedSize();
        long maxMemory =  Runtime.getRuntime().maxMemory();

        int halfHeight  = imageHeight / 2;
        int halfWidth   = imageWidth / 2;
        int scale = 1;

        //4 bits per pixel in memory. We need 2 bitmap in memory
        if (imageHeight*imageWidth*4>maxMemory/2){
            scale = 2;
            while (((halfHeight/scale)*(halfWidth/scale)*4 + + allocNativeHeap + heapPad)>(maxMemory/2)){
                scale *= 2;
            }
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap  bitmap = BitmapFactory.decodeFile(filePath, options);

          fLeft   = (cX - radius) *  imageScaleFactor / scaleFactor / scale;
          fRight  = (cX + radius) *  imageScaleFactor / scaleFactor / scale;
          fBottom = (cY + radius) *  imageScaleFactor / scaleFactor / scale;
          fTop    = (cY - radius) *  imageScaleFactor / scaleFactor / scale;
          radius = radius * imageScaleFactor / scaleFactor / scale;

        int iLeft = (int) fLeft;
        int iTop = (int) fTop;
        int iWidth = (int) (fRight - fLeft);
        int iHeight = (int) (fBottom - fTop);

        Bitmap tmpBmp = Bitmap.createBitmap(bitmap, iLeft, iTop, iWidth, iHeight);
        bitmap.recycle();

        outputBitmap = Bitmap.createBitmap(iWidth, iHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0,0,iWidth,iHeight);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);


        //TODO android.graphics.BitmapRegionDecoder

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawCircle(iWidth / 2, iHeight / 2, radius-1, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(tmpBmp, rect, rect, paint);
        tmpBmp.recycle();
        return SaveImageToFile ();
    }

    public static Boolean SaveResult (String filePath, String resultPath, int color){

      Bitmap  bitmap = BitmapFactory.decodeFile(filePath);
        Bitmap  bkgBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight() , Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bkgBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        paint.setStrokeWidth(0);
        canvas.drawRect(0, 0, bitmap.getWidth(),bitmap.getHeight(), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();

        OutputStream fOut = null;
        String fileName = resultPath+".jpg";
        File file = new File(fileName);
        try {
            fOut = new FileOutputStream(file);
            bkgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        }
        catch (Exception e) {
            //e.getMessage();
            return false;
        }
        finally {
            bkgBitmap.recycle();
        }

    return true;
    }

    public static String SaveImageToFile (){
        String folderToSave =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/";
        OutputStream fOut = null;
        String fileName = folderToSave+"TmpRndPic"+Long.toHexString(System.currentTimeMillis())+".png";
        File file = new File(fileName);
        try {
            fOut = new FileOutputStream(file);
            outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        }
        catch (Exception e) {
            return e.getMessage();
        }
        finally {
            outputBitmap.recycle();
        }
        return fileName;
    }


}
