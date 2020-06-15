package com.example.lab1;

import com.example.lab1.Module.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private UI_LoginReg ui;
    private MainActivity A_THIS;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        A_THIS = MainActivity.this;

        //  初始化数据库
        try {
            DBAccess.deployDB(this);
        } catch (IOException e) {
            Log.e("MainActivity/DBInit", Log.getStackTraceString(e));
        }

        ui = new UI_LoginReg();
        ui.btn_lf.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    if (ui.verifyLogin(ui.et_user.getText().toString(), ui.et_pwd.getText().toString())) {
                        // 记住密码
                        if(ui.cb_rmblg.isChecked()) SPLoginMgr.SaveLogin(A_THIS, ui.et_user.getText().toString(), ui.et_pwd.getText().toString());
                        else SPLoginMgr.SaveLogin(A_THIS, null, null);
                        //Toast 成功 跳转
                        Toast.makeText(A_THIS, "登陆成功", Toast.LENGTH_SHORT).show();
                        Intent itLevel = new Intent(MainActivity.this, LevelActivity.class);
                        Bundle bLevel = new Bundle();
                        bLevel.putString("usr", ui.et_user.getText().toString());
                        itLevel.putExtras(bLevel);
                        startActivity(itLevel);
                    } else
                        Toast.makeText(A_THIS, "用户名或密码不正确！", Toast.LENGTH_LONG).show();//Toast 失败 用户名或密码不正确
                } catch(SQLException e) {
                    Toast.makeText(A_THIS, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        ui.btn_rt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(A_THIS, RegsiterActivity.class));
            }
        });
        // 获取记住的密码
        Map<String, String> rmblogin = SPLoginMgr.LoadLogin(A_THIS);
        String usr = rmblogin.get("usr"), pwd = rmblogin.get("pwd");
        if(usr!=null && pwd!=null) {
            ui.et_user.setText(usr);
            ui.et_pwd.setText(pwd);
            ui.cb_rmblg.setChecked(true);
        }
    }
    class UI_LoginReg extends Fn_Login{
        public EditText et_user, et_pwd, et_pwd2;
        public Button btn_lf, btn_rt;
        public CheckBox cb_rmblg;
        public UI_LoginReg(){
            et_user = (EditText)findViewById(R.id.editText);
            et_pwd = (EditText)findViewById(R.id.editText2);
            // et_pwd2 = (EditText)findViewById(R.id.editText3);
            btn_lf = (Button)findViewById(R.id.btnLevel0);
            btn_rt = (Button)findViewById(R.id.button2);
            cb_rmblg = (CheckBox)findViewById(R.id.checkBox);
        }
    }
    class Fn_Login{
        DBAccess dbx;
        SQLiteDatabase db;
        public Fn_Login(){
            dbx = new DBAccess();
            db = dbx.getInstance();
            loadData_Inside();
        }
        public boolean verifyLogin(String usr, String pwd){
            // 登录验证
            boolean rt = false;
            Cursor cur = db.rawQuery("select * from LoginInfo where usr=? and pwd=?", new String[]{usr, pwd});
            if(cur.getCount()>0) rt = true;
            cur.close();
            return rt;
        }

        //public void loadData_SQLite(){}
        private void loadData_Inside(){}
    }
}
class SPLoginMgr {
    public static void SaveLogin(Context context, String usr, String pwd) {
        SharedPreferences.Editor edit = context.getSharedPreferences("ldata", Context.MODE_PRIVATE).edit();
        edit.putString("usr", usr);
        edit.putString("pwd", pwd);
        edit.commit();
    }
    public static Map<String, String> LoadLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences("ldata", Context.MODE_PRIVATE);
        String usr = sp.getString("usr",null), pwd = sp.getString("pwd", null);
        Map<String, String> ret = new HashMap<String, String>();
        ret.put("usr", usr);
        ret.put("pwd", pwd);
        return ret;
    }
}