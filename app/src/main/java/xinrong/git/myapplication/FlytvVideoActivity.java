package xinrong.git.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.ViewUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.xutils.x;

import xinrong.git.myapplication.adapter.VideoAdapter;
import xinrong.git.myapplication.bean.ProgramBean;
import xinrong.git.myapplication.bean.VideoBean;
import xinrong.git.myapplication.tools.AlertTools;
import xinrong.git.myapplication.tools.AudioUtils;
import xinrong.git.myapplication.tools.LoadingDataDialog;
import xinrong.git.myapplication.tools.LoadingTvDialog;
import xinrong.git.myapplication.view.LinearTransparentPanel;
import xinrong.git.myapplication.view.VideoView;

/**
 * Created by nike on 16/1/9.
 */

//@ContentView(R.layout.ad_video_custome)
public class FlytvVideoActivity extends Activity {

    private final String TAG = "AD";
    @ViewInject(R.id.listView)
    private ListView listView;

    @ViewInject(R.id.videoView)
    private android.widget.VideoView videoView;



    @ViewInject(R.id.videoView_suffer)
    private VideoView videoView_suffer;

    @ViewInject(R.id.txt_msg)
    private TextView txt_msg;

    @ViewInject(R.id.tips_loading_msg)
    private TextView tips_loading_msg;

    @ViewInject(R.id.layout_tips)
    private RelativeLayout layout_tips;






    @ViewInject(R.id.layout_list)
    LinearTransparentPanel layout_list;

    SharedPreferences httpList;
    SharedPreferences video_xml;
    private VideoAdapter videoAdapter;
    private ArrayList<VideoBean> items = new ArrayList<VideoBean>();
    private int index = 0;
    private int index_left = 0;
    boolean isShow = true;
    private int numIndex = 0;
    private LoadingTvDialog loadingDataDialog;
    // 直播电视
    // flytv
    android.os.Handler handler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    index_left++;
                    if (index_left >= 9) {
                        layout_list.setVisibility(View.INVISIBLE);
                        isShow = false;
                        txt_msg.requestFocus();

                    }
                    handler.sendEmptyMessageDelayed(10, 1000);
                    break;
                case 100:
                    handler.sendEmptyMessageDelayed(100, 10000);
                    initMsg();
                    break;
                case 101:
                    numIndex++;
                    if(numIndex>2) {
                        playNum();
                    }
                    handler.sendEmptyMessageDelayed(101, 700);
                    break;
                case 102:
                    // 重新播放
                    play();
                    break;
            }

        }
    };
    private int posi;
    private AudioUtils audioUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ad_video_custome);
        x.view().inject(this);
        LogUtils.d("init()");
        Gson gson = new Gson();
        httpList = getSharedPreferences("http_json", Activity.MODE_PRIVATE);
        audioUtils = AudioUtils.getInster(this);
        video_xml = getSharedPreferences("http_mode", Activity.MODE_PRIVATE);
        String name = httpList.getString("httpList", "");
        if (name.equals("")) {
            String jsonStr = readFile();
            try {
                //  test
                JSONObject jsonObject = new JSONObject(jsonStr);
                name = jsonObject.getString("program_list");
                LogUtils.d("读取 本地文件" + name);
            } catch (Exception e) {

            }
        } else {
            //

        }

        // 菜单 82
        // 单选 视频播放显示  1. 默认  2. 拉伸(全屏)

        loadingDataDialog = new LoadingTvDialog(this,R.style.MyDialogStyle);
        List<VideoBean> videoBean = gson.fromJson(name, new TypeToken<List<VideoBean>>() {
        }.getType());
        initIrMap();
        Display display =  this.getWindowManager().getDefaultDisplay();
        View view =  findViewById(R.id.layout_root);
        LogUtil.e("---------------------------------------------------heigth "+display.getWidth()+"| "+display.getHeight()+"|"+view.getHeight()+"|"+view.getWidth());
//        videoView.setVideoScale(display.getWidth(),display.getHeight());
        items.addAll(videoBean);
        videoAdapter = new VideoAdapter(this, items);
        listView.setAdapter(videoAdapter);
        listView.requestFocus();
        listView.setSelection(0);
        handler.sendEmptyMessageDelayed(10, 1000);
        handler.sendEmptyMessageDelayed(100, 10000);
        handler.sendEmptyMessageDelayed(101, 700);
        if (items.size() > 0) {
            int playIndex = video_xml.getInt("playIndex", 0);
            listView.setSelection(playIndex);
            VideoBean entity = items.get(playIndex);
            videoView.setVideoURI(Uri.parse(entity.getProgram()));
//                  videoView.setMediaController(mediaco);
//                  mediaco.setMediaPlayer(videoView);
            //让VideiView获取焦点
            videoView.start();


        }
