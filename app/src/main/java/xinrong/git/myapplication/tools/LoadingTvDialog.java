package xinrong.git.myapplication.tools;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import org.xutils.common.util.LogUtil;

import xinrong.git.myapplication.R;

/**
 * 加载中Dialog
 * 
 * @author xml
 */
public class LoadingTvDialog extends Dialog {

	private TextView tips_loading_msg;

	private String message = null;
	boolean isCancel = false;

	public LoadingTvDialog(Context context) {
		super(context);
		message = getContext().getResources().getString(R.string.msg_load_ing);
		init();
	}

	public LoadingTvDialog(Context context, int theme) {
		super(context,theme);
		message = getContext().getResources().getString(R.string.msg_load_ing);
		init();
	}

	public LoadingTvDialog(Context context, String message) {
		super(context);
		this.message = message;
		init();
	}

	public LoadingTvDialog(Context context, int theme, String message) {
		super(context, theme);
		this.message = message;
		init();
	}

	public void setDialogCancel(boolean isCancel) {
		this.isCancel = isCancel;
		init();
	}

	void init() {
		this.setCancelable(isCancel);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.view_tips_tv_loading);
		tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);
		tips_loading_msg.setText(this.message);
	}

	public void setText(String message) {
		this.message = message;
		tips_loading_msg.setText(this.message);
		LogUtil.e("setText" +message);
	}

	public void setText(int resId) {
		setText(getContext().getResources().getString(resId));
	}

}
