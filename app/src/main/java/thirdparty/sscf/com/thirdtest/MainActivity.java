package thirdparty.sscf.com.thirdtest;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.sscf.investment.messagebox.MessageBox;
import com.sscf.investment.messagebox.Reference;
import com.sscf.investment.messagebox.bean.MessageButton;
import com.sscf.investment.messagebox.holder.ContentCreater;
import com.sscf.investment.messagebox.holder.IContentHolder;
import com.sscf.investment.messagebox.listener.OnDismissDialogListener;
import com.sscf.third.taskpoll.execute.TaskBuilder;
import com.sscf.third.taskpoll.execute.process.ProcessParam;
import com.sscf.third.taskpoll.execute.process.Processable;
import com.sscf.third.taskpoll.execute.process.ProcessableGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(v -> {
            MessageBox.builder()
                    .contentHolder(ContentCreater.createTips(this, "您的材料已经提交", "请耐心等待审核", R.drawable.messagebox_icon))
                    .bgColor(Color.TRANSPARENT)
                    .showBottomDivider(false)
                    .build("wait")
                    .show(getSupportFragmentManager());
        });

        findViewById(R.id.button7).setOnClickListener(v -> {
            MessageBox.builder()
                    .contentMessage("圆角，无标题，无按钮情况！\n我是第二行的数据", 10f, 1.5f)
                    .radius(20, 20, 20, 20)
                    .showBottomDivider(false)
                    .build("wait")
                    .show(getSupportFragmentManager());
        });

        findViewById(R.id.button8).setOnClickListener(v -> {
            MessageBox.builder()
                    .title("圆角对话框")
                    .contentMessage("圆角，有标题，无按钮情况！")
                    .radius(20, 20, 20, 20)
                    .showBottomDivider(false)
                    .build("wait")
                    .show(getSupportFragmentManager());
        });

        findViewById(R.id.button9).setOnClickListener(v -> {
            MessageBox.builder()
                    .title("圆角对话框")
                    .contentHolder(ContentCreater.createTips(MainActivity.this, "提示内容", "提示副内容"))
                    .radius(20, 20, 20, 20)
                    .showBottomDivider(false)
                    .build("wait")
                    .show(getSupportFragmentManager());
        });

        findViewById(R.id.button2).setOnClickListener(v -> {
            MessageBox.builder()
                    .title("绑定提示")
                    .contentMessage("检测到您输入的手机号已注册，您是否需要将本账号与该手机号合并？之后您可以使用本账号快速登录。")
                    .contentGravity(Gravity.LEFT)
                    .bgColor(Color.WHITE)
                    .titleHeight(200)
                    .radius(20, 20, 20, 20)
                    .Ok("确定")
                    .Cancel("取消")
                    .addButton(new MessageButton("测试", Color.YELLOW))
                    .dimAmount(0f)
                    .onDismissDialogListener((messagebox, bundle, holder, initiativeDiasmiss) -> Log.d("MainActivity", "====================>>>>>>> initiativeDiasmiss " + initiativeDiasmiss))
                    .build("dialog")
                    .show(getSupportFragmentManager());
        });

        findViewById(R.id.button3).setOnClickListener(v -> {
            Reference.FULL_SCREEN_DIALOG
                    .contentHolder(ContentCreater.createTips(this, "您的材料已经提交", "请耐心等待审核", -1))
                    .bgColor(Color.TRANSPARENT)
                    .bgContentColor(Color.TRANSPARENT)
                    .animationStyleRes(Reference.Animation.Animation_Dialog_Fade)
                    .showBottomDivider(false)
                    .build("wait")
                    .show(getSupportFragmentManager());
        });

        findViewById(R.id.button6).setOnClickListener(v -> {
            MessageBox.builder()
                    .contentMessage("您的材料已经提交")
                    .bgColor(Color.TRANSPARENT)
                    .showBottomDivider(false)
                    .animationStyleRes(R.style.MessageBox_Animations_Dialog_Bottom)
                    .styleRes(R.style.dialog_share_theme)
                    .fullScreen(true)
                    .build("wait")
                    .show(getSupportFragmentManager());
        });

        findViewById(R.id.button5).setOnClickListener(v -> {
            // 多线程，同时执行，一起返回
            ProcessableGroup group = new ProcessableGroup() {
                @Override
                public void postResult(Object[] results) {
                    StringBuffer builder = new StringBuffer();
                    for (int i=0; i<results.length; i++) {
                        builder.append(results[i]).append(", ");
                    }
                    Toast.makeText(getApplicationContext(), "执行一组任务 threadId " + builder.toString(), Toast.LENGTH_SHORT).show();
                }
            };

            group.postSequentially(new Processable() {
                @Override
                protected Object doInBackground(ProcessParam params) throws Exception {
                    Thread.sleep(2000);
                    return getThreadID();
                }

                @Override
                protected void onPostExecute(ProcessParam params, @NonNull Object result) {
                    Toast.makeText(getApplicationContext(), "执行一个任务 threadId " + getThreadID(), Toast.LENGTH_SHORT).show();
                }
            }, new Processable() {
                @Override
                protected Object doInBackground(ProcessParam params) throws Exception {
                    Thread.sleep(2000);
                    return getThreadID();
                }

                @Override
                protected void onPostExecute(ProcessParam params, @NonNull Object result) {
                    Toast.makeText(getApplicationContext(), "执行一个任务 threadId " + getThreadID(), Toast.LENGTH_SHORT).show();
                }
            }, new Processable() {
                @Override
                protected Object doInBackground(ProcessParam params) throws Exception {
                    Thread.sleep(2000);
                    return getThreadID();
                }

                @Override
                protected void onPostExecute(ProcessParam params, @NonNull Object result) {
                    Toast.makeText(getApplicationContext(), "执行一个任务 threadId " + getThreadID(), Toast.LENGTH_SHORT).show();
                }
            });
            TaskBuilder.build().execute(group);
        });

        findViewById(R.id.button4).setOnClickListener(v -> {
            TaskBuilder.build().execute(new Processable() {
                @Override
                protected Object doInBackground(ProcessParam params) throws Exception {
                    Thread.sleep(2000);
                    return true;
                }

                @Override
                protected void onPostExecute(ProcessParam params, @NonNull Object result) {
                    Toast.makeText(getApplicationContext(), "执行一个任务 threadId " + getThreadID(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestScanActivity.class));
            }
        });

        findViewById(R.id.calendar_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestCalendarViewActivity.class));
            }
        });


    }
}