//        String json = rawJson();
        String msg = video_xml.getString("message", "").toString().trim();

        if (!msg.equals("") || msg != null) {
            txt_msg.setText(msg);
        } else {

        }
        initMsg();
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                index_left = 0;
                listView.requestFocus();
                LogUtil.e("listView onItemSelected 开始播放 "+position);
                index = position;
//                if(!isLeftControll){
                    play();
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // ok键
        // 侧栏 翻页
        // 无侧栏 声音
        // 数字键 匹配时间 无响应时间 ！

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(FlytvVideoActivity.this,"播放 第"+(position+1)+"资源",Toast.LENGTH_SHORT).show();
                LogUtil.e("videoView setOnItemClickListener() 播放"+items.get(position).getProgram_name()+" index ="+position );
                if(isLeftNum){
                    playNum();
                    return;
                }
                if (index == position) {
                    //
                    if (layout_list.getVisibility() == View.VISIBLE) {
                        layout_list.setVisibility(View.INVISIBLE);
                    } else {
                        layout_list.setVisibility(View.VISIBLE);
                    }
                } else {
                    posi = position;
                    index = position;
//                    if (videoView.isPlaying()) {
//                        videoView.stopPlayback();
//                    }
//                    videoView.start();
                    play();
                    LogUtil.e("videoView setOnItemClickListener() 播放 start()");
//                    VideoBean entity = items.get(position);
//                    videoView.setVideoURI(Uri.parse(entity.getProgram()));
//                  videoView.setMediaController(mediaco);
//                  mediaco.setMediaPlayer(videoView);
                    SharedPreferences.Editor editor = video_xml.edit();
                    editor.putInt("playIndex", position);
                    editor.commit();
                    //让VideiView获取焦点

                }


            }
        });
        // 视频播放问题
        //
        // 默认都去本地文件
        videoView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                LogUtil.e("videoView onKeyDown()" + keyCode);
//                Toast.makeText(FlytvVideoActivity.this, "videoView onKeyDown " + keyCode, Toast.LENGTH_SHORT).show();
                listView.requestFocus();
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    return true;
                }
                return false;
            }
        });
        videoView_suffer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                LogUtil.e("videoView onKeyDown()" + keyCode);
