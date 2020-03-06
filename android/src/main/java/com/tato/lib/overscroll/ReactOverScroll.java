package com.tato.lib.overscroll;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by eagleliu on 2017/12/13.
 */
public class ReactOverScroll extends SizeMonitoringFrameLayout implements IOverScrollUpdateListener {
    private boolean mBounce = false;

    public ReactOverScroll(Context context) {
        super(context);
    }

    public ReactOverScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReactOverScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ReactOverScroll can host only one direct child");
        }
        super.addView(child, index);

        if (mBounce) {
            setUpOverScroll(child);
        }
    }

    private void setUpOverScroll(View child) {
        if (child != null) {
            final IOverScrollDecor iOverScrollDecor;
            if (child instanceof ScrollView) {
                iOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll((ScrollView) child);
            } else if (child instanceof HorizontalScrollView) {
                iOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll((HorizontalScrollView) child);
            } else if (child instanceof ListView) {
                iOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll((ListView) child);
            } else if (child instanceof ViewPager) {
                iOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll((ViewPager) child);
            } else {
                iOverScrollDecor = OverScrollDecoratorHelper.setUpStaticOverScroll(child, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            }
            iOverScrollDecor.setOverScrollUpdateListener(this);
        }
    }

    public void setBounce(boolean bounce) {
        mBounce = bounce;
        if (bounce) {
            setUpOverScroll(getChildAt(0));
        }
    }

    @Override
    public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
        WritableMap map = new WritableNativeMap();
        map.putDouble("offset", offset);
        sendEvent((ReactContext)getContext(),"OverScroll.RESULT",map);
    }

    protected void sendEvent(ReactContext context, String eventName, WritableMap params){
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName,params);
    }

}
