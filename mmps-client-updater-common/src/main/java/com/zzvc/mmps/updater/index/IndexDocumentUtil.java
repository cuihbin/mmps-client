package com.zzvc.mmps.updater.index;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.xmlbeans.XmlException;

import com.zzvc.mmps.documents.UpdateDocument;

public class IndexDocumentUtil {
	public static UpdateDocument read(InputStream is) throws XmlException, IOException {
		return UpdateDocument.Factory.parse(new GZIPInputStream(is));
	}
	
	public static void write(UpdateDocument document, OutputStream os) throws IOException {
		GZIPOutputStream gzos = null;
		try {
			gzos = new GZIPOutputStream(os);
			document.save(gzos);
		} finally {
			if (gzos != null) {
				try {
					gzos.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