//                Toast.makeText(FlytvVideoActivity.this, "videoView onKeyDown " + keyCode, Toast.LENGTH_SHORT).show();
                listView.requestFocus();
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    return true;
                }
                return false;
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e("videoView  播放失败!");
                handler.sendEmptyMessageDelayed(102, 15000);
                return false;
            }
        });

        videoView_suffer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e("videoView  播放失败!");
                handler.sendEmptyMessageDelayed(102, 15000);
                return false;
            }
        });


    }


    void initIrMap(){
        irCode.clear();
        for (int i = 0;i<items.size();i++){
            VideoBean entity  =  items.get(i);
            irCode.put(entity.getId(),i+"");
        }

    }

    private int settings_index = 0;
    private long mExitTime;
    private long exitTime = 0;
    public static final int MIN_CLICK_DELAY_TIME = 800;
    private long lastClickTime = 0;

    private long nextuplastClickTime = 0;
    // 如果在设置界面 让侧边长显示 不消失


    private String tvIrCode;
    HashMap<String,String> irCode = new HashMap<String,String>();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.e("onKeyDown" + keyCode);
        isLeftControll = false;
        numIndex = 0;
        switch (keyCode){
             case   KeyEvent.KEYCODE_ENTER :
             case      KeyEvent.KEYCODE_DPAD_CENTER:
                 settings_index = 0;
                 listView.requestFocus();
                 listView.setSelection(index);
                 index_left = 0;
                 if(layout_list.getVisibility()==View.VISIBLE){
                     layout_list.setVisibility(View.INVISIBLE);
                     isShow = false;
                 }else{
                     layout_list.setVisibility(View.VISIBLE);
                     isShow = true;
                 }
                 playNum();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                isLeftNum = false;
                LogUtil.e("当前播放的  节目 "+index+"|是否可以上一首视频"+(index + 1 <= items.size()));
                if (index == 0) {
                    if (settings_index == 0) {

                        AlertTools.showTips(FlytvVideoActivity.this, R.mipmap.tips_wait, "已经是第一个视频");
                    }
                    if (settings_index == 9) {
                        Intent intent = new Intent(FlytvVideoActivity.this, FlytvVideoSettings.class);
                        startActivityForResult(intent, 100);
                        index = 0;
                        settings_index = 0;
                    } else {
                        settings_index++;
                    }
                } else if (index - 1 >= 0) {
                    index--;
                    listView.setSelection(index);
//                        play();
                }
                break;
            case  KeyEvent.KEYCODE_DPAD_DOWN:
                isLeftNum = false;
                LogUtil.e("当前播放的  节目"+index+"|是否可以下一首视频"+(index + 1 <= items.size()));
                settings_index = 0;
                if (items.size() == 0 || index == items.size() - 1) {
//                        Toast.makeText(this, "已经是最后一个视频 ", Toast.LENGTH_SHORT).show();
                    AlertTools.showTips(FlytvVideoActivity.this,R.mipmap.tips_wait,"已经是最后一个视频 ");
                } else if (index + 1 <= items.size()) {
                    index++;
//                              LogUtil.e("onKeyDown 开始下一首   is_next_down play()");
                    listView.setSelection(index);
//                             LogUtil.e("onKeyDown 开始下一首   setSelection "+index);
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                Intent intent = new Intent(FlytvVideoActivity.this,FlytvSettings.class);
                startActivityForResult(intent,200);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                isLeftNum = false;
                leftOrRight(false, true);
                  break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                isLeftNum = false;
                leftOrRight(false, false);
                break;
            case KeyEvent.KEYCODE_0:
                numEnter(0);
                break;
            case KeyEvent.KEYCODE_1:
                numEnter(1);
                break;
            case KeyEvent.KEYCODE_2:
                numEnter(2);
                break;
            case KeyEvent.KEYCODE_3:
                numEnter(3);
                break;
            case KeyEvent.KEYCODE_4:
                numEnter(4);
                break;
            case KeyEvent.KEYCODE_5:
                numEnter(5);
                break;
            case KeyEvent.KEYCODE_6:
                numEnter(6);
                break;
            case KeyEvent.KEYCODE_7:
                numEnter(7);
                break;
            case KeyEvent.KEYCODE_8:
                numEnter(8);
                break;
            case KeyEvent.KEYCODE_9:
                numEnter(9);
                break;
            default:
                tvIrCode = "";
                isLeftNum = false;
                break;

        }

        return super.onKeyDown(keyCode, event);
    }

    void numEnter  (int code){
        AlertTools.colseTips();
        tvIrCode =tvIrCode + code;
        isLeftNum = true;
        tvIrCode =   tvIrCode.replace("null","");
        if(layout_tips.getVisibility()==View.INVISIBLE){
            layout_tips.setVisibility(View.VISIBLE);
        }

        tips_loading_msg.setText("播放的节目 ID :  "+tvIrCode);
    }
    void playNum(){
        if(isLeftNum){
            String position  = tvIrCode;
            if(position==null||position.equals("")){
                //AlertTools.showTips(FlytvVideoActivity.this,R.mipmap.tips_error,"无效的节目ID");
                numIndex = 0;
                tvIrCode = "";
                isLeftNum = false;
                tips_loading_msg.setText("无效的节目 ID");
                layout_tips.setVisibility(View.INVISIBLE);
            }else{
                position = position.replace("null","");
                if(Integer.parseInt(position)<=items.size()){
                    //
                    AlertTools.showTips(FlytvVideoActivity.this,R.mipmap.tips_success,"播放的节目 ID = "+position);
                    // 选中并且播放
                    numIndex = 0;
                    isLeftNum = false;
                    tvIrCode = "";
                    listView.setSelection(Integer.parseInt(position) - 1);
                    layout_tips.setVisibility(View.INVISIBLE);


                }else{
                    numIndex = 0;
                    tvIrCode = "";
                    tips_loading_msg.setText("播放的节目 ID  "+position+" 无效");


                }

            }
        }
    }

    void play() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
//        if (currentTime - nextuplastClickTime > MIN_CLICK_DELAY_TIME) {
            nextuplastClickTime = currentTime;
            LogUtils.e("--------------------------------------------------------------------");
            LogUtil.e("up up  is_next_down play()" + videoView.isPlaying());
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
            if (videoView_suffer.isPlaying()) {
                videoView_suffer.stopPlayback();
            }
            Display display =  this.getWindowManager().getDefaultDisplay();


            LogUtil.e("---------------------------------------------------heigth "+display.getWidth()+"| "+display.getHeight()+"|||");
            LogUtil.e("up up  is_next_down stop()" + videoView.isPlaying());
            VideoBean entity = items.get(index);

            int playType =  video_xml.getInt("playType", 1);


