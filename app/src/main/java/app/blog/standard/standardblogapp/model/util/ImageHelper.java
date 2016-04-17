package app.blog.standard.standardblogapp.model.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import app.blog.standard.standardblogapp.R;

/**
 * Helper class to deal with image-related problems.
 *
 * @author victor
 */
public class ImageHelper {

    /**
     * Takes a Bitmap and save it as in a JPEG image, and returns its absolute path.
     *
     * @param bitmap Bitmap to be saved as image
     * @return Image's path
     */
    public static String saveImage(Bitmap bitmap) {
        String directory = Environment.getExternalStorageDirectory().getPath() +
                "/" + Util.getStringById(R.string.image_folder_name) + "/";
        String fileName = getFileName();
        String filePath = directory + fileName;

        File folder = new File(directory);

        if(!folder.exists()) folder.mkdirs();

        File file = new File(folder, getFileName());
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Canvas canvas = new Canvas(bitmap.copy(Bitmap.Config.ARGB_8888, true));

        FileOutputStream output;
        try {
            output = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        canvas.drawBitmap(bitmap, 0, 0, null);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

        try {
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return "file://" + filePath;
    }

    /**
     * Generates a simple name for the image to be saved.
     *
     * @return Name based on prefix + date + random number.
     */
    private static String getFileName() {
        int randomNumber = new Random().nextInt(999);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
        String date = dateFormat.format(new Date());

        return Util.getStringById(R.string.image_prefix) + date + "_" + randomNumber + ".jpg";
    }

}
