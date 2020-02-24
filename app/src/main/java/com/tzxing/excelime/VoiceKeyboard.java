package com.tzxing.excelime;

import android.content.Context;
import android.inputmethodservice.Keyboard;

public class VoiceKeyboard extends Keyboard {
    public VoiceKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public VoiceKeyboard(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
    }

    public VoiceKeyboard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
    }

    public VoiceKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }
}