//                  videoView.setMediaController(mediaco);
//                  mediaco.setMediaPlayer(videoView);
            SharedPreferences.Editor editor = video_xml.edit();
            editor.putInt("playIndex", index);
            editor.commit();
            //让VideiView获取焦点

            if(playType==0){
                videoView.setVisibility(View.VISIBLE);
                videoView_suffer.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(Uri.parse(entity.getProgram()));
                videoView.start();
            }else{
                videoView.setVisibility(View.INVISIBLE);
                videoView_suffer.setVisibility(View.VISIBLE);
                videoView_suffer.setVideoURI(Uri.parse(entity.getProgram()));
                videoView_suffer.start();
            }
            LogUtil.e("播放视频");
            LogUtils.e("--------------------------------------------------------------------playType="+playType);
//        }
    }
    private boolean isLeftControll = false;
    private boolean isLeftNum = false;

    void leftOrRight(boolean isView,boolean isLeft) {
        if(isView){
            // 控制翻页
//            isLeftControll = true;
//            int position = listView.getSelectedItemPosition();
//            if(isLeft){
//                if(position -10>0){
//                    listView.setSelection(position -10);
//                }else{
//                    listView.setSelection(0);
//                }
//            }else{
//                if(position +10<items.size()-1){
//                    listView.setSelection(position +10);
//                }else{
//                    listView.setSelection(items.size()-1);
//                }
//            }
        }else{
            // 控制音量
            isLeftControll = false;
            audioUtils.setVolume(isLeft);
//            listView.forceLayout();
            listView.requestFocus();
        }


    }
    // 音量控制 换一种方式   不然会导致焦点失去
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    LogUtils.e("Result()");
                    String name = httpList.getString("httpList", "");
                    Gson gson = new Gson();
                    List<VideoBean> videoBean = gson.fromJson(name, new TypeToken<List<VideoBean>>() {
                    }.getType());
                    items.clear();
                    initIrMap();
                    items.addAll(videoBean);
                    videoAdapter.notifyDataSetChanged();
                    if (items.size() > 0) {
                        posi = video_xml.getInt("playIndex", 0);
                        VideoBean entity = items.get(posi);
                        listView.setSelection(posi);
//                        videoView.setVideoURI(Uri.parse(entity.getProgram()));
//                        videoView.start();
                        layout_list.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case 200:
                if (resultCode == RESULT_OK) {
                    play();
                }
                break;

        }
    }

    private String readFile() {
        Resources res = this.getResources();
        InputStream in = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        sb.append("");
        try {
            in = res.openRawResource(R.raw.demo);
            String str;
            br = new BufferedReader(new InputStreamReader(in, "GBK"));
            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
        } catch (Resources.NotFoundException e) {

            LogUtils.d("file not exis");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            LogUtils.d("file not  exception");
            e.printStackTrace();
        } catch (IOException e) {
            LogUtils.d("file not  error");
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    void initMsg() {

        String httpUrl = video_xml.getString("httpUrl", "").toString().trim();
        if (httpUrl.equals("") || httpUrl == null) {
            return;
        }
        String name = httpUrl + "/message.txt";
        RequestParams params = new RequestParams(name);
//                    params.setSslSocketFactory(...); // 设置ssl
//                    params.addQueryStringParameter("wd", "xUtils");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                if (result != null || result.equals("")) {

                    SharedPreferences.Editor editor_show = video_xml.edit();
                    try {
                        if (!result.trim().equals("") || result.trim() != null) {
//                          editor_show.putInt("playIndex", 0);
                            if (!result.equals(video_xml.getString("message", ""))) {
                                editor_show.putString("message", result);
                                txt_msg.setText(result);
                                editor_show.commit();
                            }
                        } else {

                        }
                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.pause();
        videoView.stopPlayback();


        videoView_suffer.pause();
        videoView_suffer.stopPlayback();



        layout_list.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        play();
        layout_list.setVisibility(View.VISIBLE);
        LogUtil.e("onRestart start Video()" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlertTools.colseTips();
        videoView.stopPlayback();
        videoView_suffer.stopPlayback();
        handler.removeMessages(10);
        handler.removeMessages(100);
        handler.removeMessages(101);
        handler.removeMessages(102);

    }
}
