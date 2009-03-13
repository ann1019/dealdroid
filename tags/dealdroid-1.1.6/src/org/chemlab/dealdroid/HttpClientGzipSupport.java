package org.chemlab.dealdroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

/**
 * @author shade
 * @version $Id$
 */
public class HttpClientGzipSupport {

	/**
	 * Enables GZIP compression on the HttpClient.
	 * 
	 * @param httpClient
	 */
	public static void enableCompression(final DefaultHttpClient httpClient) {
		httpClient.addRequestInterceptor(new GzipRequestInterceptor());
		httpClient.addResponseInterceptor(new GzipResponseInterceptor());
	}

	static class GzipDecompressingEntity extends HttpEntityWrapper {

		public GzipDecompressingEntity(final HttpEntity entity) {
			super(entity);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.http.entity.HttpEntityWrapper#getContent()
		 */
		@Override
		public InputStream getContent() throws IOException, IllegalStateException {

			// the wrapped entity's getContent() decides about repeatability
			final InputStream wrappedin = wrappedEntity.getContent();
			return new GZIPInputStream(wrappedin);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.http.entity.HttpEntityWrapper#getContentLength()
		 */
		@Override
		public long getContentLength() {
			// length of ungzipped content is not known
			return -1;
		}

	}

	static class GzipRequestInterceptor implements HttpRequestInterceptor {

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.apache.http.HttpRequestInterceptor#process(org.apache.http.
		 * HttpRequest, org.apache.http.protocol.HttpContext)
		 */
		@Override
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			if (!request.containsHeader("Accept-Encoding")) {
				request.addHeader("Accept-Encoding", "gzip");
			}
		}
	}

	static class GzipResponseInterceptor implements HttpResponseInterceptor {

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.apache.http.HttpResponseInterceptor#process(org.apache.http.
		 * HttpResponse, org.apache.http.protocol.HttpContext)
		 */
		@Override
		public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header ceheader = entity.getContentEncoding();
				if (ceheader != null) {
					HeaderElement[] codecs = ceheader.getElements();
					for (int i = 0; i < codecs.length; i++) {
						if (codecs[i].getName().equalsIgnoreCase("gzip")) {
							response.setEntity(new GzipDecompressingEntity(response.getEntity()));
							return;
						}
					}
				}
			}
		}
	}
}
