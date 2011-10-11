package jp.hutcraft.android.tsr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jp.hutcraft.android.tsr.util.ImageUtil;
import jp.hutcraft.tsr.service.Tweet;
import jp.hutcraft.tsr.service.TweetStreamService;
import jp.hutcraft.tsr.service.TweetStreamServiceFactory;
import jp.hutcraft.tsr.service.TweetStreamServiceFactory.LogAppender;
import jp.hutcraft.tsr.service.TweetStreamServiceFactory.TweetHandler;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetStreamActivity extends ListActivity {
	
	public static final String tag = "TweetStreamReader";
	private TweetStreamService service;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final Intent i = getIntent();
		final String user = i.getStringExtra("user");
		final String pass = i.getStringExtra("pass");
		final String keyword = i.getStringExtra("keyword");
		
		service = TweetStreamServiceFactory.create(new TweetHandler() {
			@Override public void notice(final Tweet tweet) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.d("tweetStreamReader", "arrived");
						list.add(0, tweet);
						while (list.size() > 100) {
							list.remove(list.size()-1);
						}
						onContentChanged();
					}
				});
			}
		}, user, pass, keyword, new LogAppender(){
			@Override public void notice(final String message) {
				Log.d(tag, message);
			}
			@Override public void notice(final Throwable e) {
				Log.d(tag, "", e);
			}});

		final ListAdapter adapter = new ListAdapter(getApplicationContext(), list);
		setListAdapter(adapter);
		
		startBackgroundThread();
	}
	
	private List<Tweet> list = new LinkedList<Tweet>();
	
	private static class ListAdapter extends ArrayAdapter<Tweet>{
		
		private LayoutInflater mInflater;
		private TextView mNameText;
		private ImageView mTweetIcon;
		private TextView mTweetText;
		private TextView mMetaText;

		public ListAdapter(Context context, List<Tweet> objects) {
			super(context, 0, objects);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

//		private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		private final DateFormat df = new SimpleDateFormat("HH:mm:ss");
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.row, null);
			}
			final Tweet t = this.getItem(position);
			if (t == null) return convertView;
			
			mTweetIcon = (ImageView)convertView.findViewById(R.id.tweetIcon);
			try {
				final Drawable drawable = ImageUtil.createByUrl(t.user.profile_image_url);
				mTweetIcon.setImageDrawable(drawable);
			} catch (final MalformedURLException e) {
				Log.e(tag, t.user.profile_image_url, e);
			} catch (final IOException e) {
				Log.e(tag, "", e);
			}
			mNameText = (TextView)convertView.findViewById(R.id.nameText);
			mNameText.setText(t.user.name +" / "+ t.user.screen_name);
			mTweetText = (TextView)convertView.findViewById(R.id.tweetText);
			mTweetText.setText(t.text);
			mMetaText = (TextView)convertView.findViewById(R.id.metaText);
			mMetaText.setText(df.format(t.created_at));
			return convertView;
		}
	}
	
	private final Handler handler = new Handler();

	private ScheduledExecutorService srv;
	private void startBackgroundThread() {
		srv = Executors.newSingleThreadScheduledExecutor();
		srv.schedule(new Runnable() {
			public void run() {
				logHandler.post(new Runnable(){
					@Override
					public void run() {
						statusMessage("start reading the stream");
					}});
				service.invoke();
				logHandler.post(new Runnable(){
					@Override
					public void run() {
						statusMessage("the read service had finished");
					}});
			}
		},  0, TimeUnit.MILLISECONDS);
	}
	
	final Handler logHandler = new Handler();
	private void statusMessage(final String message) {
		final TextView messageView = (TextView)findViewById(R.id.statusMessage);
		messageView.setText(message);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			service.finish();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}