package thirdparty.sscf.com.thirdtest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sscf.askstock.calendarview.Calendar;
import com.sscf.askstock.calendarview.CalendarView;

import java.util.HashMap;

/**
 * (Hangzhou)
 *
 * @author: wzm
 * @date :  2019/2/28 11:26
 * Summary:
 */
public class TestCalendarViewActivity extends AppCompatActivity implements CalendarView.OnCalendarInterceptListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_layout);
        initView();
    }

    private void initView() {


        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnCalendarInterceptListener(this);
        HashMap<String, Calendar> map = new HashMap<>();
        Calendar calendar = new Calendar();
        calendar.setYear(calendarView.getCurYear());
        calendar.setMonth(calendarView.getCurMonth());
        calendar.setDay(3);
        calendar.setSchemeColor(Color.RED);
        calendar.setScheme("看");
        calendar.addScheme(Color.YELLOW,"牛");
        Calendar calendar2 = new Calendar();
        calendar2.setYear(calendarView.getCurYear());
        calendar2.setMonth(calendarView.getCurMonth());
        calendar2.setDay(5);
        calendar2.setSchemeColor(Color.CYAN);
        calendar2.setScheme("小");
        calendar2.addScheme(Color.DKGRAY,"牛1");
        map.put(calendar.toString(),calendar);
        map.put(calendar2.toString(),calendar2);
        calendarView.setSchemeDate(map);
    }

    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        int day = calendar.getDay();
        return day==1||day==23;
    }

    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
        Toast.makeText(this,
                calendar.toString() + (isClick ? "拦截不可点击" : "拦截设定为无效日期"),
                Toast.LENGTH_SHORT).show();
    }
}
