package com.yjt.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.yjt.keyboard.constant.Constant;
import com.yjt.keyboard.listener.OnKeyboardListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DynamicKeyBoardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {

    private final List<Character> keys = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    private int deleteWidth;
    private int deleteHeight;
    private int deleteBackground;
    private Drawable deleteDrawable;
    private Rect deleteDrawRect;
    private OnKeyboardListener onKeyboardListener;

    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        this.onKeyboardListener = onKeyboardListener;
    }

    public DynamicKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public DynamicKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DynamicKeyBoardView, defStyleAttr, 0);
        deleteDrawable = typedArray.getDrawable(R.styleable.DynamicKeyBoardView_delete_drawable);
        deleteBackground = typedArray.getColor(R.styleable.DynamicKeyBoardView_delete_background, Color.TRANSPARENT);
        deleteWidth = typedArray.getDimensionPixelOffset(R.styleable.DynamicKeyBoardView_delete_width, -1);
        deleteHeight = typedArray.getDimensionPixelOffset(R.styleable.DynamicKeyBoardView_delete_height, -1);
        typedArray.recycle();

        Keyboard keyboard = new Keyboard(context, R.xml.keyboard);
        setKeyboard(keyboard);
        setEnabled(true);
        setPreviewEnabled(false);
        setOnKeyboardActionListener(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Keyboard.Key key : getKeyboard().getKeys()) {
            if (key.codes[0] == Constant.KeyBoard.EMPTY_CODE) {
                drawKeyBackground(key, canvas, deleteBackground);
            } else if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                drawKeyBackground(key, canvas, deleteBackground);
                drawDeleteButton(key, canvas);
            }
        }
    }

    private void drawKeyBackground(Keyboard.Key key, Canvas canvas, int color) {
        ColorDrawable drawable = new ColorDrawable(color);
        drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        drawable.draw(canvas);
    }

    private void drawDeleteButton(Keyboard.Key key, Canvas canvas) {
        if (deleteDrawable == null) {
            return;
        }
        if (deleteDrawRect == null || deleteDrawRect.isEmpty()) {
            int drawWidth, drawHeight;
            int intrinsicWidth = deleteDrawable.getIntrinsicWidth();
            int intrinsicHeight = deleteDrawable.getIntrinsicHeight();
            if (deleteWidth > 0 && deleteHeight > 0) {
                drawWidth = deleteWidth;
                drawHeight = deleteHeight;
            } else if (deleteWidth > 0 && deleteHeight <= 0) {
                drawWidth = deleteWidth;
                drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
            } else if (deleteWidth <= 0 && deleteHeight > 0) {
                drawHeight = deleteHeight;
                drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
            } else {
                drawWidth = intrinsicWidth;
                drawHeight = intrinsicHeight;
            }
            if (drawWidth > key.width) {
                drawWidth = key.width;
                drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
            }
            if (drawHeight > key.height) {
                drawHeight = key.height;
                drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
            }
            int left = key.x + (key.width - drawWidth) / 2;
            int top = key.y + (key.height - drawHeight) / 2;
            deleteDrawRect = new Rect(left, top, left + drawWidth, top + drawHeight);
        }
        if (deleteDrawRect != null && !deleteDrawRect.isEmpty()) {
            deleteDrawable.setBounds(deleteDrawRect.left, deleteDrawRect.top, deleteDrawRect.right, deleteDrawRect.bottom);
            deleteDrawable.draw(canvas);
        }
    }

    public void shuffleKeyboard() {
        Keyboard keyboard = getKeyboard();
        if (keyboard != null && keyboard.getKeys() != null && keyboard.getKeys().size() > 0) {
            Collections.shuffle(keys);
            int index = 0;
            for (Keyboard.Key key : getKeyboard().getKeys()) {
                if (key.codes[0] != Constant.KeyBoard.EMPTY_CODE && key.codes[0] != Keyboard.KEYCODE_DELETE) {
                    char code = keys.get(index++);
                    key.codes[0] = code;
                    key.label = Character.toString(code);
                }
            }
            setKeyboard(keyboard);
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
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            if (onKeyboardListener != null) {
                onKeyboardListener.onDelete();
            }
        } else if (primaryCode != Constant.KeyBoard.EMPTY_CODE) {
            if (onKeyboardListener != null) {
                onKeyboardListener.onInsert(Character.toString((char) primaryCode));
            }
        }
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
