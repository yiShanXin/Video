package xinrong.git.myapplication.view;

/**
 * Created by nike on 16/1/9.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;
/**
 * 透明的面板
 * @author nike
 *
 */
public class LinearTransparentPanel extends LinearLayout {
    private Paint innerPaint, borderPaint;

    public LinearTransparentPanel(Context context) {
        this(context, null);
    }

    public LinearTransparentPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        innerPaint = new Paint();

        innerPaint.setARGB(120, 0, 0, 0); //gray

//        innerPaint.setARGB(180, 255, 255, 255); //gray
        innerPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setARGB(0, 255, 255, 255);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(0);
    }

    public void setInnerPaint(Paint innerPaint) {
        this.innerPaint = innerPaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        RectF drawRect = new RectF();
        drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

        canvas.drawRoundRect(drawRect, 0, 0, innerPaint);
        canvas.drawRoundRect(drawRect, 0, 0, borderPaint);

        super.dispatchDraw(canvas);
    }

}