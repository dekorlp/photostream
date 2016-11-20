package de.hda.photostream;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import hochschuledarmstadt.photostream_tools.adapter.BasePhotoAdapter;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by dennis on 05.11.16.
 */

public class PhotoAdapter extends BasePhotoAdapter<PhotoAdapter.PhotoViewHolder> {

    private static final float BEGIN_SCALE = 0.5f, BEGIN_ALPHA = 0.1f, MAX = 1.0f;
    private static final int DURATION_IN_MILLIS = 500;
    public static ImageButton imbDeletePhoto;

    public static final class PhotoViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public TextView textViewDescription;
        public TextView textViewAnzahlKommentare;


        public PhotoViewHolder(View itemView)
        {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            textViewAnzahlKommentare = (TextView) itemView.findViewById(R.id.textViewAnzahlKommentare);
            imbDeletePhoto = (ImageButton) itemView.findViewById(R.id.imbDeletePicture);
        }

    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position)
    {
        super.onBindViewHolder(holder, position);
        Photo photo = getItemAtPosition(position);
        holder.textViewDescription.setText(photo.getDescription());
        holder.textViewAnzahlKommentare.setText(photo.getCommentCount() + " Kommentare");
        loadBitmapIntoImageViewAsync(holder, holder.imageView, photo);

        if(photo.isDeleteable())
        {
            imbDeletePhoto.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onBitmapLoadedIntoImageView(ImageView imageView) {
        imageView.setScaleX(BEGIN_SCALE);
        imageView.setScaleY(BEGIN_SCALE);
        imageView.setAlpha(BEGIN_ALPHA);
        imageView.animate().scaleX(MAX).scaleY(MAX).alpha(MAX).setDuration(DURATION_IN_MILLIS).start();
    }




}
