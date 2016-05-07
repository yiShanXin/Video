package xinrong.git.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xinrong.git.myapplication.bean.ProgramBean;
import xinrong.git.myapplication.bean.VideoBean;
import xinrong.git.myapplication.tools.AlertTools;
import xinrong.git.myapplication.tools.LoadingDataDialog;

/**
 * Created by nike on 16/1/9.
 */

@ContentView(R.layout.settings)
public class FlytvVideoSettings extends Activity {


    @ViewInject(R.id.btn_update)
    private  Button btn_update;

    @ViewInject(R.id.edit_http)
    private EditText edit_http;

    //  192.168.1.105

    SharedPreferences  httpList ;
    SharedPreferences  video_xml ;

    private LoadingDataDialog loadingDataDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);


        loadingDataDialog = new LoadingDataDialog(FlytvVideoSettings.this,R.style.MyDialogStyle);


        httpList = getSharedPreferences("http_json",Activity.MODE_PRIVATE);

        video_xml = getSharedPreferences("http_mode",Activity.MODE_PRIVATE);

        String httpJson = video_xml.getString("httpVideoJSON", "http://192.168.1.5/1.json");
        edit_http.setText(httpJson);
        edit_http.setSelection(edit_http.getText().length());
        // httpList
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("Linux new ()");

                String name = edit_http.getText().toString().trim();
                if(name.contains("http://")&& name.contains(".json") ){
                   String httpUrl =  name.substring(0,name.lastIndexOf("/"));
                    SharedPreferences.Editor editor_show = video_xml.edit();
                    editor_show.putString("httpUrl",httpUrl);
                    editor_show.putString("httpVideoJSON",name);
                    editor_show.commit();

                    loadingDataDialog.show();
                    RequestParams params = new RequestParams(name);
//                    params.setSslSocketFactory(...); // 设置ssl
//                    params.addQueryStringParameter("wd", "xUtils");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            loadingDataDialog.dismiss();
                            LogUtil.d("返回 result [" + result + "]");
                            if(result!=null || result.equals("")){
                                SharedPreferences.Editor editor = httpList.edit();
                                SharedPreferences.Editor editor_show = video_xml.edit();
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    Gson gson = new Gson();
                                    List<VideoBean> videoBean = gson.fromJson(jsonObject.getString("program_list"), new TypeToken<List<VideoBean>>(){}.getType());
//                                    Collections.sort(videoBean);

                                    if(videoBean!=null){
                                        boolean isNull = false;
                                        for (int i = 0 ; i< videoBean.size();i++){
                                            VideoBean items  =  videoBean.get(i);
                                            if(items==null){
                                                isNull = true;
                                                break;
                                            }
                                        }
                                        LogUtils.e("videoBean" + videoBean.size());
                                        if(!isNull){
                                            editor.putString("httpList", jsonObject.getString("program_list"));
                                            editor_show.putInt("playIndex", 0);
                                            editor.commit();
                                            editor_show.commit();
                                            Intent intent= new Intent();
                                            setResult(RESULT_OK,intent);
                                            finish();
                                        }else{
//                                            Toast.makeText(x.app(), "", Toast.LENGTH_LONG).show();
                                            AlertTools.showTips(FlytvVideoSettings.this, R.mipmap.tips_wait, "节目单列表格式不正确，请查证 ！");
                                        }

                                    }else{
                                        LogUtils.e("videoBean" +(videoBean==null));
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    LogUtil.d("返回 result [" + e.getMessage()  + "]");
                                }

                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
//                            LogUtil.d("返回 result [" + cex.getMessage() + "]");
//                            Toast.makeText(x.app(), "网络异常 ！", Toast.LENGTH_LONG).show();
                            AlertTools.showTips(FlytvVideoSettings.this, R.mipmap.tips_wait, "网络异常 或者服务器异常 ！");

                            loadingDataDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(Callback.CancelledException cex) {
                            LogUtil.d("返回 result [" + cex.getMessage() + "]");
//                            Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                            loadingDataDialog.dismiss();
                        }

                        @Override
                        public void onFinished() {
                            loadingDataDialog.dismiss();
                        }
                    });

                }
            }
        });

    }
}
