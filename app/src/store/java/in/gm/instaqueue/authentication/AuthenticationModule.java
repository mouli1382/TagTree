package in.gm.instaqueue.authentication;

import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.BuildConfig;
import in.gm.instaqueue.application.IQStoreApplication;
import io.fabric.sdk.android.Fabric;

@Module
public class AuthenticationModule {
    private FirebaseAuthenticationManager firebaseAuthenticationManager;

    @Provides @Named("digits")
    @Singleton
    public FirebaseAuthenticationManager provideDigitsAuthenticationManager(IQStoreApplication application) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(application, new TwitterCore(authConfig), new Digits.Builder().build());
        firebaseAuthenticationManager = new FirebaseAuthenticationManager();
        return firebaseAuthenticationManager;
    }

    @Provides @Named("gauth")
    @Singleton
    public FirebaseAuthenticationManager provideGoogleAuthenticationManager() {
        firebaseAuthenticationManager = new FirebaseAuthenticationManager();
        return firebaseAuthenticationManager;
    }
}