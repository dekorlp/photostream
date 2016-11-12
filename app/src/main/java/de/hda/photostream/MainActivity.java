package de.hda.photostream;

import android.app.Activity;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

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
    private FloatingActionButton fab;
    CoordinatorLayout bottomSheetLayout;
    String tag = MainActivity.class.getName();

    private static final int COLUMNS_PER_ROW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.activityMainRecyclerView);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS_PER_ROW));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new PhotoAdapter();
        mRecyclerView.setAdapter(mAdapter);

        bottomSheetLayout = (CoordinatorLayout) findViewById(R.id.bottom_sheet_Fotosource);

        fab = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new PictureSourceBottomSheetDialogFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

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
