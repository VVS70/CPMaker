package com.utg.cpmaker;

import android.app.Activity;
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

public class Main extends Activity {

    public GraphicsView myView;

    //Constants
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 0;
    private static final int TAKE_PHOTO = 1;
    private static final String CAMERA_FILE_PREFIX = "IMG_";
    private static final String CAMERA_FILE_EXTENSION = ".jpg";
    private static String photoPath = null;
    private static AutoRoundImage roundedImage = null;

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
               menu.add(Menu.FIRST, 0, 0, "Crop image");
               menu.add(Menu.FIRST, 4, 0, "Crop&Resize");
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
                imageViewRound.setImageBitmap(rPicture);
                //roundedImage = new AutoRoundImage(rPicture,myView.centerX,myView.centerY,myView.circleRadius,myView.fScale,(menuKey==4));
                //imageViewRound.setImageDrawable(roundedImage);
                Bitmap rImage = RoundCropBitmap.Create(rPicture,myView.centerX,myView.centerY,myView.circleRadius,myView.fScale,(menuKey==4));
                imageViewRound.setImageBitmap(rImage);
                ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
                colorPicker.setColor(Color.BLACK);
                colorPicker.setMonitor(imageViewRound);
                break;}
            case 1:{ // auto crop image
                setContentView(R.layout.main);
                ImageView imageViewRound = (ImageView) findViewById(R.id.imageView_round);
                Bitmap rPicture = myView.getOriginBitmap();
                imageViewRound.setImageBitmap(rPicture);
                roundedImage = new AutoRoundImage(rPicture);
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
                    String fileName = folderToSave+"RndPic"+Long.toHexString(System.currentTimeMillis())+".jpg";
                    File file = new File(fileName);

                    fOut = new FileOutputStream(file);

                    imageViewRound.buildDrawingCache();
                    imageViewRound.setDrawingCacheEnabled(true);

                    Bitmap bitmap = imageViewRound.getDrawingCache(false);
                    //Bitmap bitmap = roundedImage.getBitmap();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

                    fOut.flush();
                    fOut.close();

                    galleryAddPic(fileName);

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
                            myView=new GraphicsView(this,filePath);
                            setContentView(myView);
                        }
                        cursor.close();
                    }
                    break;
                case TAKE_PHOTO:
                    if (resultCode == RESULT_OK) {

                         if (photoPath!=null) {
                            myView=new GraphicsView(this,photoPath);
                            setContentView(myView);
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
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }



}