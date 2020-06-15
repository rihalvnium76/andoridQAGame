package com.example.lab1;

import com.example.lab1.Module.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    private UICtrl.GameLoader loader;
    private UICtrl.GameChoiceRecorder choiceRecorder;

    // 载入下一题
    private void loadNextQuestion() {
        UICtrl.GameLoader.DBItem d = loader.nextQuestion();
        if(d!=null) {
            tvQuestion.setText(d.question);
            tvChoice.setText(null);
            choiceRecorder.setMultiChoice(d.solution.length() > 1);
            tvCorrectNum.setText(loader.getQuestionIndex() + "/" + loader.getMaxQuestionCount() + "题");
            int choiceCount = d.choiceCount;
            for(int i=0; i<btnChoiceList.length; ++i)
                btnChoiceList[i].setVisibility(i<d.choiceCount? View.VISIBLE: View.INVISIBLE); // 显示隐藏按钮
        } else
            quitButton();
    }
    // 游戏结束
    private void gameOver() {
        AlertDialog.Builder box = new AlertDialog.Builder(GameActivity.this);
        box.setTitle("游戏结束").setMessage("本次答题答对题数为 " + loader.getQuestionIndex() + " 题！")
            .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 退出
                    GameActivity.this.finish();
                }
            })
            .create().show();
    }
    // 退出
    private void quitButton() {
        // 保存记录 退出
        loader.recordHiScore(UICtrl.LOGIN_USER, UICtrl.currentLevelID, loader.getQuestionIndex());
        gameOver();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 初始化题库
        loader = new UICtrl.GameLoader();
        loader.initQuestions(UICtrl.currentLevelID);
        // 初始化选择记录器
        choiceRecorder = new UICtrl.GameChoiceRecorder();
        // 初始化界面组件
        initComponents();
        // 弹出说明
        loader.showGameRuleMsgbox(GameActivity.this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // 开始游戏，载入题目
        loadNextQuestion();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // 退出
            AlertDialog.Builder box = new AlertDialog.Builder(GameActivity.this);
            box.setMessage("是否结束游戏返回主界面？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quitButton();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 取消
                        }
                    })
                    .create().show();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initComponents() {
        tvChoice = findViewById(R.id.tvChoice);
        tvCorrectNum = findViewById(R.id.tvCorrectNum);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnChA = findViewById(R.id.btnChoiceA);
        btnChB = findViewById(R.id.btnChoiceB);
        btnChC = findViewById(R.id.btnChoiceC);
        btnChD = findViewById(R.id.btnChoiceD);
        btnQuit = findViewById(R.id.btnQuit);
        btnNext = findViewById(R.id.btnNext);

        btnChoiceList = new Button[] {
            btnChA, btnChB, btnChC, btnChD
        };

        btnChA.setOnClickListener(new ChoiceButtonListener(UICtrl.GameChoiceRecorder.CHOICE_A));
        btnChB.setOnClickListener(new ChoiceButtonListener(UICtrl.GameChoiceRecorder.CHOICE_B));
        btnChC.setOnClickListener(new ChoiceButtonListener(UICtrl.GameChoiceRecorder.CHOICE_C));
        btnChD.setOnClickListener(new ChoiceButtonListener(UICtrl.GameChoiceRecorder.CHOICE_D));
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 校验答案是否正确
                if(loader.verifyAnswer(choiceRecorder.getAnswer()))
                    loadNextQuestion();
                else {
                    AlertDialog.Builder box = new AlertDialog.Builder(GameActivity.this);
                    box.setTitle("Oops").setMessage("很遗憾，回答错误！\n本题正解为 " + loader.getQuestion().solution)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            quitButton();
                            }
                        })
                        .create().show();
                }
            }
        });
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出
                AlertDialog.Builder box = new AlertDialog.Builder(GameActivity.this);
                box.setMessage("是否结束游戏返回主界面？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quitButton();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 取消
                        }
                    })
                    .create().show();
            }
        });

        // 添加滚动条
        tvQuestion.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
    class ChoiceButtonListener implements View.OnClickListener {
        private int choiceIndex;
        public ChoiceButtonListener(int choiceIndex) {
            this.choiceIndex = choiceIndex;
        }
        @Override
        public void onClick(View v) {
            choiceRecorder.setAnswer(choiceIndex);
            tvChoice.setText(choiceRecorder.getAnswer());
        }
    }

    private TextView tvQuestion, tvCorrectNum, tvChoice;
    private Button btnChA, btnChB, btnChC, btnChD, btnQuit, btnNext;
    private Button[] btnChoiceList;
}
