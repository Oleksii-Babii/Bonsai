package org.tudublin.bonsaiapp.util;

import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.tudublin.bonsaiapp.R;
import org.tudublin.bonsaiapp.model.Tree;

public final class ImageUtils {

    private static final String TAG = "ImageUtils";

    private ImageUtils() {}

    public static void loadTreeImage(ImageView view, Tree tree) {
        if (tree.getImageData() != null && !tree.getImageData().isEmpty()) {
            try {
                byte[] bytes = Base64.decode(tree.getImageData(), Base64.DEFAULT);
                Glide.with(view.getContext())
                        .load(bytes)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_tree_placeholder)
                        .centerCrop()
                        .into(view);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Invalid base64 image for tree " + tree.getId(), e);
                view.setImageResource(R.drawable.ic_tree_placeholder);
            }
        } else {
            String url = tree.getImageUrl();
            if ((url == null || url.isEmpty()) && tree.getSpecies() != null) {
                url = tree.getSpecies().getImageUrl();
            }
            if (url != null && !url.isEmpty()) {
                Glide.with(view.getContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_tree_placeholder)
                        .centerCrop()
                        .into(view);
            } else {
                view.setImageResource(R.drawable.ic_tree_placeholder);
            }
        }
    }
}
