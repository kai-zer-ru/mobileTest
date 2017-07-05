package pro.myburse.android.myburse.Utils;

/**
 * Created by alexey on 05.07.17.
 */

public class OttoMessage {
    private String action;
    private Object data;

    public OttoMessage(){

    }

    public OttoMessage(String action, Object data){
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
