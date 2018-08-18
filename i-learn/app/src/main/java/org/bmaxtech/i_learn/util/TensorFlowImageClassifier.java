package org.bmaxtech.i_learn.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TensorFlowImageClassifier {
    private static final String TAG = "TensorFlowClassifier";
    private static final int MAX_RESULTS = 3;
    private static final int BATCH_SIZE = 1;
    private static final int PIXEL_SIZE = 3;
    private static final float THRESHOLD = 0.1f;
    private static final String LABELS_FILE = "labels.txt";
    private static final String MODEL_FILE = "optimized_graph.tflite";
    private Interpreter interpreter;
    private int inputSize = 224;
    private boolean active = false;

    private List<String> labelList;

    TensorFlowImageClassifier(Context context, int x, int y) {
        this.initClassifier(context);
    }

    private void initClassifier(Context context) {
        try {
            interpreter = new Interpreter(loadModelFile(context.getAssets(), MODEL_FILE));
            active = true;
            labelList = loadLabelList(context.getAssets(), LABELS_FILE);
        } catch (Exception ex) {
            Log.w(TAG, "Unable to initialize TensorFlow Lite.", ex);
        }
    }

    /**
     * Recognize Image
     *
     * @param bitmap
     * @return
     */
    public List<Recognition> recognizeImage(Bitmap bitmap) {
        List<Recognition> recognitionList = new ArrayList<>();
        try {
            if (active) {
                ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);
                byte[][] result = new byte[1][labelList.size()];
                interpreter.run(byteBuffer, result);
                recognitionList = getSortedResult(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return recognitionList;
    }

    /**
     * Start tensorflow Classification
     */
    public void startClassifier() {
        active = true;
    }

    /**
     * Destroy tensorflow interpreter
     */
    public void destroyClassifier() {
        // interpreter.close();
        active = false;
    }

    /**
     * Load Prediction Model
     *
     * @param assetManager
     * @param modelPath
     * @return
     * @throws IOException
     */
    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * Load Label File
     *
     * @param assetManager
     * @param labelPath
     * @return
     * @throws IOException
     */
    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    /**
     * Convert Bitmap To Byte Buffer
     *
     * @param bitmap
     * @return
     */
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false);
        ByteBuffer byteBuffer;
        byteBuffer = ByteBuffer.allocateDirect(BATCH_SIZE * inputSize * inputSize * PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.put((byte) ((val >> 16) & 0xFF));
                byteBuffer.put((byte) ((val >> 8) & 0xFF));
                byteBuffer.put((byte) (val & 0xFF));
            }
        }
        return byteBuffer;
    }

    /**
     * Get Sorted Results
     *
     * @param labelProbArray
     * @return
     */
    @SuppressLint("DefaultLocale")
    private List<Recognition> getSortedResult(byte[][] labelProbArray) {
        PriorityQueue<Recognition> priorityQueue = new PriorityQueue<>(MAX_RESULTS, new Comparator<Recognition>() {
            @Override
            public int compare(Recognition lhs, Recognition rhs) {
                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
            }
        });

        for (int i = 0; i < labelList.size(); ++i) {
            float confidence = (labelProbArray[0][i] & 0xff) / 255.0f;
            if (confidence > THRESHOLD) {
                priorityQueue.add(new Recognition("" + i,
                        labelList.size() > i ? labelList.get(i) : "unknown",
                        confidence));
            }
        }

        final ArrayList<Recognition> recognitions = new ArrayList<>();
        int recognitionsSize = Math.min(priorityQueue.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; ++i) {
            recognitions.add(priorityQueue.poll());
        }

        return recognitions;
    }
}
