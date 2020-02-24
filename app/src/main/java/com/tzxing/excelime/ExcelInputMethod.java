package com.tzxing.excelime;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

public class ExcelInputMethod extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private VoiceKeyboard mVoiceKeyboard;
    private InputMethodManager mInputMethodManager;
    private String mWordSeparators;
    private StringBuilder mComposing = new StringBuilder();
    private int mLastDisplayWidth;
    private VoiceKeyboardView mInputView;


    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
    }

    @NonNull
    Context getDisplayContext() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return this;
        }
        final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        return createDisplayContext(wm.getDefaultDisplay());
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();

        final Context displayContext = getDisplayContext();
        if (mVoiceKeyboard != null) {
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mVoiceKeyboard = new VoiceKeyboard(displayContext, R.xml.voice);
    }

    @Override
    public View onCreateInputView() {
        mInputView = (VoiceKeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setKeyboard(mVoiceKeyboard);
        return mInputView;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
        if (mInputView != null) {
            mInputView.closing();
        }
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        mInputView.closing();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                return false;
            default:
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }


    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }


    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }


    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (isWordSeparator(primaryCode)) {
            // Handle separator

            sendKey(primaryCode);
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    private void handleBackspace() {

        keyDownUp(KeyEvent.KEYCODE_DEL);

    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }

        getCurrentInputConnection().commitText(
                String.valueOf((char) primaryCode), 1);

    }

    @Override
    public void onText(CharSequence charSequence) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        ic.commitText(charSequence, 0);
        ic.endBatchEdit();

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
