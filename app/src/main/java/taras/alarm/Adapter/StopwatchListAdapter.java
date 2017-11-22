package taras.alarm.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import taras.alarm.R;

public class StopwatchListAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    ArrayList<Long> timeList;

    public StopwatchListAdapter(Context context, ArrayList<Long> timeList) {
        this.context = context;
        this.timeList = timeList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return timeList.size();
    }

    @Override
    public Object getItem(int i) {
        return timeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.stopwatch_list_item, viewGroup, false);
        }

        if (timeList.size() > 1 && i > 0){
            long currentTime = timeList.get(i);
            long previousTime = timeList.get(i - 1);
            long difTime = currentTime - previousTime;
            ((TextView) view.findViewById(R.id.stopwatch_listview_item_dif_time)).setText(convertTimeToString(difTime));
        } else {
            ((TextView) view.findViewById(R.id.stopwatch_listview_item_dif_time)).setText("");
        }

        String num = String.valueOf(i + 1);
        ((TextView) view.findViewById(R.id.stopwatch_listview_item_num)).setText(num);
        ((TextView) view.findViewById(R.id.stopwatch_listview_item_time)).setText(convertTimeToString(timeList.get(i)));

        /*
        if (timeList.size() < 2 && i == 0){
            ((TextView) view.findViewById(R.id.stopwatch_listview_item_dif_time)).setText("");
        } else {
            if (i > 0){
                long currentTime = timeList.get(i - 1);
                long previousTime = timeList.get(i);
                long difTime = currentTime - previousTime;
                ((TextView) view.findViewById(R.id.stopwatch_listview_item_dif_time)).setText(convertTimeToString(difTime));
            }
        }*/


        return view;
    }

    private String convertTimeToString(long time){
        long updateTime = time;
        int seconds = (int) (updateTime / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        int milliSeconds = (int) (updateTime % 1000);

        String timeString = String.format("%02d:%02d:%02d", minutes, seconds, milliSeconds);
        return  timeString;
    }


}
