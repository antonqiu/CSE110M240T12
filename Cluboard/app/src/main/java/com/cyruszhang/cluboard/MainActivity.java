package com.cyruszhang.cluboard;

/**
 * Created by zhangxinyuan on 2/29/16.
 */
import com.cyruszhang.cluboard.activity.Home;
import com.parse.ui.ParseLoginBuilder;
import com.parse.ui.ParseLoginDispatchActivity;

public class MainActivity extends ParseLoginDispatchActivity {
    private static final int LOGIN_REQUEST = 0;

    @Override
    protected Class<?> getTargetClass() {
       /* ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                MainActivity.this);
        startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
        */
        return Home.class;

    }
}