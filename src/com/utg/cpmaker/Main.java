package com.utg.cpmaker;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.*;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.provider.MediaStore;
import android.graphics.Color;
import com.utg.cpmaker.ColorPicker.ColorPicker;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.util.Log;
import android.view.Display;
import android.graphics.Point;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;


public class Main extends Activity {

    private Point size;
    //Constants
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 0;
    private static final int TAKE_PHOTO = 1;
    private static final String CAMERA_FILE_PREFIX = "IMG_";
    private static final String CAMERA_FILE_EXTENSION = ".jpg";
    private static String photoPath = null;
    private static AutoRoundImage roundedImage = null;
    private String tmpFile ="";
    private int menuKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        */

        size = new Point();
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        size.set((int)((dpWidth-30)*displayMetrics.density),(int)((dpHeight-184)*displayMetrics.density));
        setContentView(R.layout.dialog);

        //myView=new GraphicsView(this,"");
        //setContentView(myView);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
               menu.add(Menu.FIRST, 0, 0, "Crop image");
               menu.add(Menu.FIRST, 4, 0, "Crop&Resize");
               menu.add(Menu.FIRST, 1, 1, "AutoCrop");
               menu.add(Menu.FIRST, 2, 2, "Try again");
               menu.add(Menu.FIRST, 3, 3, "Save Image");
      return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuKey=item.getItemId();
        switch (menuKey) {
            case 0: case 4:{ // manual crop image (0) ||  manual crop&resize (4)
/*                Bitmap rPicture = myView.getOriginBitmap();
                setContentView(R.layout.main);
                ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
                AutoRoundImage roundedImage = new AutoRoundImage(rPicture,myView.centerX,myView.centerY,myView.circleRadius,myView.fScale,(menuKey==4));
                imageViewRound.setImageDrawable(roundedImage);

                ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
                colorPicker.setColor(Color.BLACK);
                colorPicker.setMonitor(imageViewRound);
*/
                break;}
            case 1:{ // auto crop image
/*                setContentView(R.layout.main);
                ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
                Bitmap rPicture = myView.getOriginBitmap();
                imageViewRound.setImageBitmap(rPicture);
                roundedImage = new AutoRoundImage(rPicture);
                imageViewRound.setImageDrawable(roundedImage);
                ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
                colorPicker.setColor(Color.BLACK);
                colorPicker.setMonitor(imageViewRound);
*/
                break;}
            case 2:{ //reload image

                setContentView(R.layout.main);

                break;}
            case 3:{  //save image

                String folderToSave =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/";
                /*
                ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
                TextView txtView = (TextView)findViewById(R.id.textView);
                */
  /*
                tmpFile =
                        RoundCropBitmap.Create(myView.getFullFileName(),
                                myView.centerX,
                                myView.centerY,
                                myView.circleRadius,
                                myView.getScale(),
                                myView.getfScale(),
                                (menuKey == 4));

                String fileName = folderToSave+"RndPic"+Long.toHexString(System.currentTimeMillis());
                ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);

                if (RoundCropBitmap.SaveResult(tmpFile,fileName,colorPicker.getColor())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                    builder.setTitle("  File saved!")
                            .setMessage("Name: "+fileName)
                            .setIcon(R.drawable.ic_info)
                            .setCancelable(false)
                            .setNegativeButton("ОК",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                File file = new File(tmpFile);
                if (file.exists()) { file.delete();};
                //deleteFile(tmpFile);
   */
                break;}
        }
        return super.onOptionsItemSelected(item);
    }

        public void selectPicture(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PICTURE_ACTIVITY_REQUEST_CODE);
        }

        public void getFromCamera(View v){
            try {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                 File photo = null;
                  try {
                  photo = createImageFile();
                  } catch (IOException e) {
                      e.printStackTrace();
                      photo = null;
                      photoPath = null;
                  }
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                startActivityForResult(captureIntent, TAKE_PHOTO);
            } catch (Exception e) {
                String errorMessage = "Camera don't work:(";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
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
                            //setContentView(myView);
                            GraphicsView.setParam(filePath, size);
                            setContentView(R.layout.edit_layout);
                        }
                        cursor.close();
                    }
                    break;
                case TAKE_PHOTO:
                    if (resultCode == RESULT_OK) {

                         if (photoPath!=null) {
                            GraphicsView.setParam(photoPath, size);
                            GraphicsView myView= (GraphicsView) findViewById(R.id.resView);
                            //new GraphicsView(this);
                            //setContentView(myView);
                            setContentView(R.layout.edit_layout);
                         }
                    }
                    break;
            }
        }
    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = CAMERA_FILE_PREFIX + timeStamp + "_";
        File image = File.createTempFile(
                imageFileName,
                CAMERA_FILE_EXTENSION,
                getAlbumDir()
        );
        photoPath = image.getAbsolutePath();
        return image;
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = Environment.getExternalStoragePublicDirectory(
                         Environment.DIRECTORY_PICTURES);
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("Camera: ", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted for READ/WRITE.");
        }
        return storageDir;
    }

    public void cropImageClick(View v){
        GraphicsView myView= (GraphicsView) findViewById(R.id.resView);
        // TODO  Error "Could not execute method of the activity" WTF?!
        Bitmap rPicture = myView.getOriginBitmap();

        /*
        setContentView(R.layout.main);
        ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
        AutoRoundImage roundedImage = new AutoRoundImage(rPicture,myView.centerX,myView.centerY,myView.circleRadius,myView.fScale,(menuKey==4));
        imageViewRound.setImageDrawable(roundedImage);
         */
        AutoRoundImage roundedImage = new AutoRoundImage(rPicture,myView.centerX,myView.centerY,myView.circleRadius,myView.fScale,false);
         myView.setImage(roundedImage.getBitmap());


        ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
        colorPicker.setColor(Color.BLACK);
        //colorPicker.setMonitor(imageViewRound);
        colorPicker.setMonitor(myView);
    }
}

