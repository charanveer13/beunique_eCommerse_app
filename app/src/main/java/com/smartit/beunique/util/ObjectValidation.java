package com.smartit.beunique.util;

import android.view.View;

public interface ObjectValidation {

	boolean isEmpty(Object o);

	boolean isEmptyStr(String text);

	boolean isNonEmptyStr(String text);

	boolean isEmptyView(View v);

	String getTextFromView(View v);
}
