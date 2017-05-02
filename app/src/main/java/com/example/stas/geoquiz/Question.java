package com.example.stas.geoquiz;

/**
 * Created by Stas on 27.04.2017.
 */

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mAlreadyAnswer;

    public Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public boolean isAlreadyAnswer() {
        return mAlreadyAnswer;
    }

    public void setAlreadyAnswer(boolean alreadyAnswer) {
        mAlreadyAnswer = alreadyAnswer;
    }
}
