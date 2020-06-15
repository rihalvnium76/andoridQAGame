package com.example.lab1.Module;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class UICtrl {
    public static String LOGIN_USER;
    public static int currentLevelID; // 当前关卡iD

    // 选择合成器
    public static class GameChoiceRecorder {
        private boolean[] selectedAnswer; // 多选选择
        private int currentAnswerIndex; // -1 多选 0无选择 1A 2B ...
        public static final int
            MAX_CHOICE_NUMBER = 4, // 最大选项数
            CHOICE_A = 0, CHOICE_B = 1,
            CHOICE_C = 2, CHOICE_D = 3;
        public static final String CHOICE_LIST = "ABCD"; // 选项列表

        public GameChoiceRecorder() {
            selectedAnswer = new boolean[MAX_CHOICE_NUMBER];
        }
        // 设置该题是否为多选
        public void setMultiChoice(boolean isMultiChoice) {
            currentAnswerIndex = isMultiChoice? -1: 0;
            for(int i=0; i<MAX_CHOICE_NUMBER; ++i) selectedAnswer[i] = false;
        }
        // 记录作答选择
        public void setAnswer(int index) {
            if(currentAnswerIndex==-1)
                selectedAnswer[index] = !selectedAnswer[index];
            else
                currentAnswerIndex = index + 1;
        }
        // 获取作答
        public String getAnswer() {
            StringBuilder rt = new StringBuilder();
            if(currentAnswerIndex==-1)
                for(int i=0; i<MAX_CHOICE_NUMBER; ++i)
                    rt.append(selectedAnswer[i]? CHOICE_LIST.charAt(i): "");
            else
                if(currentAnswerIndex!=0)
                    rt.append(CHOICE_LIST.charAt(currentAnswerIndex - 1));
            return rt.toString();
        }
    }

    // 题库加载器
    public static class GameLoader {
        public static int MAX_QUESTION_NUMBER = 15; // 单次最大提问次数
        private DBAccess dbx;
        private SQLiteDatabase db;
        private int currentQuestionIndex, maxQuestionIndex; // 当前题号 题库最大题号 [0,maxQuestionIndex]
        public static class DBItem {
            public String question, solution;
            public int choiceCount;

            public DBItem(String question, String solution, int choiceCount) {
                this.question = question;
                this.solution = solution;
                this.choiceCount = choiceCount;
            }
        }
        private ArrayList<DBItem> dataList;

        public GameLoader() {
            dbx = new DBAccess();
            db = dbx.getInstance();
            dataList = new ArrayList<DBItem>(15);
        }

        // 显示游戏帮助弹窗
        public void showGameRuleMsgbox(Context context, DialogInterface.OnClickListener PlayButtonClickListener) {
            AlertDialog.Builder box = new AlertDialog.Builder(context);
            box.setTitle("游戏规则")
                .setMessage("游戏会从题库中随机抽取至多" + getMaxQuestionCount() + "题提问\n中途答错会立即结束游戏\n结束游戏时会记录最高答对题数")
                .setPositiveButton("了解，开始", PlayButtonClickListener)
                .create().show();
        }

        // 载入题库
        public void initQuestions(int levelID) {
            Cursor csr = db.rawQuery("select q_ch,ch_count,solution from LevelContent where levelid=" + levelID, null);
            int rc = csr.getCount();
            if(rc>0) {
                dataList.clear(); // 清空表
                while(csr.moveToNext())
                    dataList.add(new DBItem(
                        csr.getString(0),
                        csr.getString(2),
                        csr.getInt(1)
                    ));
            }
            csr.close();
            randomizeQuestions(rc);
            currentQuestionIndex = -1; // 初始化当前题号
            maxQuestionIndex = rc - 1;
        }
        // 打乱题库
        private void randomizeQuestions(int rc) {
            Random rnd = new Random();
            for(int i=0; i<rc; ++i) {
                DBItem t;
                int j = rnd.nextInt(rc);
                // random swap
                t = dataList.get(i);
                dataList.set(i, dataList.get(j));
                dataList.set(j, t);
            }
        }
        // 载入下一题
        public DBItem nextQuestion() {
            ++currentQuestionIndex;
            return getQuestion();
        }
        // 获取当前题数据
        public DBItem getQuestion() {
            DBItem rt = null;
            if(currentQuestionIndex<MAX_QUESTION_NUMBER && currentQuestionIndex<=maxQuestionIndex)
                rt = dataList.get(currentQuestionIndex);
            return rt;
        }
        // 获取当前题索引
        // 返回值：无题时-1 有题目时从0起
        public int getQuestionIndex() {
            return currentQuestionIndex;
        }
        // 校验答案
        // 参数：choice 1 A 2 B 3 C 4 D
        // 返回值：true正确，false错误
        public boolean verifyAnswer(String answer) {
            answer = answer!=null? answer.toUpperCase(): "";
            DBItem d = dataList.get(currentQuestionIndex);
            char[] a = answer.toCharArray(), b = d.solution.toCharArray();
            Arrays.sort(a);
            Arrays.sort(b);
            return new String(a).equals(new String(b));
        }
        // 记录最高纪录
        public void recordHiScore(String usr, int lvID, int hiScore) {
            Cursor cur = db.rawQuery("select hiscore from userinfo where usr=\'" + usr + "\' and levelid=" + lvID, null);
            if(cur.moveToNext())
                if(cur.getInt(0)>hiScore)
                    return;
                else
                    db.execSQL("update UserInfo set hiScore=" + hiScore + " where usr=\'" + usr + "\' and levelID=" + lvID);
            else
                db.execSQL("insert into UserInfo values(\'" + UICtrl.LOGIN_USER + "\'," + lvID + "," + hiScore + ")");
        }
        // 获取最大问题数
        public int getMaxQuestionCount() {
            int b = maxQuestionIndex+1;
            return MAX_QUESTION_NUMBER>b? b: MAX_QUESTION_NUMBER;
        }
    }
}
