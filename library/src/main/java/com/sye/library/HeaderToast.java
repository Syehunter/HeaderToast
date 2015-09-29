package com.sye.library;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Sye on 2015/9/22.
 */
public class HeaderToast implements View.OnTouchListener {

    private final static int CLOSE = 0;

    private final static int ANIM_DURATION = 600;
    private final static int SHOW_DURATION = 2000;

    private final Context mContext;
    private View mHeaderToastView;
    private WindowManager wm;
    private LinearLayout linearLayout;
    private float downX;
    private float downY;
    private ImageView iv_header_toast;
    private TextView tv_header_toast;

    public HeaderToast(Context context){
        //使用applicationContext保证Activity跳转时Toast不会消失
        this.mContext = context.getApplicationContext();
    }

    public void show(String toast){
        showHeaderToast(toast);
    }

    private synchronized void showHeaderToast(String toast) {

        initHeaderToastView();

        setText(toast);

        setHeaderViewInAnim();

        //2s后自动关闭
        mHeaderToastHandler.sendEmptyMessageDelayed(CLOSE, SHOW_DURATION);
    }

    public void showWithCustomIcon(int icon, String toast){
        showHeaderToastWithCustomIcon(icon, toast);
    }

    private synchronized void showHeaderToastWithCustomIcon(int icon, String toast) {
        initHeaderToastView();

        setIcon(icon);

        setText(toast);

        setHeaderViewInAnim();

        //2s后自动关闭
        mHeaderToastHandler.sendEmptyMessageDelayed(CLOSE, SHOW_DURATION);
    }

    /**
     * 为mHeaderToastView添加进入动画
     */
    private void setHeaderViewInAnim() {
        ObjectAnimator a = ObjectAnimator.ofFloat(mHeaderToastView, "translationY", -700, 0);
        a.setDuration(ANIM_DURATION);
        a.start();
    }

    private void setText(String toast) {
        tv_header_toast.setText(toast);
    }

    private void setIcon(int icon) {
        iv_header_toast.setBackgroundResource(icon);
    }

    private void initHeaderToastView() {
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        //为mHeaderToastView添加parent使其能够展示动画效果
        linearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(llParams);

        mHeaderToastView = View.inflate(mContext, R.layout.header_toast, null);
        //为mHeaderToastView添加滑动删除事件
        mHeaderToastView.setOnTouchListener(this);
        iv_header_toast = (ImageView) mHeaderToastView.findViewById(R.id.iv_header_toast);
        tv_header_toast = (TextView) mHeaderToastView.findViewById(R.id.tv_header_toast);

        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        wmParams.gravity = Gravity.CENTER | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        linearLayout.addView(mHeaderToastView);
        wm.addView(linearLayout, wmParams);
    }

    public Handler mHeaderToastHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CLOSE:
                    animDismiss();
                    break;
                default:
                    Log.e("HeaderToast", "no selection matches");
                    break;
            }
        }
    };

    /**
     * HeaderToast消失动画
     */
    private void animDismiss() {
        if(null == linearLayout || null == linearLayout.getParent()){
            //如果linearLayout已经被从wm中移除，直接return
            return;
        }

        ObjectAnimator a = ObjectAnimator.ofFloat(mHeaderToastView, "translationY", 0, -700);
        a.setDuration(ANIM_DURATION);
        a.start();

        a.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 移除HeaderToast
     */
    private void dismiss() {
        if(null != linearLayout && null != linearLayout.getParent()){
            wm.removeView(linearLayout);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getRawX();
                float currentY = event.getRawY();
                if((downX - currentX) >= 40 || (downY - currentY) >= 10){
                    animDismiss();
                }
                break;
        }
        return true;
    }
}
