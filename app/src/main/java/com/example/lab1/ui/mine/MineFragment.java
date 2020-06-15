package com.example.lab1.ui.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.lab1.Module.DBAccess;
import com.example.lab1.Module.UICtrl;
import com.example.lab1.R;

public class MineFragment extends Fragment {

    private MineViewModel dashboardViewModel;
    private TextView tv_username, tvInfo;
    private Button btnDelete;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(MineViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mine, container, false);
        /*final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        tv_username = (TextView)(root.getRootView().findViewById(R.id.textView));
        tv_username.setText(UICtrl.LOGIN_USER);
        btnDelete = (Button)(root.getRootView().findViewById(R.id.button));
        Log.d("MyLEVEL", "onCreate: "+tv_username.getText().toString());

        // 设置滚动条
        tvInfo = (TextView)(root.getRootView().findViewById(R.id.textView2));
        tvInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

        getHiScore();

        // 删除按钮添加事件
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder box = new AlertDialog.Builder(getContext());
                box.setTitle("警告")
                   .setMessage("是否清空当前账号所有最高纪录数据？")
                   .setPositiveButton("是", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           SQLiteDatabase db = new DBAccess().getInstance();
                           db.execSQL("delete from UserInfo where usr=\'" + UICtrl.LOGIN_USER + "\'");
                           getHiScore();
                       }
                   })
                   .setNegativeButton("否", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                       }
                   })
                   .create().show();
            }
        });
        return root;
    }

    private void getHiScore() {
        // 获取最高级记录信息
        SQLiteDatabase db = new DBAccess().getInstance();
        Cursor cur = db.rawQuery("select levelinfo.levelid,levelinfo.levelname,userinfo.hiscore from userinfo,levelinfo where userinfo.usr=\'" + UICtrl.LOGIN_USER + "\' and userinfo.levelid=levelinfo.levelid order by levelinfo.levelid asc", null);
        StringBuilder str = new StringBuilder("");
        while(cur.moveToNext())
            str.append("关卡" + cur.getInt(0) + "-" + cur.getString(1) + " : " + cur.getInt(2) + "\n");
        tvInfo.setText(str.toString());
        Log.d("MyLEVEL", "str.hiScoreInfo: "+str.toString());
        Log.d("MyLEVEL", "tv.hiScoreInfo: "+tvInfo.getText().toString());
    }
}
