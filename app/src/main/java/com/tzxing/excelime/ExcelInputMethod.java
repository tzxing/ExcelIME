package com.tzxing.excelime;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

public class ExcelInputMethod extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private VoiceKeyboard mVoiceKeyboard;
    private VoiceKeyboardView mExcelKeyboardView;
    private InputMethodManager mInputMethodManager;
    private String mWordSeparators;
    private VoiceKeyboard mCurKeyboard;
    private StringBuilder mComposing = new StringBuilder();
    private long mMetaState;
    private boolean mPredictionOn;
    private boolean mCompletionOn;
    private CompletionInfo[] mCompletions;





    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //mWordSeparators = getResources().getString(R.string.word_separators);
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();
        final Context displayContext = this;
        mVoiceKeyboard = new VoiceKeyboard(this, R.xml.voice);
    }



    @Override
    public void onBindInput() {
        super.onBindInput();
    }

    @Override
    public View onCreateInputView() {
        VoiceKeyboardView inputView = (VoiceKeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        inputView.setOnKeyboardActionListener(this);
        inputView.setKeyboard(mVoiceKeyboard);
        return inputView;
    }

    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        mComposing.setLength(0);
        updateCandidates();

        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;


        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {


            default:
                mCurKeyboard = mVoiceKeyboard;
        }

        //mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    private void updateCandidates() {
    }


    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int i, int[] ints) {

    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
