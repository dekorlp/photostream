package de.hda.photostream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.adapter.BasePhotoAdapter;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnRequestListener;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;


public class MainActivity extends  PhotoStreamActivity implements OnPhotosReceivedListener, OnRequestListener, OnNewPhotoReceivedListener {


    private RecyclerView mRecyclerView;
    //private RecyclerView.LayoutManager mLayoutManager;
    private PhotoAdapter mAdapter;
    String tag = MainActivity.class.getName();

    private static final int COLUMNS_PER_ROW = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int NEW_PICTURE_REQUEST = 2;
    static final int GALLERY_PIC_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.activityMainRecyclerView);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS_PER_ROW));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();



        if(Intent.ACTION_SEND.equals(action) && type != null)
        {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                } catch (IOException ex)
                {
                    Toast.makeText(this, "Photo konnte nicht gewählt werden!", Toast.LENGTH_LONG).show();
                }

                if(imageBitmap != null) {
                    Intent newPicture = new Intent(MainActivity.this, NewPicture.class);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    newPicture.putExtra("imvImage",bytes);
                    startActivityForResult(newPicture, NEW_PICTURE_REQUEST);
                }
            }
        }

        mAdapter = new PhotoAdapter();

        mAdapter.setOnItemClickListener(R.id.imbDeletePicture, new BasePhotoAdapter.OnItemClickListener<PhotoAdapter.PhotoViewHolder>()
        {

            @Override
            public void onItemClicked(PhotoAdapter.PhotoViewHolder viewHolder, View v, Photo photo) {
                getPhotoStreamClient().deletePhoto(photo.getId());
                getPhotoStreamClient().loadPhotos();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    public void openBottomSheet (View v) {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        TextView txtKamera = (TextView) view.findViewById(R.id.txt_kamera);
        TextView txtGallery = (TextView) view.findViewById(R.id.txt_gallery);

        final Dialog mBottomSheetDialog = new Dialog(MainActivity.this,
                R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        txtKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                mBottomSheetDialog.dismiss();
            }
        });

        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                takePictureIntent.setType("image/*");
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser( takePictureIntent, "Photoauswahl"), GALLERY_PIC_REQUEST);
                }
                mBottomSheetDialog.dismiss();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Intent newPicture = new Intent(MainActivity.this, NewPicture.class);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            newPicture.putExtra("imvImage", bytes);
            startActivityForResult(newPicture, NEW_PICTURE_REQUEST);
        }

        if(requestCode == GALLERY_PIC_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            Uri uri = data.getData();


            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            } catch (IOException ex)
            {
                Toast.makeText(this, "Photo konnte nicht gewählt werden!", Toast.LENGTH_LONG).show();
            }

            if(imageBitmap != null) {
                Intent newPicture = new Intent(MainActivity.this, NewPicture.class);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                newPicture.putExtra("imvImage",bytes);
                startActivityForResult(newPicture, NEW_PICTURE_REQUEST);
            }

        }

        if(requestCode == NEW_PICTURE_REQUEST && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            byte[] bytes =  extras.getByteArray("imvImage");
            Bitmap imageBitmap = (Bitmap) BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            byte[] imageBytes = BitmapUtils.bitmapToBytes(imageBitmap);
            String description = extras.get("edtDescription").toString();

            IPhotoStreamClient photoStreamClient = getPhotoStreamClient();

            try {
                photoStreamClient.uploadPhoto( imageBytes, description);
            } catch(IOException ex)
            {
                Toast.makeText(this, "Photo konnte nicht hochgeladen werden!", Toast.LENGTH_LONG).show();
            } catch(JSONException jex)
            {
                Toast.makeText(this, "Photo konnte nicht hochgeladen werden!", Toast.LENGTH_LONG).show();
            }
            getPhotoStreamClient().loadPhotos();
        }
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnRequestListener(this, RequestType.SEARCH_PHOTOS);
        photoStreamClient.addOnPhotosReceivedListener(this);

        if(savedInstanceState == null)
        {
            photoStreamClient.loadPhotos();
        }

    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnRequestListener(this);
        photoStreamClient.removeOnPhotosReceivedListener(this);
    }

    @Override
    public void onPhotosReceived(PhotoQueryResult result) {
        List<Photo> photos = result.getPhotos();
        if(result.isFirstPage())
        {
            mAdapter.set(photos);
        }
        else
        {
            mAdapter.addAll(photos);
        }
    }

    @Override
    public void onReceivePhotosFailed(HttpError httpError) {
        String title = "Could not load photos";
        Log.e(tag, title);
    }

    @Override
    public void onNoNewPhotosAvailable() {

    }

    @Override
    public void onRequestStarted() {

    }

    @Override
    public void onRequestFinished() {

    }

    @Override
    public void onNewPhotoReceived(Photo photo) {
        mAdapter.addAtFront(photo);
    }
}
