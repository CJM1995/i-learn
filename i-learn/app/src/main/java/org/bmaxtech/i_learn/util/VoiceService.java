package org.bmaxtech.i_learn.util;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.Locale;
import java.util.Observable;

public class VoiceService extends Observable implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    // unique id for TTS
    private static final String TTS_ID = "1234567890";
    // voice rate
    private static final float VOICE_RATE = 0.8f;
    // for API level above 15, check and set this voice
    private static final String VOICE_TYPE = "en-us-x-sfg#male_1-local";

    private TextToSpeech textToSpeech;
    private Activity activity;
    private boolean running = false;

    /**
     * Make Voice Assistance Instance
     *
     * @param context
     * @param mActivity
     */
    public VoiceService(Context context, Activity mActivity) {
        this.activity = mActivity;
        this.textToSpeech = new TextToSpeech(context, this);
    }

    /**
     * Play Voice Command String
     *
     * @param voiceCommand
     */
    public void playVoiceCommand(String voiceCommand) {
        if (this.textToSpeech != null) {
            // play voice commands associated with MainActivity View
            this.running = true;
            Bundle bundle = new Bundle();
            bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
            // text to speech
            // check API level
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.textToSpeech.speak(voiceCommand, TextToSpeech.QUEUE_FLUSH, bundle, TTS_ID);
            } else {
                this.textToSpeech.speak(voiceCommand, TextToSpeech.QUEUE_FLUSH, null);
            }

        } else {
            Log.d("VoiceService", "init textToSpeech");
        }
    }

    /**
     * Stop Voice Commands
     */
    public void stopVoiceCommand() {
        this.running = false;
        this.textToSpeech.shutdown();
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            this.textToSpeech.setLanguage(Locale.UK);
            this.textToSpeech.setSpeechRate(VOICE_RATE);
            // set voice tone given in Constants
            // check API level
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (Voice voice : this.textToSpeech.getVoices()) {
                    if (voice.getName().equals(VOICE_TYPE)) {
                        this.textToSpeech.setVoice(voice);
                        break;
                    }
                }
            }
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    running = false;
                    Log.d("VoiceService", "TTS finished");
                    notifyChanges();
                }

                @Override
                public void onError(String utteranceId) {
                    running = false;
                    Log.e("VoiceService", "TTS Speak Failed!");
                }

                @Override
                public void onStart(String utteranceId) {
                }
            });
        } else {
            this.running = false;
            Log.e("VoiceService", "Initialization Failed!");
        }
        this.activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onUtteranceCompleted(String s) {

    }

    /**
     * Notify Voice Commands Execution
     */
    private void notifyChanges() {
        setChanged();
        notifyObservers();
    }
}
