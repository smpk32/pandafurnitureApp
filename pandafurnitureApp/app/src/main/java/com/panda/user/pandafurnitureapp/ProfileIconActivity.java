package com.panda.user.pandafurnitureapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.panda.user.pandafurnitureapp.item.MemberInfoItem;
import com.panda.user.pandafurnitureapp.lib.FileLib;
import com.panda.user.pandafurnitureapp.lib.MyLog;
import com.panda.user.pandafurnitureapp.lib.RemoteLib;
import com.panda.user.pandafurnitureapp.lib.StringLib;
import com.panda.user.pandafurnitureapp.remote.RemoteService;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 *
 */
public class ProfileIconActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int CROP_FROM_ALBUM = 3;

    Context context;

    ImageView profileIconImage;

    MemberInfoItem memberInfoItem;

    File profileIconFile;
    String profileIconFilename;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_icon);

        context = this;

        memberInfoItem = ((MyApp) getApplication()).getMemberInfoItem();

        setToolbar();
        setView();
        setProfileIcon();
    }

    /**
     *
     */
    private void setToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.profile_setting);
        }
    }

    /**
     *
     */
    public void setView() {
        profileIconImage = (ImageView) findViewById(R.id.profile_icon);

        Button albumButton = (Button) findViewById(R.id.album);
        albumButton.setOnClickListener(this);

        Button cameraButton = (Button) findViewById(R.id.camera);
        cameraButton.setOnClickListener(this);
    }

    /**
     *
     */
    private void setProfileIcon() {
        MyLog.d(TAG, "onResume " +
                RemoteService.MEMBER_ICON_URL + memberInfoItem.memberIconFilename);

        if (StringLib.getInstance().isBlank(memberInfoItem.memberIconFilename)) {
            Picasso.with(this).load(R.drawable.ic_person).into(profileIconImage);
        } else {
            Picasso.with(this)
                    .load(RemoteService.MEMBER_ICON_URL + memberInfoItem.memberIconFilename)
                    .into(profileIconImage);
        }
    }

    /**
     * ?
     */
    private void setProfileIconFile() {
        profileIconFilename = memberInfoItem.seq + "_" + String.valueOf(System.currentTimeMillis());

        profileIconFile = FileLib.getInstance().getProfileIconFile(context, profileIconFilename);
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        setProfileIconFile();

        if (v.getId() == R.id.album) {
            getImageFromAlbum();

        } else if (v.getId() == R.id.camera) {
            getImageFromCamera();
        }
    }

    /**
     *
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_close, menu);
        return true;
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_close:
                finish();
                break;
        }

        return true;
    }

    /**
     *
     */
    private void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(profileIconFile));
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     *
     */
    private void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    /**
     *
     * @param inputUri
     * @param outputUri
     * @return
     * */
    private Intent getCropIntent(Uri inputUri, Uri outputUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

        return intent;
    }

    /**
     *
     */
    private void cropImageFromCamera() {
        Uri uri = Uri.fromFile(profileIconFile);
        Intent intent = getCropIntent(uri, uri);
        startActivityForResult(intent, CROP_FROM_CAMERA);
    }

    /**
     *
     */
    private void cropImageFromAlbum(Uri inputUri) {
        Uri outputUri = Uri.fromFile(profileIconFile);

        MyLog.d(TAG, "startPickFromAlbum uri " + inputUri.toString());
        Intent intent = getCropIntent(inputUri, outputUri);
        startActivityForResult(intent, CROP_FROM_ALBUM);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param intent
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        MyLog.d(TAG, "onActivityResult " + intent);

        if (resultCode != RESULT_OK) return;

        if (requestCode == PICK_FROM_CAMERA) {
            cropImageFromCamera();

        } else if (requestCode == CROP_FROM_CAMERA) {
            Picasso.with(this).load(profileIconFile).into(profileIconImage);
            uploadProfileIcon();

        } else if (requestCode == PICK_FROM_ALBUM && intent != null) {
            Uri dataUri = intent.getData();
            if (dataUri != null) {
                cropImageFromAlbum(dataUri);
            }
        } else if (requestCode == CROP_FROM_ALBUM && intent != null) {
            Picasso.with(this).load(profileIconFile).into(profileIconImage);
            uploadProfileIcon();
        }
    }

    /**
     *
     */
    private void uploadProfileIcon() {
        RemoteLib.getInstance().uploadMemberIcon(memberInfoItem.seq, profileIconFile);

        memberInfoItem.memberIconFilename = profileIconFilename + ".png";
    }
}