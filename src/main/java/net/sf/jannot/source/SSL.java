package net.sf.jannot.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.logging.Level;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import tudelft.utilities.logging.Reporter;

public class SSL {
	private final Reporter log;
	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();
	private final static String certFile = "tmpcacert";

	public SSL(Reporter log) {
		this.log = log;
	}

	/**
	 * Checks certificate, prints warnings if something seems wrong
	 * 
	 * @param url the url to certify
	 */
	public void certify(URL url) {
		if (url.toString().startsWith("https://")) {
			log.log(Level.FINE, "Using SSL.");
			try {
				addCertificate(url);
				Properties systemProps = System.getProperties();
				systemProps.put("javax.net.ssl.trustStore", certFile);
				System.setProperties(systemProps);
			} catch (Exception e) {
				log.log(Level.WARNING,
						"Something went wrong while installing the certificate",
						e);
			}
		}
	}

	private void addCertificate(URL url)
			throws NoSuchAlgorithmException, CertificateException, IOException,
			KeyStoreException, KeyManagementException {
		String host = url.getHost();
		int port = 443;
		char[] passphrase = "changeit".toCharArray();

		File file = new File(certFile);
		if (file.isFile() == false) {
			char SEP = File.separatorChar;
			File dir = new File(System.getProperty("java.home") + SEP + "lib"
					+ SEP + "security");
			file = new File(dir, "jssecacerts");
			if (file.isFile() == false) {
				file = new File(dir, "cacerts");
			}
		}
		log.log(Level.FINE, "Loading KeyStore " + file + "...");
		InputStream in = new FileInputStream(file);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, passphrase);
		in.close();

		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf
				.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = context.getSocketFactory();

		log.log(Level.FINE,
				"Opening connection to " + host + ":" + port + "...");
		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		socket.setSoTimeout(10000);
		try {
			log.log(Level.FINE, "Checking for existing certificate...");
			socket.startHandshake();
			socket.close();
		} catch (SSLException e) {
			log.log(Level.INFO, "Certificate not yet there, installing it");
		}

		X509Certificate[] chain = tm.chain;
		if (chain == null) {
			log.log(Level.WARNING, "Could not obtain server certificate chain");
			return;
		}

		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			log.log(Level.FINE, " " + (i + 1) + " Subject "
					+ cert.getSubjectDN() + ".  Issuer  " + cert.getIssuerDN());
			sha1.update(cert.getEncoded());
			log.log(Level.FINE, "   sha1    " + toHexString(sha1.digest()));
			md5.update(cert.getEncoded());
			log.log(Level.FINE, host);
		}

		/* Store certificates */
		log.log(Level.INFO, "Storing certificates");
		int index = 0;
		for (X509Certificate cert : chain) {
			String alias = host + "-" + index++;
			ks.setCertificateEntry(alias, cert);

		}
		OutputStream out = new FileOutputStream(certFile);
		ks.store(out, passphrase);
		out.close();

	}

	private String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

}

class SavingTrustManager implements X509TrustManager {

	private final X509TrustManager tm;
	protected X509Certificate[] chain;

	SavingTrustManager(X509TrustManager tm) {
		this.tm = tm;
	}

	public X509Certificate[] getAcceptedIssuers() {
		throw new UnsupportedOperationException();
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		throw new UnsupportedOperationException();
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		this.chain = chain;
		tm.checkServerTrusted(chain, authType);
	}
}
