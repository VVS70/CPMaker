package com.utg.cpmaker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;

public class GraphicsView extends ImageView{
    float centerX = 0;
    float centerY = 0;
    float circleRadius = 10;
    float fScale;
    private Bitmap sourceBitmap;
    private Paint paint;
    private int scale;
    double rX,rY =0;
    private static String fullFileName;
    private static Point point;

    public static void setParam(String filePath, Point size) {
        fullFileName = filePath;
        point = size;
    }

    public GraphicsView(Context context, AttributeSet attrs, int defStyle) {
        this(context);
    }

    public GraphicsView(Context context, AttributeSet attrs) {
        this(context);

    }

    public GraphicsView(Context context){
        super(context,null,0);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(3.0f);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fullFileName, options);

        int imageHeight = options.outHeight;
        int imageWidth  = options.outWidth;

        int viewHeight  = point.x;
        int viewWidth   = point.y;

        int halfHeight  = imageHeight / 2;
        int halfWidth   = imageWidth / 2;
        scale = 1;
        if ((imageHeight>viewHeight)||(imageWidth>viewWidth)){
            while ((halfHeight / scale) > viewHeight&&(halfWidth / scale) > viewWidth){
                scale *= 2;
            }
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        sourceBitmap = BitmapFactory.decodeFile(fullFileName, options);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.save();
        if (sourceBitmap!=null) {
            fScale = Math.min((float) canvas.getWidth() / sourceBitmap.getWidth(), (float) canvas.getHeight() / sourceBitmap.getHeight());
            canvas.scale(fScale, fScale);
            canvas.drawBitmap(sourceBitmap, 0, 0, null);
        }
        canvas.restore();
        canvas.drawCircle(centerX, centerY, circleRadius, paint);
    }

    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            centerX = event.getX();
            centerY = event.getY();
            circleRadius =10;
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            rX=event.getX()-centerX;
            rY=event.getY()-centerY;
            circleRadius = (float) (Math.sqrt(Math.pow(rX, 2) + Math.pow(rY,2)));
            invalidate();
        }
        return true;
    }

    public Bitmap getOriginBitmap (){
        return sourceBitmap;
    }

    public String getFullFileName()  {
        return fullFileName;
    }

    public int getScale() {
        return scale;
    }

    public float getfScale() {
        return fScale;
    }

    public void setImage(Bitmap bitmap) {
        sourceBitmap=bitmap;
        this.invalidate();
    }
}

