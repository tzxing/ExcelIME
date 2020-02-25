package com.tzxing.excelime;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.util.Log;
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
    private KeyboardView mInputView;


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
        mVoiceKeyboard = new VoiceKeyboard(displayContext, R.xml.voice);
    }

    @Override
    public View onCreateInputView() {
        mInputView = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
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
        mInputView.setKeyboard(mVoiceKeyboard);
        mInputView.closing();//??????
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void sendKey(int keyCode) {
        //传入的参数是ASCII
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
        Log.i("TAG", "onPress");


    }

    @Override
    public void onRelease(int i) {
        Log.i("TAG", "onRelease");

    }

    //Send a key press to the listener.
    //primaryCode	int: this is the key that was pressed
    //keyCodes	int: the codes for all the possible alternative keys with the primary code being
    // the first. If the primary key code is a single character such as an alphabet or number or
    // symbol, the alternatives will include other characters that may be on the same key or
    // adjacent keys. These codes are useful to correct for accidental presses of a key adjacent to
    // the intended key.
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Log.i("TAG", "onkey");
        if (isWordSeparator(primaryCode)) {
            // Handle separator

            sendKey(primaryCode);
        }else if (primaryCode==-7){
            sendKey(0x597d);

        }
        else if (primaryCode == Keyboard.KEYCODE_DELETE) {
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
        Log.i("TAG", "handleback");

        keyDownUp(KeyEvent.KEYCODE_DEL);
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        Log.i("TAG", "handleCharacter");

        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }

        getCurrentInputConnection().commitText(
                String.valueOf((char) primaryCode), 1);

    }

    //Sends a sequence of characters to the listener.
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
        Log.i("TAG", "swipeLeft");

    }

    @Override
    public void swipeRight() {
        Log.i("TAG", "swipeRight");

    }

    @Override
    public void swipeDown() {
        Log.i("TAG", "swipeDown");

    }

    @Override
    public void swipeUp() {
        Log.i("TAG", "swipeUp");

    }
}


