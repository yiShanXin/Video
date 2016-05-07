package xinrong.git.myapplication.tools;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by nike on 16/2/27.
 */
public class AudioUtils {

    private AudioUtils(){};

    private static  AudioUtils audioUtils;
    private static  AudioManager mAudioManager;
    public static AudioUtils getInster(Context context){
        if(audioUtils==null){
            audioUtils = new AudioUtils();
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        return audioUtils;
    }
    public  void setVolume(boolean isLeft){
        if (isLeft) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
//            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
        else{
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
//            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,
//                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        }
    }


}
