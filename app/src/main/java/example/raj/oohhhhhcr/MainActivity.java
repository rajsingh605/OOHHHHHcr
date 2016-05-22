package example.raj.oohhhhhcr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_FILE = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int PIC_CROP = 3;
    public static final String EXTRA_MESSAGE = "BTP";

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

    public void trainData(Bitmap bm) {
        Context context = getApplicationContext();
        String Data_Path = context.getFilesDir().toString() + "/OCR/";
        String tessPath = context.getFilesDir().toString() + "/OCR/tessdata/";
        File dir = new File(Data_Path);
        if (!dir.exists()) {
            if(!dir.mkdirs())
                System.out.println("Failed");
        }
        dir = new File(tessPath);
        if (!dir.exists()) {
            if(!dir.mkdirs())
                System.out.println("Failed");
        }
        if (!(new File(Data_Path + "tessdata/" + "eng" + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + "eng" + ".traineddata");
                OutputStream out = new FileOutputStream(Data_Path + "tessdata/" + "eng" + ".traineddata");
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(Data_Path, "eng");
        baseApi.setImage(bm);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();
        Intent intent = new Intent(this, DisplayText.class);
        intent.putExtra(EXTRA_MESSAGE, recognizedText);
        startActivity(intent);
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
        Context context = getApplicationContext();
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                //Uri picUri = data.getData();
                //performCrop(picUri);
                Bitmap thumbnail =  (Bitmap) data.getExtras().get("data");
                /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(context.getFilesDir(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                if (ivImage != null)
                    ivImage.setImageBitmap(thumbnail);
                trainData(thumbnail);
            }
            else if (requestCode == SELECT_FILE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bm = null;
                try {
                    bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (ivImage != null)
                    ivImage.setImageBitmap(bm);
                trainData(bm);
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
