package pl.adamnowicki.ssedemo;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

@Slf4j
public class CustomListener implements AsyncListener {

    private Runnable onCompleteCallback;

    public CustomListener(Runnable onCompleteCallback) {
        this.onCompleteCallback = onCompleteCallback;
    }

    @Override
    public void onComplete(AsyncEvent event) {
        onCompleteCallback.run();
        log.info("Async completed");
    }

    @Override
    public void onTimeout(AsyncEvent event) {
        log.info("Async timeout");
    }

    @Override
    public void onError(AsyncEvent event) {
        log.info("Async error");
    }

    @Override
    public void onStartAsync(AsyncEvent event) {
        log.info("Async started");
    }
}
