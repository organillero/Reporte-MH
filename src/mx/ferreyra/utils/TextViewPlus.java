package mx.ferreyra.utils;

import mx.ferreyra.utils.AutoResizeTextView.OnTextResizeListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.text.Layout.Alignment;
import android.widget.TextView;

import com.aureacode.reportemh.R;


public class TextViewPlus  extends TextView{

	private static final String TAG = "TextView";

	// Minimum text size for this text view
	public static final float MIN_TEXT_SIZE = 20;

	// Our ellipse string
	private static final String mEllipsis = "...";

	// Flag for text and/or size changes to force a resize
	@SuppressWarnings("unused")
	private boolean mNeedsResize = false;

	private boolean resizable = false;

	// Registered resize listener
	private OnTextResizeListener mTextResizeListener;

	// Text size that is set from code. This acts as a starting point for resizing
	private float mTextSize;

	// Temporary upper bounds on the starting text size
	private float mMaxTextSize = 0;

	// Lower bounds for text size
	private float mMinTextSize = MIN_TEXT_SIZE;

	// Text view line spacing multiplier
	private float mSpacingMult = 1.0f;

	// Text view additional line spacing
	private float mSpacingAdd = 0.0f;

	// Add ellipsis to text that overflows at the smallest text size
	private boolean mAddEllipsis = true;

	// Off screen canvas for text size rendering
	private static final Canvas sTextResizeCanvas = new Canvas();



	public TextViewPlus(Context context) {
		super(context);
	}

	public TextViewPlus(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
		this.mTextSize = getTextSize();
	}

	public TextViewPlus(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
		this.mTextSize = getTextSize();
	}


	@Override
	protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {

		this.mNeedsResize = true;
		// Since this view may be reused, it is good to reset the text size
		// resetTextSize();
		if (this.resizable)
			resizeText();

		super.onTextChanged(text, start, before, after);
	}

	/**
	 * If the text view size changed, set the force resize flag to true
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);
		if (w != oldw || h != oldh) {
			this.mNeedsResize = true;
			if (this.resizable)
				resizeText();
		}


	}





	private void init(Context ctx, AttributeSet attrs) {
		TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
		String customFont = a.getString(R.styleable.TextViewPlus_customFont);
		boolean rezisable = a.getBoolean(R.styleable.TextViewPlus_resizable, false);

		setCustomFont(ctx, customFont);
		setResizable(rezisable);

		a.recycle();
	}

	public void setResizable (boolean rezisable){
		this.resizable = rezisable;
	}

	public boolean setCustomFont(Context ctx, String asset) {
		Typeface tf = null;
		try {
			tf = Typeface.createFromAsset(ctx.getAssets(), asset);  
		} catch (Exception e) {
			Log.e(TAG, "Could not get typeface: "+e.getMessage());
			return false;
		}

		setTypeface(tf);  
		return true;
	}

	/**
	 * Resize the text size with default width and height
	 */
	public void resizeText() {

		int heightLimit = getHeight() - getPaddingBottom() - getPaddingTop();
		int widthLimit = getWidth() - getPaddingLeft() - getPaddingRight();
		resizeText(widthLimit, heightLimit);
	}

	/**
	 * Resize the text size with specified width and height
	 * @param width
	 * @param height
	 */
	public void resizeText(int width, int height) {
		CharSequence text = getText();
		// Do not resize if the view does not have dimensions or there is no text
		if(text == null || text.length() == 0 || height <= 0 || width <= 0 || this.mTextSize == 0) {
			return;
		}

		// Get the text view's paint object
		TextPaint textPaint = getPaint();

		// Store the current text size
		float oldTextSize = textPaint.getTextSize();
		// If there is a max text size set, use the lesser of that and the default text size
		float targetTextSize = this.mMaxTextSize > 0 ? Math.min(this.mTextSize, this.mMaxTextSize) : this.mTextSize;


		// Get the required text height
		int textHeight = getTextHeight(text, textPaint, width, targetTextSize);


		// Until we either fit within our text view or we had reached our min text size, incrementally try smaller sizes
		while(textHeight > height && targetTextSize > this.mMinTextSize) {
			targetTextSize = Math.max(targetTextSize - 2, this.mMinTextSize);
			textHeight = getTextHeight(text, textPaint, width, targetTextSize);
		}


		// If we had reached our minimum text size and still don't fit, append an ellipsis
		if(this.mAddEllipsis && targetTextSize == this.mMinTextSize && textHeight > height) {
			// Draw using a static layout
			StaticLayout layout = new StaticLayout(text, textPaint, width, Alignment.ALIGN_NORMAL, this.mSpacingMult, this.mSpacingAdd, false);
			layout.draw(sTextResizeCanvas);
			// Check that we have a least one line of rendered text
			if(layout.getLineCount() > 0) {
				// Since the line at the specific vertical position would be cut off,
				// we must trim up to the previous line
				int lastLine = layout.getLineForVertical(height) - 1;
				// If the text would not even fit on a single line, clear it
				if(lastLine < 0) {
					setText("");
				}
				// Otherwise, trim to the previous line and add an ellipsis
				else {
					int start = layout.getLineStart(lastLine);
					int end = layout.getLineEnd(lastLine);
					float lineWidth = layout.getLineWidth(lastLine);
					float ellipseWidth = textPaint.measureText(mEllipsis);

					// Trim characters off until we have enough room to draw the ellipsis
					while(width < lineWidth + ellipseWidth) {
						lineWidth = textPaint.measureText(text.subSequence(start, --end + 1).toString());
					}
					setText(text.subSequence(0, end) + mEllipsis);
				}
			}
		}

		// Some devices try to auto adjust line spacing, so force default line spacing
		// and invalidate the layout as a side effect
		textPaint.setTextSize(targetTextSize);
		setLineSpacing(this.mSpacingAdd, this.mSpacingMult);

		// Notify the listener if registered
		if(this.mTextResizeListener != null) {
			this.mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
		}


		setHeight(textHeight);
		refreshDrawableState();
		invalidate();
		//        on
		//        // Reset force resize flag
		this.mNeedsResize = false;


	}

	// Set the text size of the text paint object and use a static layout to render text off screen before measuring
	private int getTextHeight(CharSequence source, TextPaint paint, int width, float textSize) {
		// Update the text paint object
		paint.setTextSize(textSize);
		// Draw using a static layout
		StaticLayout layout = new StaticLayout(source, paint, width, Alignment.ALIGN_NORMAL, this.mSpacingMult, this.mSpacingAdd, true);
		layout.draw(sTextResizeCanvas);
		return layout.getHeight();
	}

	/**
	 * Return lower text size limit
	 * @return
	 */
	public float getMinTextSize() {
		return this.mMinTextSize;
	}

	/**
	 * Set flag to add ellipsis to text that overflows at the smallest text size
	 * @param addEllipsis
	 */
	public void setAddEllipsis(boolean addEllipsis) {
		this.mAddEllipsis = addEllipsis;
	}

	/**
	 * Return flag to add ellipsis to text that overflows at the smallest text size
	 * @return
	 */
	public boolean getAddEllipsis() {
		return this.mAddEllipsis;
	}

	/**
	 * Reset the text to the original size
	 */
	public void resetTextSize() {
		super.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.mTextSize);
		this.mMaxTextSize = this.mTextSize;
	}

}
