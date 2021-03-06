package ru.justd.lilwidgets;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * Created by defuera on 10/11/2015.
 * <p>
 * This is a helper class that allows views to display following states:
 * 1) Data is loading
 * 2) No internet connection error
 * 3) No data found message
 * <p>
 * No data found message can be replaced with any custom widget by setting it with {@link LilLoaderWidget}
 * <p>
 * <p>
 * 1) Display loading state with colored loader
 * 2) Display error state (networkError, no data, another type of error) with text, with image, with custom view/layout
 * 3) handle click on
 */
public class LilLoaderWidget extends FrameLayout {

    //region default errors

    public static final NetworkError NETWORK_ERROR = new NetworkError();
    public static final NoDataError NO_DATA_ERROR = new NoDataError();

    //endregion

    private ProgressBar progressBar;
    private ViewGroup errorViewContainer;

    private ClickListener onClickListener;
    private Error error;

    public LilLoaderWidget(Context context) {
        super(context);
        init();
    }

    public LilLoaderWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @CallSuper
    protected void init() {
        inflate(getContext(), R.layout.widget_lil_loader, this);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        errorViewContainer = (ViewGroup) findViewById(R.id.error_widget_container);

        int progressColor = Utils.loadColorFromStyle(getContext(), R.style.LilStyle, R.attr.lilLoaderWidgetProgressColor);
        if (progressColor != 0) {
            Utils.setProgressColor(progressBar, progressColor);
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null && error != null) {
                    onClickListener.onClick(error);
                }
            }
        });
    }

    public void showLoading() {
        error = null;

        show();
        progressBar.setVisibility(VISIBLE);
        errorViewContainer.setVisibility(GONE);
    }

    public void showNetworkError() {
        showError(NETWORK_ERROR);
    }

    public void showNoDataError() {
        showError(NO_DATA_ERROR);
    }

    public void showError(Error error) {
        this.error = error;

        show();
        progressBar.setVisibility(GONE);

        errorViewContainer.setVisibility(VISIBLE);
        errorViewContainer.removeAllViews();

        errorViewContainer.addView(error.inflateErrorView(getContext()));
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    @SuppressWarnings("WeakerAccess")
    public interface Error {
        View inflateErrorView(Context context);
    }

    private static final class NetworkError implements Error {

        private NetworkError() {}

        @Override
        public View inflateErrorView(Context context) {
            return View.inflate(
                    context,
                    Utils.loadLayoutFromStyle(context, R.style.LilStyle, R.attr.lilNetworkErrorView),
                    null
            );
        }

    }

    private static final class NoDataError implements Error {

        private NoDataError() {}

        @Override
        public View inflateErrorView(Context context) {
            return View.inflate(
                    context,
                    Utils.loadLayoutFromStyle(context, R.style.LilStyle, R.attr.lilNoDataErrorView),
                    null
            );
        }

    }

    public void setOnErrorClicked(@NonNull ClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface ClickListener {
        void onClick(@Nullable Error error);
    }

}
