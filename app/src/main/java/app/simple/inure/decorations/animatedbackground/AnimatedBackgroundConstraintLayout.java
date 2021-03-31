package app.simple.inure.decorations.animatedbackground;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import app.simple.inure.R;
import app.simple.inure.decorations.corners.LayoutBackground;
import app.simple.inure.util.ColorUtils;

import static app.simple.inure.decorations.animatedbackground.Utils.animateBackground;

public class AnimatedBackgroundConstraintLayout extends ConstraintLayout {
    
    public AnimatedBackgroundConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        LayoutBackground.setBackground(context, this, attrs);
    }
    
    public AnimatedBackgroundConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        LayoutBackground.setBackground(context, this, attrs);
    }
    
    @SuppressLint ("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                animateBackground(
                        ColorUtils.INSTANCE.changeAlpha(ColorUtils.INSTANCE.resolveAttrColor(getContext(), R.attr.colorAppAccent), Utils.alpha),
                        this);
                break;
            }
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP: {
                animateBackground(Color.TRANSPARENT, this);
                break;
            }
        }
        
        return super.onTouchEvent(event);
    }
    
    @Override
    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        clearAnimation();
    }
}
