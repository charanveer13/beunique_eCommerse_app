package com.smartit.beunique.components;

import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.util.FontTypeface;
import com.smartit.beunique.util.UIUtil;
import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 8/2/19.
 */

public class BUniqueTextView extends AppCompatTextView {

    public BUniqueTextView(Context context) {
        super ( context );
        init ( context );
    }

    public BUniqueTextView(Context context, AttributeSet attrs) {
        super ( context, attrs );
        init ( context );
    }

    public BUniqueTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super ( context, attrs, defStyleAttr );
        init ( context );
    }

    private void init(Context context) {
        if(this.isInEditMode ( )) {
            return;
        }

        try {
            if(getTypeface ( ) != null) {
                SessionSecuredPreferences securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
                int selectedLangId = securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
                FontTypeface fontTypeface = FontTypeface.getInstance ( context );
                this.setTypeface ( selectedLangId == 1 ? fontTypeface.getEnglishTypeface ( ) : fontTypeface.getArabicTypeface ( ) );
            }
        } catch (Exception e) {
            e.printStackTrace ( );
        }

    }

    public void setTextDimen(@DimenRes int dimenID) {
        super.setTextSize ( TypedValue.COMPLEX_UNIT_PX, UIUtil.getDimension ( dimenID ) );
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize ( TypedValue.COMPLEX_UNIT_DIP, size );
    }

}
