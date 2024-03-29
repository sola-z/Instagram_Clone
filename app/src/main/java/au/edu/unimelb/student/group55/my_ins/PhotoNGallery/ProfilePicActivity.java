package au.edu.unimelb.student.group55.my_ins.PhotoNGallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.InputStream;

import au.edu.unimelb.student.group55.my_ins.Profile.EditProfileActivity;
import au.edu.unimelb.student.group55.my_ins.Profile.ProfileActivity;
import au.edu.unimelb.student.group55.my_ins.R;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;


// This class allows user to either select a photo from library or take a photo with camera
// User can crop their image and apply filters on the image
// After these procedures, the brightness and contrast of the image can also be changed
// the final image is stored in the external storage and the path of the image is passed to photo upload service

public class ProfilePicActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap selectedImage;
    private String FILE_NAME = "/test.jpg";
    public static final String WORKING_DIRECTORY = "MyINS/test.jpg";
    private String imagePath;

    private String postMessage;

    public String cropPath;
    private static final String TAG = "ProfilePic Activity";
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.profilepic_gallery );
        context = ProfilePicActivity.this;

        imagePath = applicationFolder();
        imageView = (ImageView) findViewById(R.id.profile_pic);

        TextView shareClose = (TextView) findViewById(R.id.gallery_cancel);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    public String applicationFolder(){

        //change the result image name to ensure image is stored properly
        String temp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "test.jpg";
        return temp;
    }

    public void startImageFilter(String sourcePath, String destinationPath){
        EditImageActivity.start(this, sourcePath, destinationPath, 100, true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get the result image path from image crop procedure

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try{
                    cropPath = resultUri.getPath();
                    // After having cropped the image, start the next procedure
                    // start to add filters etc.
                    startImageFilter( cropPath, imagePath );
                }
                catch(Exception e){
                    Toast.makeText(context, "failed to read image data", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
            else{
                finish();
            }
        }

        // After having finished the filters & brightness contrast setting
        // return the image data to image view
        if (requestCode == 100) { // same code you used while starting

            // Set the corresponding factors of the library class
            String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //Read the image to bitmap from storage
            if(isImageEdit == false){
                selectedImage = BitmapFactory.decodeFile(cropPath, options);
                imagePath = cropPath;
            }
            else{
                selectedImage = BitmapFactory.decodeFile(newFilePath, options);
                imagePath = newFilePath;
            }


            imageView.setImageBitmap( selectedImage );
            changeProfilePic( selectedImage );
        }

    }

    public void changeProfilePic(Bitmap selectedImage){
        TextView nextScreen = (TextView) findViewById(R.id.gallery_next);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent( context, EditProfileActivity.class );
                    intent.putExtra( "profilePicPath", imagePath );
                    Log.d(TAG,"imagePath: " + imagePath);
                    startActivity(intent);
                    finish();
                }
        });
    }
}
