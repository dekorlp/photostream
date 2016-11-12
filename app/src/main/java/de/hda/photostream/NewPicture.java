package de.hda.photostream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;

public class NewPicture extends AppCompatActivity {

    ImageView btpPreview;
    Button btnTeilen;
    Bitmap bitmap;
    EditText edtText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_picture);

        btpPreview = (ImageView) findViewById(R.id.imvPreview);
        btnTeilen = (Button) findViewById(R.id.btnTeilen);
        edtText = (EditText) findViewById(R.id.etDescription);

        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("imvImage");

        bitmap = (Bitmap) BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        btpPreview.setImageBitmap(bitmap);

        btnTeilen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();

                returnIntent.putExtra("imvImage", bytes);
                returnIntent.putExtra("edtDescription", edtText.getText());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
