package example.raj.oohhhhhcr;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_FILE = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int PIC_CROP = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gallBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }

    public void camBtn(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void performCrop(Uri picUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setData(picUri);
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, PIC_CROP);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Uri picUri = data.getData();
                performCrop(picUri);
               /* Bundle extra = data.getExtras();
                Bitmap thumbnail =  extra.getParcelable("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                Context context = getApplicationContext();
                File destination = new File(context.getFilesDir(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ivImage != null)
                    ivImage.setImageBitmap(thumbnail);*/
            }
            else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                /*String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                //options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);*/
                if (ivImage != null)
                    ivImage.setImageURI(selectedImageUri);
                else
                    System.out.println("Not able to take From Gallery");
            }
            else if(requestCode == PIC_CROP) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                if (ivImage != null)
                    ivImage.setImageBitmap(thePic);
            }
        }
    }
}
