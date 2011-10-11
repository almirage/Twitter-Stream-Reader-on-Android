package jp.hutcraft.android.tsr.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import jp.hutcraft.android.tsr.Config;
import android.content.Context;
import android.util.Log;

public class FileUtil {
	public static void save(final String fileName, final Serializable o, final Context context) {
		try {
			final OutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			try {
				final ObjectOutputStream oos = new ObjectOutputStream(outputStream);
				try {
					oos.writeObject(o);
				} catch (final IOException e) {
					Log.e(Config.tag, "", e);
				} finally {
					try {
						oos.close();
					} catch (final IOException e) {
						Log.e(Config.tag, "", e);
					}
				}
			} catch (final IOException e) {
				Log.e(Config.tag, "", e);
			} finally {
				try {
					outputStream.close();
				} catch (final IOException e) {
					Log.e(Config.tag, "", e);
				}
			}
		} catch (final FileNotFoundException e) {
			Log.e(Config.tag, "", e);
		}
	}

	/**
	 * 
	 * @param <T>
	 * @param fileName
	 * @param context
	 * @return null if the object cant loaded
	 */
	public static <T> T load(final String fileName, final Context context) {
		try {
			final InputStream is = context.openFileInput(fileName);
			try {
				final ObjectInputStream ois = new ObjectInputStream(is);
				return uncheckedCast(ois.readObject());
			} catch (final StreamCorruptedException e) {
				Log.e(Config.tag, "", e);
			} catch (final IOException e) {
				Log.e(Config.tag, "", e);
			} catch (final ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (final IOException e) {
					Log.e(Config.tag, "", e);
				}
			}
		} catch (final FileNotFoundException e) {
			Log.e(Config.tag, "", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> T uncheckedCast(final Object o) {
		return (T)o;
	}
}
