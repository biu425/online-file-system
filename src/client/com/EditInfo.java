package client.com;

import java.io.Serializable;

public class EditInfo implements Serializable{
    public int sequence; // used to synchronize
    public int mode; // mode == 0, delete; mode == 1, insert.
    public int startOffset; // valid when mode == 0 & 1
    public int endOffset; // valid when mode == 0
    public String text; // valid when mode == 1

    public EditInfo() {}

    public EditInfo(int mode, int startOffset, int endOffset, String text){
        this.mode = mode;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.text = text;
    }

    public EditInfo(EditInfo e){
        this.mode = e.mode;
        this.startOffset = e.startOffset;
        this.endOffset = e.endOffset;
        this.text = e.text;
    }

}
