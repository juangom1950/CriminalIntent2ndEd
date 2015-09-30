package bignerdranch2nded.com.criminalintent2nded;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
//import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by Juan on 9/16/2015.
 *
  Explanation: https://www.youtube.com/watch?v=iSczM1n4ieQ
 */
public class PictureUtils {

    //This method checks to see how big the screen is, and then scales the image down to that size
    //The ImageView you load into will always be smaller than this size, so this is a very conservative estimate
    public static Bitmap getScaledBitmap(String path, Activity activity) {

        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

       /* float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;*/
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        int inSampleSize = 1;
        int scaleFactor = 1;
        //int inSampleSize = 3;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            /*if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }*/
            scaleFactor = Math.min(srcWidth/destWidth, srcHeight/destHeight);
        }

        /*if (srcHeight > destHeight || srcWidth > destWidth) {

            final int halfHeight = (int) (srcHeight / 2);
            final int halfWidth = (int) (srcWidth / 2);

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > destHeight
                    && (halfWidth / inSampleSize) > destWidth) {
                inSampleSize *= 2;
            }
        }*/

        options = new BitmapFactory.Options();
        //options.inSampleSize = inSampleSize;
        options.inSampleSize = scaleFactor;

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    public static   Bitmap scaleDownAndRotatePic(String path) {//you can provide file path here
        int orientation;
        try {
            if (path == null) {
                return null;
            }
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 0;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);

            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Log.e("ExifInteface .........", "rotation =" + orientation);

            //exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

            Log.e("orientation", "" + orientation);
            Matrix m = new Matrix();

            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                //m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                // if(m.preRotate(90)){
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
                return bitmap;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
                return bitmap;
            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
