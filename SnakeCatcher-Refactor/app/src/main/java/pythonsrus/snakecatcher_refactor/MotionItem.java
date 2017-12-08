package pythonsrus.snakecatcher_refactor;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rishitaroy on 12/1/17.
 */

@IgnoreExtraProperties
public class MotionItem {

    public Long start_time;
    public Long end_time;
    public Long duration;
    public boolean motion_detected;
    public Long start_datetime_epoch;
    public String start_datetime_human;


    public MotionItem(){
        start_time = System.currentTimeMillis();
        Date date = new Date();
        this.start_datetime_epoch = (Long) date.getTime();
        this.start_datetime_human = passHumanTime(this.start_datetime_epoch);
        motion_detected = false;

    }

    public void endMotion(boolean didItMove){
        end_time = System.currentTimeMillis();
        motion_detected = didItMove;
        duration = (end_time - start_time)/1000;
    }

    private String passHumanTime(Long t){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(t);
    }

    public Long getDatetime(){
        return start_datetime_epoch;
    }

    public String getHumanTime(){
        return start_datetime_human;
    }

    public boolean getMotionDetected(){
        return motion_detected;
    }

    public Long getDuration(){
        return duration;
    }


}
