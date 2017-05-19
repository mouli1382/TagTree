package in.mobifirst.tagtree.view;

import android.content.Context;
import android.util.AttributeSet;

public class WeekButton extends android.widget.ToggleButton {

    private static int mWidth;

    public WeekButton(Context context) {
        super(context);
    }

    public WeekButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static void setSuggestedWidth(int w) {
        mWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int h = getMeasuredHeight();
//        int w = getMeasuredWidth();
        int w = mWidth;
        if (h > 0 && w > 0) {
            if (w < h) {
                if (MeasureSpec.getMode(getMeasuredHeightAndState()) != MeasureSpec.EXACTLY) {
                    h = w;
                }
            } else if (h < w) {
                if (MeasureSpec.getMode(getMeasuredWidthAndState()) != MeasureSpec.EXACTLY) {
                    w = h;
                }
            }
        }
        setMeasuredDimension(w, h);
    }
}
