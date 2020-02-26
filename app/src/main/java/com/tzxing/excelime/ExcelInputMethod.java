package com.tzxing.excelime;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;

public class ExcelInputMethod extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private VoiceKeyboard mVoiceKeyboard;
    private InputMethodManager mInputMethodManager;
    private String mWordSeparators;
    private KeyboardView mInputView;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private SpeechRecognizer mIat;
    private Toast mToast;
    private String resultType = "json";
    private StringBuffer buffer = new StringBuffer();
    private boolean isPrint = true;
    int ret = 0; // 函数调用返回值

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
        } else if (primaryCode == -7) {
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：" + ret + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            } else {
                showTip(getString(R.string.text_begin));
            }

        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5e098817");
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        setParam();

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


    public void setParam() {


        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIat.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
        mIat.setParameter(SpeechConstant.SUBJECT, null);
        //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //此处engineType为“cloud”
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
        //设置语音输入语言，zh_cn为简体中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //设置结果返回语言
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        //自动停止录音，范围{0~10000}
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
    }


    private void printResult(RecognizerResult results) {
        isPrint = !isPrint;
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        Log.i("Tag", resultBuffer.toString());

        if (isPrint) {
            getCurrentInputConnection().commitText(resultBuffer.toString(), 1);
        }
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("TAG", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.e("TAG", "初始化失败，错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。

            showTip(error.getPlainDescription(true));

        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("TAG", results.getResultString());
            //System.out.println(flg++);
            if (resultType.equals("json")) {

                printResult(results);


            } else if (resultType.equals("plain")) {
                buffer.append(results.getResultString());
            }


        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d("TAG", "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

}


