package xinrong.git.myapplication.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nike on 16/1/9.
 */
public class ProgramBean implements Serializable {


    private String msgCodo;
    private String message;
    private ArrayList<VideoBean> progrem_list;


    public String getMsgCodo() {
        return msgCodo;
    }

    public void setMsgCodo(String msgCodo) {
        this.msgCodo = msgCodo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<VideoBean> getProgrem_list() {
        return progrem_list;
    }

    public void setProgrem_list(ArrayList<VideoBean> progrem_list) {
        this.progrem_list = progrem_list;
    }
}
