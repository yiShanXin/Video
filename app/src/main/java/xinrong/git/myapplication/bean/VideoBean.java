package xinrong.git.myapplication.bean;

import java.io.Serializable;

/**
 * Created by nike on 16/1/9.
 */
public class VideoBean implements Serializable {


    private String id;
    private String program_name;
    private String program;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}
