package pythonsrus.snakecatcher_refactor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MotionItemView extends RecyclerView.ViewHolder {
    public TextView date;
    public TextView duration;
    public TextView motion_detected;

    public MotionItemView(View itemView) {
        super(itemView);

        date = (TextView) itemView.findViewById(R.id.date);
        duration = (TextView) itemView.findViewById(R.id.duration);
        motion_detected = (TextView) itemView.findViewById(R.id.motion_detected);
    }

    public void bind(MotionItem model) {
        date.setText(model.getHumanTime());
        duration.setText("Duration of session: " + model.duration + " seconds");
        if (model.getMotionDetected()){
            motion_detected.setText("There was motion detected during this session");
        } else {
            motion_detected.setText("There was no motion detected during this session");
        }

    }
}
