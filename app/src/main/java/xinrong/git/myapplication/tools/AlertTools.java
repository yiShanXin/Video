package xinrong.git.myapplication.tools;

import android.content.Context;
import android.os.Build;

/**
 * 封装Toast 提示
 * 
 * @author nike
 * 
 */
public class AlertTools {
	private static TipsToast tipsToast;

	public static void showTips(Context context, int iconResId, int msgResId) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(context.getApplicationContext(),
					msgResId, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		if (iconResId != 0) {
			tipsToast.setIcon(iconResId);
		}
		tipsToast.setText(msgResId);
	}

	public static void showTips(Context context, int iconResId, String msgResId) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(context.getApplicationContext(),
					msgResId, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		if (iconResId != 0) {
			tipsToast.setIcon(iconResId);
		}
		tipsToast.setText(msgResId);
	}

	public static void colseTips() {
		if (tipsToast != null) {
			tipsToast.cancel();
		}
	}
}
