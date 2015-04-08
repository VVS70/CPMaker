package com.utg.cpmaker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.os.Environment;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.widget.TextView;


public class Main extends Activity {
    public static Bitmap myBitmap;
    public GraphicsView myView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView=new GraphicsView(this, myBitmap);
        setContentView(myView);
    }



    public boolean onCreateOptionsMenu(Menu menu) {
               menu.add(0,0,0,"Crop");
               menu.add(0,1,1,"AutoCrop");
               menu.add(0,2,1,"Reload");
               menu.add(0,3,1,"Save Image");

      return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int k=item.getItemId();
        switch (k) {
            case 0:{
                setContentView(R.layout.main);
                ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
                Bitmap rPicture = BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
                imageViewRound.setImageBitmap(rPicture);
                AutoRoundImage roundedImage = new AutoRoundImage(rPicture,myView.centerX,myView.centerY,myView.circleRadius,myView.fScale);
                imageViewRound.setImageDrawable(roundedImage);
                break;}
            case 1:{
                setContentView(R.layout.main);
                ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
                Bitmap rPicture = BitmapFactory.decodeResource(getResources(), R.drawable.pic1); //,options);
                imageViewRound.setImageBitmap(rPicture);
                AutoRoundImage roundedImage = new AutoRoundImage(rPicture);
                imageViewRound.setImageDrawable(roundedImage);
                break;}
            case 2:{



                break;}
            case 3:{

                String folderToSave =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/";

                ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
                TextView txtView = (TextView)findViewById(R.id.textView);

                OutputStream fOut = null;
                try {

                    File file = new File(folderToSave,"RoundedPic"+
                            Long.toHexString(System.currentTimeMillis())+".jpg");

                    fOut = new FileOutputStream(file);
                    imageViewRound.buildDrawingCache();
                    imageViewRound.setDrawingCacheEnabled(true);
                    Bitmap bitmap = imageViewRound.getDrawingCache();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

                    fOut.flush();
                    fOut.close();

                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            file.getAbsolutePath(),
                            file.getName(),
                            file.getName());

                    txtView.setText("Image saved!");
                }
                catch (Exception e)
                {
                    txtView.setText(e.getMessage().toString());
                }

                imageViewRound.getDrawingCache(true);

                break;}
        }
        return super.onOptionsItemSelected(item);
    }
}