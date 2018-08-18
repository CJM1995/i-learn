package org.bmaxtech.i_learn.activity;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.bmaxtech.i_learn.R;

public class SplashActivity extends AppCompatActivity {
    // add launch delay
    private static final int APP_LAUNCH_DELAY = 5000;
    private static final int PERMISSION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // add animation to splash screen
        YoYo.with(Techniques.BounceInUp).duration(3000).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                YoYo.with(Techniques.Tada).duration(1000).playOn(findViewById(R.id.icon_1));
            }
        }).playOn(findViewById(R.id.icon_1));
        YoYo.with(Techniques.ZoomIn).duration(1000).playOn(findViewById(R.id.text_1));
        YoYo.with(Techniques.ZoomIn).duration(2000).playOn(findViewById(R.id.text_2));
        YoYo.with(Techniques.ZoomIn).duration(3000).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                YoYo.with(Techniques.RubberBand).duration(1000).playOn(findViewById(R.id.text_3));
            }
        }).playOn(findViewById(R.id.text_3));
        final Handler handler = new Handler();
        Activity activity = this;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                } else {
                    // move view to main activity
                    moveToMainActivity();
                }
            }
        }, APP_LAUNCH_DELAY);
    }

    /**
     * On Request Permission Response
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.moveToMainActivity();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * Move to main activity
     */
    private void moveToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.enter_from_left, R.anim.exit_to_right);
        startActivity(intent, options.toBundle());
        finish();
    }
}
