package org.bmaxtech.i_learn.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.bmaxtech.i_learn.R;
import org.bmaxtech.i_learn.callback.PredictionCallback;
import org.bmaxtech.i_learn.util.CameraPreview;
import org.bmaxtech.i_learn.util.VoiceService;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer, PredictionCallback {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private Camera mCamera;
    private CameraPreview mPreview;
    private VoiceService voiceService;
    private TextView predictionText;
    private ImageView voiceOutputBtn;
    private LinearLayout infoPanel;
    // Example : Apple, Ball, Cat, Dog, Egg, Flower, Goldfish, Hat, Ice-Cream, Jam
    private String prediction = "Apple";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YoYo.with(Techniques.BounceIn).duration(2000).playOn(findViewById(R.id.bottom_panel));
        YoYo.with(Techniques.BounceInRight).duration(2000).playOn(findViewById(R.id.info_panel));

        // bind with observer (get instance from existing configs)
        voiceService = new VoiceService(getApplicationContext(), this);
        voiceService.addObserver(this);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        predictionText = findViewById(R.id.predictionText);
        infoPanel = findViewById(R.id.info_panel);

        // register button actions
        voiceOutputBtn = findViewById(R.id.voiceOutputBtn);
        voiceOutputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!predictionText.getText().toString().isEmpty()) {
                    voiceService.playVoiceCommand(predictionText.getText().toString());
                }
            }
        });
        infoPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!predictionText.getText().toString().isEmpty()) {
                    // put prediction result as intent extras
                    Bundle bundle = new Bundle();
                    bundle.putString("title", predictionText.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                    intent.putExtras(bundle);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.enter_from_left, R.anim.exit_to_right);
                    startActivity(intent, options.toBundle());
                } else {
                    Toast.makeText(getApplicationContext(), "No Item Detected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // REMOVE THIS (due to prediction model issue, prediction is turned off)
        predictionText.setText(prediction);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    /**
     * Get Camera Instance
     *
     * @return the Camera
     */
    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            Log.d("getCameraInstance : ", e.getMessage());
        }
        return camera;
    }

    @Override
    public void update(Observable observable, Object o) {
        // voice assistant updates
    }

    /**
     * Prediction Change Subscription
     *
     * @param prediction
     */
    @Override
    public void notifyPrediction(String prediction) {
        predictionText.setText(prediction);
    }
}
