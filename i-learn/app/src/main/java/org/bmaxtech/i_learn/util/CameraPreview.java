package org.bmaxtech.i_learn.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.bmaxtech.i_learn.callback.PredictionCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private TensorFlowImageClassifier tensorFlowImageClassifier;
    private int cameraPreviewWidth = 1280;
    private int cameraPreviewHeight = 720;
    private PredictionCallback predictionCallback;

    public CameraPreview(Context context, PredictionCallback callback, Camera camera) {
        super(context);
        predictionCallback = callback;
        try {
//            tensorFlowImageClassifier = new TensorFlowImageClassifier(context, cameraPreviewWidth, cameraPreviewHeight);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mCamera = camera;
        mHolder = getHolder();
        mCamera.setPreviewCallback(this);
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(cameraPreviewWidth, cameraPreviewHeight);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
//            tensorFlowImageClassifier.startClassifier();
        } catch (IOException e) {
            Log.d("CameraPreview : ", "surfaceCreated -> " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.d("stopPreview : ", e.getMessage());
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d("surfaceChanged : ", "surfaceChanged -> " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("surfaceDestroyed : ", "done");
//        tensorFlowImageClassifier.destroyClassifier();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//        Camera.Parameters parameters = camera.getParameters();
//        int width = parameters.getPreviewSize().width;
//        int height = parameters.getPreviewSize().height;
//        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);
//        byte[] bytes = out.toByteArray();
//        List<Recognition> prediction = tensorFlowImageClassifier.recognizeImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
//        Log.d("onPreviewFrame", "**************");
//        for (int i = 0; i < prediction.size(); i++) {
//            Log.d("", prediction.get(i).toString());
//        }
//        Log.d("onPreviewFrame", "**************");
//        if (prediction.size() > 0) {
//            // notify subscriber
//            predictionCallback.notifyPrediction(prediction.get(0).getTitle());
//        }
    }
}
