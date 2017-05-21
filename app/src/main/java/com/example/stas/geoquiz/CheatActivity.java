package com.example.stas.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.stas.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.example.stas.geoquiz.answer_shown";
    private static final String EXTRA_TOKENS_AMOUNT = "com.example.stas.geoquiz.tokens_amount";
    private static final String KEY_INDEX_CHEATING = "indexCheating";
    private static final String KEY_INDEX_TOKENS = "indexTokens";
//    private static final String MY_TAG = "test";

    private boolean mAnswerIsTrue;
    private boolean mCheatingIsTrue;
    private int mTokensAmount;
    private Button mShowAnswerButton;
    private TextView mAnswerTextView, mBuildVersionTextView, mTokensTextView;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int tokensAmount) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_TOKENS_AMOUNT, tokensAmount);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int getTokensAmount(Intent result){
        return result.getIntExtra(EXTRA_TOKENS_AMOUNT, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if (savedInstanceState != null) {
            mTokensAmount = savedInstanceState.getInt(KEY_INDEX_TOKENS);
            mCheatingIsTrue = savedInstanceState.getBoolean(KEY_INDEX_CHEATING);
            setAnswerShownResult(mCheatingIsTrue);
        }
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mTokensAmount = getIntent().getIntExtra(EXTRA_TOKENS_AMOUNT, 0);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        mTokensTextView = (TextView) findViewById(R.id.tokens_text_view);
        refreshTokensTextView();

        mBuildVersionTextView = (TextView) findViewById(R.id.build_version_text_view);
        mBuildVersionTextView.setText(String.format(getString(R.string.build_version), Build.VERSION.SDK_INT));

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        if (mTokensAmount == 0) mShowAnswerButton.setEnabled(false);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(MY_TAG, "mAmountTokens = " + mTokensAmount);
                mTokensAmount--;
                refreshTokensTextView();
                // Log.d(MY_TAG, "mAmountTokens = " + mTokensAmount);
                mTokensTextView.invalidate();
                mCheatingIsTrue = true;
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(mCheatingIsTrue);

                //adding activity animation code
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void refreshTokensTextView() {
        mTokensTextView.setText(String.format(getResources().getString(R.string.tokens_text), mTokensAmount));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_INDEX_CHEATING, mCheatingIsTrue);
        outState.putInt(KEY_INDEX_TOKENS, mTokensAmount);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_TOKENS_AMOUNT, mTokensAmount);
        setResult(RESULT_OK, data);
    }
}
