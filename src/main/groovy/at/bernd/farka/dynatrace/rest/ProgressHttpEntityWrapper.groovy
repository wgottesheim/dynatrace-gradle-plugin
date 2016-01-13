package at.bernd.farka.dynatrace.rest
import org.apache.http.HttpEntity
import org.apache.http.entity.HttpEntityWrapper

/**
 * Created by cwat-bfarka on 24.03.2015.
 */

public class ProgressHttpEntityWrapper extends HttpEntityWrapper {

    private final ProgressCallback progressCallback;

    public static interface ProgressCallback {

        public void progress(float progress);
    }

    public ProgressHttpEntityWrapper(final HttpEntity entity, final ProgressCallback progressCallback) {
        super(entity);
        this.progressCallback = progressCallback;
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
        this.wrappedEntity.writeTo(out instanceof ProgressFilterOutputStream ? out : new ProgressFilterOutputStream(out,
                progressCallback, getContentLength()));
    }

    static class ProgressFilterOutputStream extends FilterOutputStream {

        private final ProgressCallback progressCallback;
        private long transferred;
        private long totalBytes;

        ProgressFilterOutputStream(final OutputStream out, final ProgressCallback progressCallback, final long totalBytes) {
            super(out);
            this.progressCallback = progressCallback;
            this.transferred = 0;
            this.totalBytes = totalBytes;
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            out.write(b, off, len);
            transferred += len;
            progressCallback.progress(getCurrentProgress());
        }

        @Override
        public void write(final int b) throws IOException {
            out.write(b);
            transferred++;
            progressCallback.progress(getCurrentProgress());
        }

        private float getCurrentProgress() {
            return ((float) transferred / totalBytes) * 100;
        }
    }

}
