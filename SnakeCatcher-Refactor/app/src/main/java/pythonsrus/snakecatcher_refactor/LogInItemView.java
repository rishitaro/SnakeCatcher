package pythonsrus.snakecatcher_refactor;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class LogInItemView extends RecyclerView.ViewHolder {
    public TextView date;
    public TextView url;
    private String TAG = "LogInItemView";

    public LogInItemView(View itemView){
        super(itemView);

        date = (TextView) itemView.findViewById(R.id.date);
        url = (TextView) itemView.findViewById(R.id.url);
    }

    public void bind(LogInItem model) {
        Log.v("HistoryView", "in bind:" + model.datetime_human);
        date.setText(model.datetime_human);
        url.setText(model.uri);
    }
}
