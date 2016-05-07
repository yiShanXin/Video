package xinrong.git.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by nike on 16/2/28.
 */
public class FlytvSettings extends Activity {

    @ViewInject(R.id.layout_radiogroup)
    private RadioGroup radioGroup;


    @ViewInject(R.id.radio_default)
    private RadioButton radio_default;




    @ViewInject(R.id.radio_full)
    private RadioButton radio_full;



    @ViewInject(R.id.btn_config)
    private Button btn_config;


    @ViewInject(R.id.btn_cancel)
    private Button btn_cancel;


    SharedPreferences video_xml;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_tips_settings_loading);
        x.view().inject(this);
        video_xml = getSharedPreferences("http_mode", Activity.MODE_PRIVATE);

        if(video_xml.getInt("playType", 1)==0){
            radio_default.setChecked(true);
        }else{
            radio_full.setChecked(true);
        }
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                int selectId = Integer.parseInt(radioButton.getTag().toString());
                if (video_xml.getInt("playType", 0)== selectId){

                }
                LogUtil.e("--------------set playType-----------------------------------"+selectId);
//
                SharedPreferences.Editor editor = video_xml.edit();
                editor.putInt("playType",selectId);
                editor.commit();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
