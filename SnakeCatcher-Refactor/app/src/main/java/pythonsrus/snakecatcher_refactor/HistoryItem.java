package pythonsrus.snakecatcher_refactor;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rishitaroy on 12/1/17.
 */

@IgnoreExtraProperties
public class HistoryItem {

    public String uid;
    public String uri;
    public Long datetime_epoch;
    public String datetime_human;

    public HistoryItem(){
        //default constructor
    }

    public HistoryItem(String uid, String uri){
        this.uid = uid;
        this.uri = uri;
        Date date = new Date();
        this.datetime_epoch = (Long) date.getTime();
        this.datetime_human = getHumanTime(this.datetime_epoch);

    }

    public Long getDatetime(){
        return datetime_epoch;
    }

    private String getHumanTime(Long t){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(t);
    }

    public String passHumanTime(){
        return datetime_human;
    }
}