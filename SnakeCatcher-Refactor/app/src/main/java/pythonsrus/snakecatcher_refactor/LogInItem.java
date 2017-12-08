package pythonsrus.snakecatcher_refactor;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rishitaroy on 12/1/17.
 */

@IgnoreExtraProperties
public class LogInItem {

    public String uid;
    public String uri;
    public Long datetime_epoch;
    public String datetime_human;

    public LogInItem(){
        //default constructor
    }

    public LogInItem(String uid, String uri){
        this.uid = uid;
        this.uri = uri;
        Date date = new Date();
        this.datetime_epoch = (Long) date.getTime();
        this.datetime_human = passHumanTime(this.datetime_epoch);

    }

    private String passHumanTime(Long t){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(t);
    }

    public Long getDatetime(){
        return datetime_epoch;
    }

    public String passHumanTime(){
        return datetime_human;
    }

    public String getUid(){
        return uid;
    }

    public String getUri(){
        return uri;
    }
}
