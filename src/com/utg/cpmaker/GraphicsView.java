package com.utg.cpmaker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.MotionEvent;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;


public class GraphicsView extends View{
    float centerX = 0;
    float centerY = 0;
    float circleRadius = 10;
    float fScale;
    private Bitmap myBitmap;
    private Paint paint;
    double rX,rY =0;

    public GraphicsView(Context context, Bitmap bmp) {
        super(context);
        myBitmap = bmp;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(3.0f);
        myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        fScale =Math.min((float)canvas.getWidth() / myBitmap.getWidth() , (float)canvas.getHeight() / myBitmap.getHeight());
        canvas.save();
        canvas.scale(fScale , fScale );
        canvas.drawBitmap(myBitmap, 0, 0, null);
        canvas.restore();
        canvas.drawCircle(centerX, centerY, circleRadius, paint);
     }
    public boolean onTouchEvent(MotionEvent event)
    {
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



}
