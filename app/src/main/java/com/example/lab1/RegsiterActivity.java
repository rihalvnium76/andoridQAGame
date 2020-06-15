package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lab1.Module.*;

public class RegsiterActivity extends AppCompatActivity {
    private RegsiterActivity A_THIS;
    private  UI_LoginReg ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter);
        A_THIS = RegsiterActivity.this;
        ui = new UI_LoginReg();
        ui.btn_lf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册
                String a = ui.et_user.getText().toString(), b = ui.et_pwd.getText().toString(), c = ui.et_pwd2.getText().toString();
                if (b.compareTo(c)!=0)
                    Toast.makeText(A_THIS, "密码与确认密码不一致！", Toast.LENGTH_LONG).show();
                else {
                    if (ui.registerLogin(a, b))
                        Toast.makeText(A_THIS, "注册成功", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(A_THIS, "用户名或密码应大于3位而小于20位！", Toast.LENGTH_LONG).show();
                }
            }
        });
        ui.btn_rt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 返回MainActivity
                AlertDialog.Builder builder = new AlertDialog.Builder(A_THIS);
                //builder.setTitle("普通对话框");// 设置标题
                builder.setMessage("是否要退出注册？");// 为对话框设置内容
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // 确定按钮
                        A_THIS.finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // 取消按钮
                    }
                });
                builder.create().show();// 使用show()方法显示对话框
            }
        });
    }
    class UI_LoginReg extends Fn_Login {
        public EditText et_user, et_pwd, et_pwd2;
        public Button btn_lf, btn_rt;
        public UI_LoginReg(){
            et_user = (EditText)findViewById(R.id.editText);
            et_pwd = (EditText)findViewById(R.id.editText2);
            et_pwd2 = (EditText)findViewById(R.id.editText3);
            btn_lf = (Button)findViewById(R.id.btnLevel0);
            btn_rt = (Button)findViewById(R.id.button2);
        }
    }
    class Fn_Login {
        DBAccess dbx;
        SQLiteDatabase db;
        public Fn_Login() {
            dbx = new DBAccess();
            db = dbx.getInstance();
        }
        public boolean registerLogin(String usr, String pwd){
            // 用户注册
            // 用户名,密码>=3 <=20位
            if(pwd.length()<3 || pwd.length()>20) return false;
            db.execSQL("delete from LoginInfo where usr=?",new String[]{usr}); // 删除原有账号
            db.execSQL("insert into LoginInfo values(?,?)", new String[]{usr, pwd});
            return true;
        }
    }
}
