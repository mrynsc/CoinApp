package com.pow.mining;


import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;


public class MyApp extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);

        // Log the Mobile Ads SDK version.
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        MobileAds.initialize(
                this,
                initializationStatus -> {
                });

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager();
    }

//    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    protected void onMoveToForeground() {
//        // Show the ad (if available) when the app moves to foreground.
//    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {

        appOpenAdManager.showAdIfAvailable(currentActivity);

    }


    /**
     * ActivityLifecycleCallback methods.
     */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    /**
     * Shows an app open ad.
     *
     * @param activity                 the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    public void showAdIfAvailable(
            @NonNull Activity activity,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable2(activity, onShowAdCompleteListener);
    }

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete
     * (i.e. dismissed or fails to show).
     */
    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    /**
     * Inner class that loads and shows app open ads.
     */
    private class AppOpenAdManager {
        //TODO Açılış Reklamı
        private static final String LOG_TAG = "AppOpenAdManager";
        private static final String AD_UNIT_ID = "ca-app-pub-3857955460068630/6538746126";

        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        /**
         * Keep track of the time an app open ad is loaded to ensure you don't show an expired ad.
         */
        private long loadTime = 0;

        /**
         * Constructor.
         */
        public AppOpenAdManager() {
        }

        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        private void loadAd(Context context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context,
                    AD_UNIT_ID,
                    request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        /**
                         * Called when an app open ad has loaded.
                         *
                         * @param ad the loaded app open ad.
                         */
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();

                            Log.d(LOG_TAG, "onAdLoaded.");


                            appOpenAd.setFullScreenContentCallback(
                                    new FullScreenContentCallback() {
                                        /** Called when full screen content is dismissed. */
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            // Set the reference to null so isAdAvailable() returns false.
                                            appOpenAd = null;
                                            isShowingAd = false;

                                            Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");


                                        }

                                        /** Called when fullscreen content failed to show. */
                                        @Override
                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                                            appOpenAd = null;
                                            isShowingAd = false;

                                            Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());


                                        }

                                        /** Called when fullscreen content is shown. */
                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
                                        }
                                    });


                            isShowingAd = true;
                            appOpenAd.show(currentActivity);


                        }

                        /**
                         * Called when an app open ad has failed to load.
                         *
                         * @param loadAdError the error.
                         */
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            isLoadingAd = false;
                            Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                        }
                    });
        }

        /**
         * Check if ad was loaded more than n hours ago.
         */
        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }

        /**
         * Check if ad exists and can be shown.
         */
        private boolean isAdAvailable() {
            // Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
            // https://support.google.com/admob/answer/9341964?hl=en
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         */
        private void showAdIfAvailable(@NonNull final Activity activity) {

            SharedPreferences preferences = getSharedPreferences("PREFS", 0);
            boolean isPremium = preferences.getBoolean("isPremium", false);

            if (!isPremium) {
                showAdIfAvailable2(
                        activity,
                        () -> {
                            // Empty because the user will go back to the activity that shows the ad.
                        });
            }


        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity                 the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        private void showAdIfAvailable2(
                @NonNull final Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
                return;
            }

            Log.d(LOG_TAG, "Will show ad.");
            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        /** Called when full screen content is dismissed. */
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");

                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        /** Called when fullscreen content failed to show. */
                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());

                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        /** Called when fullscreen content is shown. */
                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
                        }
                    });


            isShowingAd = true;
            appOpenAd.show(activity);


        }
    }
}
