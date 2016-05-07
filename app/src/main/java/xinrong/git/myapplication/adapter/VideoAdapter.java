package xinrong.git.myapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;

import java.util.ArrayList;
import java.util.logging.Logger;

import xinrong.git.myapplication.R;
import xinrong.git.myapplication.bean.ProgramBean;
import xinrong.git.myapplication.bean.VideoBean;

/**
 * Created by nike on 16/1/9.
 */
public class VideoAdapter extends BaseAdapter {

    private Context context ;
    private ArrayList<VideoBean>  items;
    private ProgramHolderView programHolderView;

    public VideoAdapter(Context context,ArrayList<VideoBean> items){
        this.context = context;
        this.items = items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            programHolderView = new ProgramHolderView();
            convertView = View.inflate(context, R.layout.video_item,null);
            programHolderView.tx_name =(TextView)convertView.findViewById(R.id.tx_name);
            convertView.setTag(programHolderView);
        }else{
            programHolderView =(ProgramHolderView) convertView.getTag();
        }
        final VideoBean entity = items.get(position);
        programHolderView.tx_name.setText((position+1)+"\t-\t"+entity.getProgram_name());
        return convertView;
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
//        return items.size();
        return items.size();
    }
    class ProgramHolderView{

        private TextView tx_name;
    }
}
