package com.example.stas.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String MY_TAG = "TestingAnswer";
    private static final String KEY_INDEX = "index";
    private static final String KEY_INDEX_ANSWER = "indexAnswer";
    private static final String KEY_INDEX_CHEATED = "indexCheated";
    private static final String KEY_INDEX_ANSWERED = "indexAnswered";
    private static final String KEY_INDEX_SCORE = "indexScore";
    private static final String KEY_INDEX_TOKENS = "indexTokens";
   // private static final String KEY_INDEX_CHEATED = "indexCheated";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton, mFalseButton, mCheatButton;
    private ImageButton mNextButton, mPrevButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };
    private int mCurrentIndex = 0;
    private int mTokensAmount = 3;
    private boolean mIsCheater;
    private boolean[] mQuestionsCheated;
    private boolean[] mQuestionsAnswered;
    private int mScore = 0;
    private int mIndexAnswered = 0;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_INDEX, mCurrentIndex);
        Log.d(TAG, "OnSaveInstanceState " + outState.getInt(KEY_INDEX));
        for (int i = 0; i < mQuestionBank.length; i++) {
            mQuestionsAnswered[i] = mQuestionBank[i].isAlreadyAnswer();
            mQuestionsCheated[i] = mQuestionBank[i].isCheatTrue();
        }
        outState.putBooleanArray(KEY_INDEX_ANSWER, mQuestionsAnswered);
        outState.putBooleanArray(KEY_INDEX_CHEATED, mQuestionsCheated);
        outState.putInt(KEY_INDEX_SCORE, mScore);
        outState.putInt(KEY_INDEX_ANSWERED, mIndexAnswered);
        outState.putInt(KEY_INDEX_TOKENS, mTokensAmount);
       // outState.putBoolean(KEY_INDEX_CHEATED, mIsCheater);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null){
                return;
            }
            mTokensAmount = CheatActivity.getTokensAmount(data);
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mQuestionBank[mCurrentIndex].setCheatTrue(mIsCheater);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            for (int i = 0; i < mQuestionBank.length; i++) {
                boolean[] questionsAnswered = savedInstanceState.getBooleanArray(KEY_INDEX_ANSWER);
                boolean[] questionsCheated = savedInstanceState.getBooleanArray(KEY_INDEX_CHEATED);
                mQuestionBank[i].setAlreadyAnswer(questionsAnswered[i]);
                mQuestionBank[i].setCheatTrue(questionsCheated[i]);
            }
            mScore = savedInstanceState.getInt(KEY_INDEX_SCORE);
            mIndexAnswered = savedInstanceState.getInt(KEY_INDEX_ANSWERED);
            mTokensAmount = savedInstanceState.getInt(KEY_INDEX_TOKENS);
    //        mIsCheater = savedInstanceState.getBoolean(KEY_INDEX_CHEATED);
        }

        mQuestionsAnswered = new boolean[mQuestionBank.length];
        mQuestionsCheated = new boolean[mQuestionBank.length];
//        for(int i = 0; i<mQuestionBank.length;i++){
//            mQuestionsIsCheater[i] = false;
//        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNextQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, mTokensAmount);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNextQuestion();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePrevQuestion();
            }
        });
        updateQuestion();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        if (!mQuestionBank[mCurrentIndex].isAlreadyAnswer()) {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        } else {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
    }

    private void updateNextQuestion() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
  //      mIsCheater = false;
        updateQuestion();
    }

    private void updatePrevQuestion() {

        int length = mQuestionBank.length;
        mCurrentIndex = (mCurrentIndex + (length - 1)) % length;
   //     mIsCheater = false;
        updateQuestion();
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        boolean cheatIsTrue = mQuestionBank[mCurrentIndex].isCheatTrue();

        int messageResId = 0;
        if (cheatIsTrue) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mScore++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT).show();
        mIndexAnswered++;
        if (mIndexAnswered == mQuestionBank.length) {
            int score = (int) ((float) mScore / mQuestionBank.length * 100);
            Toast toast = Toast.makeText(QuizActivity.this, "Вы набрали " + score + "% правильных ответов", Toast.LENGTH_SHORT);
            Log.d(MY_TAG, "Result " + score);
            toast.setGravity(Gravity.TOP, toast.getXOffset(), toast.getYOffset());
            toast.show();
        }
        // блокируем кнопку если был дан ответ
        if (!mQuestionBank[mCurrentIndex].isAlreadyAnswer()) {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
            mQuestionBank[mCurrentIndex].setAlreadyAnswer(true);
        }
    }
}
