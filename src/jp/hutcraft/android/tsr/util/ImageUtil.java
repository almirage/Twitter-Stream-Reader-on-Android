package jp.hutcraft.android.tsr.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;

public class ImageUtil {
	private ImageUtil() {}
	
	public static Drawable createByUrl(final String url) throws MalformedURLException, IOException {
		final InputStream is = (InputStream) new URL(url).getContent();
		return Drawable.createFromStream(is, url);
	}
}
