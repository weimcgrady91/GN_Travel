package com.gionee.gntravel.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.gionee.gntravel.R;

public class CustomTextView  extends TextView {
	 
    // Attributes
    private Paint testPaint;
//    private float cTextSize;
    private float DEFAULT_MIN_TEXT_SIZE  ;
    private float DEFAULT_MAX_TEXT_SIZE  ; 
 
    public CustomTextView (Context context, AttributeSet attrs) {
        super(context, attrs);
        DEFAULT_MIN_TEXT_SIZE = context.getResources().getDimension(R.dimen.min_textsize);
        DEFAULT_MAX_TEXT_SIZE = context.getResources().getDimension(R.dimen.max_textsize);
    }
 
    private void refitText(String text, int textWidth) {
        if (textWidth > 0) {
            testPaint = new Paint();
            testPaint.set(this.getPaint());
            int availableWidth = textWidth - this.getPaddingLeft()
                    - this.getPaddingRight();
//            float[] widths = new float[text.length()];
            Rect rect = new Rect();
            testPaint.getTextBounds(text, 0, text.length(), rect);
            int textWidths = rect.width();
//            cTextSize = this.getTextSize();
            if(textWidths > availableWidth) {
            	this.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_MIN_TEXT_SIZE);
            } 
            
        }
    };
     
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    @Override  
    protected void onTextChanged(CharSequence text, int start, int before,  
            int after) {  
        super.onTextChanged(text, start, before, after);  
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_MAX_TEXT_SIZE);
        refitText(text.toString(), this.getWidth());  
    }  
}