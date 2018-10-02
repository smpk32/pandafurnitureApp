package com.panda.user.pandafurnitureapp.lib;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 *
 * */
public class FileLib {
    public static final String TAG = FileLib.class.getSimpleName();
    private volatile static FileLib instance;

    public static FileLib getInstance() {
        if (instance == null) {
            synchronized (FileLib.class) {
                if (instance == null) {
                    instance = new FileLib();
                }
            }
        }
        return instance;
    }

    /**
     *
     * @param context
     * @return
     */
    private File getFileDir(Context context) {
        String state = Environment.getExternalStorageState();
        File filesDir;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            filesDir = context.getExternalFilesDir(null);
        } else {
            filesDir = context.getFilesDir();
        }

        return filesDir;
    }

    /**
     *
     * @param context
     * @param name
     * @return
     */
    public File getProfileIconFile(Context context, String name) {
        return new File(FileLib.getInstance().getFileDir(context), name + ".png");
    }

    /**
     *
     * @param context
     * @param name
     * @return
     */
    public File getImageFile(Context context, String name) {
        return new File(FileLib.getInstance().getFileDir(context), name + ".png");
    }
}
