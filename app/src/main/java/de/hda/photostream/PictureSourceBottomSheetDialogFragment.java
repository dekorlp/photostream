package de.hda.photostream;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;

/**
 * Created by Dennis on 12.11.2016.
 */

public class PictureSourceBottomSheetDialogFragment  extends BottomSheetDialogFragment{

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet, null);
        dialog.setContentView(contentView);
    }
}
