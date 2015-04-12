package com.utg.cpmaker;

import android.app.Activity;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.os.Environment;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.*;
import com.utg.cpmaker.ColorPicker.ColorPicker;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.provider.MediaStore;


public class Main extends Activity {
    public GraphicsView myView;
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        //Button btnGallery = (Button) findViewById(R.id.btnGallery);
        //Button btnCamera  = (Button) findViewById(R.id.btnGallery);
        //myView=new GraphicsView(this,"");
        //setContentView(myView);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
               menu.add(Menu.FIRST,0,0,"Crop image");
               menu.add(Menu.FIRST,4,0,"Crop&Resize");
               menu.add(Menu.FIRST,1,1,"AutoCrop");
               menu.add(Menu.FIRST, 2, 2, "Try again");
               menu.add(Menu.FIRST, 3, 3, "Save Image");
      return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuKey=item.getItemId();
        switch (menuKey) {
            case 0: case 4:{ // manual crop image (0) ||  manual crop&resize (4)
                setContentView(R.layout.main);
                ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
                Bitmap rPicture = myView.getOriginBitmap();
                                                         //BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
                imageViewRound.setImageBitmap(rPicture);
                AutoRoundImage roundedImage = new AutoRoundImage(rPicture,myView.centerX,myView.centerY,myView.circleRadius,myView.fScale,(menuKey==4));
                imageViewRound.setImageDrawable(roundedImage);
                ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
                colorPicker.setColor(Color.BLACK);
                colorPicker.setMonitor(imageViewRound);
                break;}
            case 1:{ // auto crop image
                setContentView(R.layout.main);
                ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
                Bitmap rPicture = myView.getOriginBitmap();
                imageViewRound.setImageBitmap(rPicture);
                AutoRoundImage roundedImage = new AutoRoundImage(rPicture);
                imageViewRound.setImageDrawable(roundedImage);
                ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
                colorPicker.setColor(Color.BLACK);
                colorPicker.setMonitor(imageViewRound);
                break;}
            case 2:{ //reload image

                setContentView(myView);

                break;}
            case 3:{  //save image

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

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);

                    fOut.flush();
                    fOut.close();
                    //TODO: Gallery does not display the saved image. Why?!
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

        public void selectPicture(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PICTURE_ACTIVITY_REQUEST_CODE);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

            switch (requestCode) {
                case SELECT_PICTURE_ACTIVITY_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        Uri selectedImage = imageReturnedIntent.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String filePath = cursor.getString(columnIndex);
                            myView=new GraphicsView(this,filePath);
                            setContentView(myView);
                        }
                        cursor.close();
                    }
                    break;
            }
        }


    }