package com.smartit.beunique.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.smartit.beunique.R;
import com.smartit.beunique.util.GlobalUtil;
import com.smartit.beunique.util.ObjectUtil;
import com.smartit.beunique.util.StringUtil;

/**
 * Created by android on 19/2/19.
 */

public class BUniqueQuantityField extends LinearLayout {

    private TextView value;
    private NumberPicker.OnValueChangeListener onValueChangeListener;
    private int incrementValue = 1;
    private boolean isRoundOFFField;
    private Integer allowedDegitAfterDecimal = 2;
    private Integer maxLength;
    private CardView quantityValueContainer;
    private FontAwesomeIcon minus, plus;
    private Integer valueSize;
    private boolean disableClick;
    @StringRes
    int deleteMsgID;

    public BUniqueQuantityField(Context context) {
        this ( context, null );
    }

    public BUniqueQuantityField(Context context, AttributeSet attrs) {
        this ( context, attrs, 0 );
    }

    public BUniqueQuantityField(Context context, AttributeSet attrs, int defStyleAttr) {
        super ( context, attrs, defStyleAttr );
        initAttrs ( attrs );
        init ( context );
    }

    private void init(Context context) {
        if(this.isInEditMode ( )) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from ( context );
        inflater.inflate ( R.layout.quantity_field_layout, this, true );
        this.minus = this.findViewById ( R.id.minus );
        this.plus = this.findViewById ( R.id.plus );
        this.value = this.findViewById ( R.id.value );
        this.quantityValueContainer = this.findViewById ( R.id.quantityValueContainer );
        this.minus.setOnClickListener ( this.minusPlusClickListener );
        this.plus.setOnClickListener ( this.minusPlusClickListener );
        this.setOnLongClickListener ( minus );
        this.setOnLongClickListener ( plus );
        this.value.setText ( "0" );
        if(this.valueSize != null) {
            this.value.setTextSize ( this.valueSize );
        }
        this.value.setOnClickListener ( null );
    }

    private void initAttrs(AttributeSet attrs) {
        if(attrs != null) {
            TypedArray attr = getContext ( ).obtainStyledAttributes ( attrs, R.styleable.BUniqueQuantityField, 0, 0 );
            if(attr.hasValue ( R.styleable.BUniqueQuantityField_textSize )) {
                this.valueSize = attr.getDimensionPixelSize ( R.styleable.BUniqueQuantityField_textSize, getResources ( ).getDimensionPixelSize ( R.dimen._15sdp ) );
            }
            this.disableClick = attr.getBoolean ( R.styleable.BUniqueQuantityField_disableClick, false );
            attr.recycle ( );
        }
    }

    private OnClickListener minusPlusClickListener = new OnClickListener ( ) {

        double amount;

        @Override
        public void onClick(View view) {
            amount = GlobalUtil.stringToDouble ( getValue ( ) );
            if(view.getId ( ) == R.id.minus) {
                if(amount == 1) {
                    new GlobalAlertDialog ( getContext ( ), true, false ) {
                        @Override
                        public void onConfirmation() {
                            amount--;
                            setValue ( String.valueOf ( amount ) );
                        }
                    }.show ( StringUtil.getStringForID ( getMessageOnDelete ( ) ) );
                    return;
                } else if(amount - 1 >= 0) {
                    amount--;
                } else {
                    amount = 0;
                }
            } else if(view.getId ( ) == R.id.plus) {
                if(amount != getMaxValue ( )) {
                    amount++;
                }
            }
            setValue ( String.valueOf ( amount ) );
        }
    };

    public void setValue(String number) {
        value.setText ( number.split ( "\\." )[ 0 ] );
    }

    public String getValue() {
        return !ObjectUtil.isEmptyView ( value ) ? ObjectUtil.getTextFromView ( value ) : "0";
    }

    public double getMaxValue() {
        return maxLength == null ? Long.MAX_VALUE : Math.pow ( 10, maxLength ) - 1;
    }

    private @StringRes
    int getMessageOnDelete() {
        return deleteMsgID != 0 ? deleteMsgID : R.string.sureToDelete;
    }

    public void setOnLongClickListener(View view) {
        new ContinuousLongClickListener ( view, new OnLongClickListener ( ) {
            @Override
            public boolean onLongClick(View v) {
                if(v.getId ( ) == R.id.minus && GlobalUtil.stringToDouble ( getValue ( ) ) - incrementValue <= 1) {
                    incrementValue = 1;
                    return false;
                }
                getMinusPlusNewValue ( );
                double currentValue = GlobalUtil.stringToDouble ( getValue ( ) );
                if(v.getId ( ) == R.id.plus) {
                    setValue ( String.valueOf ( currentValue + incrementValue >= getMaxValue ( ) ? getMaxValue ( ) : currentValue + incrementValue ) );
                } else if(v.getId ( ) == R.id.minus) {
                    setValue ( String.valueOf ( currentValue - incrementValue <= 0 ? 0 : currentValue - incrementValue ) );
                }
                return false;
            }
        } );
    }

    public int getMinusPlusNewValue() {
        incrementValue = 1;
        Handler hand1 = new Handler ( );
        hand1.postDelayed ( new Runnable ( ) {
            @Override
            public void run() {
                incrementValue = 10;
            }
        }, 1000 );
        return incrementValue;
    }


    public class ContinuousLongClickListener implements OnTouchListener, OnLongClickListener {
        private static final int WHAT = 264776257;
        private final OnLongClickListener onLongClickListener;
        private Handler handler;
        private int delay;

        private ContinuousLongClickListener(View view, OnLongClickListener onLongClickListener) {
            this.onLongClickListener = onLongClickListener;
            handler = new Handler ( );
            view.setOnLongClickListener ( this );
            view.setOnTouchListener ( this );
        }

        private int getDelayValue() {
            delay = 100;
            Handler hand = new Handler ( );
            hand.postDelayed ( new Runnable ( ) {
                @Override
                public void run() {
                    delay = 10;
                }
            }, 1500 );

            return delay;
        }

        @Override
        public boolean onLongClick(final View view) {
            getDelayValue ( );
            handler.post ( new Runnable ( ) {
                @Override
                public void run() {
                    view.performHapticFeedback ( HapticFeedbackConstants.VIRTUAL_KEY );
                    onLongClickListener.onLongClick ( view );
                    Message message = Message.obtain ( handler, this );
                    message.what = WHAT;
                    handler.sendMessageDelayed ( message, delay );
                }
            } );
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean isReleased = event.getAction ( ) == MotionEvent.ACTION_UP || event.getAction ( ) == MotionEvent.ACTION_CANCEL;
            if(isReleased) {
                handler.removeMessages ( WHAT );
            }
            return false;
        }
    }




}
