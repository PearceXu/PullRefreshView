package com.library.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;


/**
 * author ： xubaipei
 * create date： 2019-05-14
 */
public class PullRefreshView extends FrameLayout {

    private static final String TAG = "PullRefreshView";

    private static final String VS_TAG = "VS_TAG";

    ViewDragHelper mDraggerHelper;

    DraggerCB mCb = new DraggerCB();

    boolean mIntercept = true;

    int mPullOffset = 0;

    PullRefreshListener mListener;

    Context mContext;

    View mPullLoadingView;

    ImageView mStatusView;

    TextView mStatusTextView;

    ProgressBar mProgressBar;

    int mPullLoadingViewHeight;

//    RotateAnimation mLoadingAnimation = null;

    final int VIEW_STATUS_PULL = 0x01;
    final int VIEW_STATUS_REDY_RELEASE = 0x02;
    final int VIEW_STATUS_LOADING = 0x03;
    final int VIEW_STATUS_LOADING_FINISH = 0x04;

    int mViewStatus = VIEW_STATUS_LOADING_FINISH;

    public void setListener(PullRefreshListener listener) {
        this.mListener = listener;
    }

    class DraggerCB extends ViewDragHelper.Callback{
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return true;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (dy < 0){
                return 0;
            }
            mPullOffset = top;
            if (mPullOffset > mPullLoadingViewHeight){
                changeViewByStatus(VIEW_STATUS_REDY_RELEASE);
            }else {
                changeViewByStatus(VIEW_STATUS_PULL);
            }
            return top;
        }
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (mPullOffset >= mPullLoadingViewHeight && mPullOffset + yvel > 0){
                if (mListener != null){
                    changeViewByStatus(VIEW_STATUS_LOADING);
                    mListener.onPullRefresh();
                    mDraggerHelper.settleCapturedViewAt(0, mPullLoadingViewHeight);
                }else {
                    mDraggerHelper.settleCapturedViewAt(0, 0);
                }
            }else {
                mDraggerHelper.settleCapturedViewAt(0, 0);
            }
            invalidate();

        }

    }



    public PullRefreshView(Context context) {
        this(context,null);
    }

    public PullRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PullRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPullLoadingView = LayoutInflater.from(mContext).inflate(R.layout.layout_pull_refresh,null,false);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;
        addView(mPullLoadingView,0,layoutParams);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDraggerHelper = ViewDragHelper.create(this, mCb);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mPullLoadingView != null && mPullLoadingViewHeight == 0){
            mPullLoadingViewHeight = mPullLoadingView.getMeasuredHeight();
            mStatusTextView = mPullLoadingView.findViewById(R.id.status_view_tv);
            mStatusView = mPullLoadingView.findViewById(R.id.status_view_iv);
            mProgressBar = mPullLoadingView.findViewById(R.id.progress_bar);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = mDraggerHelper.shouldInterceptTouchEvent(ev);
        if (mIntercept){
            ret = true;
        }else {
            ret = false;
        }
        return ret;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = super.dispatchTouchEvent(ev);
        if (!ret){
            mIntercept = true;
        }
        if (ev.getAction() == MotionEvent.ACTION_UP){
            mIntercept = false;
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            mDraggerHelper.processTouchEvent(ev);
        }

        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDraggerHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDraggerHelper != null && mDraggerHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private String getActionStr(int action){
        switch (action){
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            default:
                return "UNKOWN";
        }
    };

    public interface PullRefreshListener{
        void onPullRefresh();
    }


//    private void startLoading(){
//        if (mLoadingAnimation == null) {
//            mLoadingAnimation = new RotateAnimation(0,360,
//                    Animation.RELATIVE_TO_SELF, 0.5f,
//                    Animation.RELATIVE_TO_SELF,0.5f);
//            mLoadingAnimation.setRepeatCount(INFINITE);
//            mLoadingAnimation.setRepeatMode(ValueAnimator.REVERSE);
//            mLoadingAnimation.setDuration(500);
//            mStatusView.setAnimation(mLoadingAnimation);
//        }
//        mLoadingAnimation.start();
//    }

    public void stopLoading(){
        changeViewByStatus(VIEW_STATUS_LOADING_FINISH);
        if (mDraggerHelper != null){
            View view  = getChildAt(1);
            mDraggerHelper.smoothSlideViewTo(view,0,0);
        }
    }

    private void changeViewByStatus(int status){
        if (mViewStatus == status){
            return;
        }
        mProgressBar.setVisibility(INVISIBLE);
        switch (status){
            case VIEW_STATUS_PULL:
                mStatusView.setVisibility(VISIBLE);
                mStatusView.setImageResource(R.drawable.ic_down_arrow);
                mStatusTextView.setText("下拉刷新...");
//                if (mLoadingAnimation != null){
//                    mLoadingAnimation.cancel();
//                }
                break;
            case VIEW_STATUS_LOADING:
                mStatusView.setVisibility(GONE);
//                mStatusView.setImageResource(R.drawable.ic_refresh);
                mStatusTextView.setText("正在加载中...");
                mProgressBar.setVisibility(VISIBLE);
//                startLoading();
                break;
            case VIEW_STATUS_REDY_RELEASE:
                mStatusView.setVisibility(VISIBLE);
                mStatusView.setImageResource(R.drawable.ic_up_arrow);
                mStatusTextView.setText("释放后刷新");
//                if (mLoadingAnimation != null){
//                    mLoadingAnimation.cancel();
//                }
                break;
            case VIEW_STATUS_LOADING_FINISH:
                mStatusTextView.setText("刷新完成");
                mStatusView.setImageResource(R.drawable.ic_finish);
//                if (mLoadingAnimation != null){
//                    mLoadingAnimation.cancel();
//                }
                break;
        }
        mViewStatus = status;
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }
}
