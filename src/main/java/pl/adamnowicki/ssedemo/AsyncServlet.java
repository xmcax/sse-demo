package pl.adamnowicki.ssedemo;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@WebServlet(urlPatterns = {"/async"}, asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/event-stream");
        resp.setHeader("Cache-Control", "no-cache");

        AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(10_000L);

        asyncContext.start(new Worker(asyncContext));
    }

    private class Worker implements Runnable {
        private final AsyncContext asyncContext;
        private final CountDownLatch countDownLatch = new CountDownLatch(1);

        Worker(AsyncContext asyncContext) {
            this.asyncContext = asyncContext;
        }

        @Override
        public void run() {
            try {
                log.info("Worker started");

                asyncContext.addListener(new CustomListener(countDownLatch::countDown));
                final ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(new Job(asyncContext), 0, 1, SECONDS);
                countDownLatch.await();
                scheduledFuture.cancel(true);

                log.info("Worker completed - exiting");
            } catch (InterruptedException e) {
                log.error("Worker interrupted");
            }
        }
    }

    private class Job implements Runnable {
        private final AsyncContext asyncContext;

        Job(AsyncContext asyncContext) {
            this.asyncContext = asyncContext;
        }

        @Override
        public void run() {
            try {
                final ServletResponse response = asyncContext.getResponse();
                log.info("Job writing");

                response.getOutputStream().print(getMessage());
                response.flushBuffer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getMessage() {
            return new StringBuilder("data:")
                    .append(LocalDateTime.now())
                    .append("\n\n")
                    .toString();
        }
    }
}
