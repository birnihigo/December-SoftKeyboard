/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.softkeyboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class LatinKeyboardView extends KeyboardView {

    static final int KEYCODE_OPTIONS = -100;
    // TODO: Move this into android.inputmethodservice.Keyboard
    static final int KEYCODE_LANGUAGE_SWITCH = -101;

    // added for Geez Keyboard
    Button[] mKeyVariants; //the six variants of a given Geez character
    private PopupWindow mPopupWindow;   //the popup window for showing the six variants
    private final int mResultCode = 1001;
    private  Context context;


    public LatinKeyboardView(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context=context;
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onClick(View v) {
        if(mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
        super.onClick(v);
    }

    private void initPopup(CharSequence chars){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        //View parent = inflater.inflate(key.popupResId, null);
        View view = inflater.inflate(R.layout.popup_dialog, null);
        mPopupWindow = new PopupWindow();
        mPopupWindow.setContentView(view);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        //mPopupWindow.setFocusable(true);

        //initialize the array
        mKeyVariants = new Button[6];
        mKeyVariants[0] = (Button)view.findViewById(R.id.btnOne);
        mKeyVariants[1] = (Button)view.findViewById(R.id.btnTwo);
        mKeyVariants[2] = (Button)view.findViewById(R.id.btnThree);
        mKeyVariants[3] = (Button)view.findViewById(R.id.btnFour);
        mKeyVariants[4] = (Button)view.findViewById(R.id.btnFive);
        mKeyVariants[5] = (Button)view.findViewById(R.id.btnSix);

        //mKeyVariants[0].setText(chars.charAt(0));

        for(int i = 0; i < 6; ++i) {
            mKeyVariants[i].setText(String.valueOf(chars.charAt(i)));

            final int index = i;
            mKeyVariants[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnKeyboardActionListener().onKey((int) (mKeyVariants[index].getText().toString().charAt(0)), null);
                    mPopupWindow.dismiss();
                }
            });
        }
    }
    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {

            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else {
            if(key.popupCharacters == null){
                return super.onLongPress(key);
            }
            initPopup(key.popupCharacters);
            mPopupWindow.showAtLocation(this, Gravity.TOP, 10, 10);
            mPopupWindow.update(key.x, key.y, 400, 80);
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard)getKeyboard();
        keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }
}
