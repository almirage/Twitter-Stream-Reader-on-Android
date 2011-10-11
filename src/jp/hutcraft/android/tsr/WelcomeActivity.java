package jp.hutcraft.android.tsr;

import java.io.Serializable;

import jp.hutcraft.android.tsr.util.FileUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WelcomeActivity extends Activity {

	private final Handler handler = new Handler();
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		final Form form = FileUtil.load(Config.formSaveName, this);
		setupDefaults(form);
		
		final Button button = (Button)findViewById(R.id.goToNext);
		button.setOnClickListener(new OnClickListener() {
			@Override public void onClick(final View v) {
				handler.post(new Runnable(){
					@Override public void run() {
						goToNext();
					}
				});
			}
		});
	}
	
	private void setupDefaults(final Form form) {
		if (form == null) return;
		((EditText)findViewById(R.id.editTextUserId)).setText(form.user);
		((EditText)findViewById(R.id.editTextPassword)).setText(form.pass);
		((EditText)findViewById(R.id.editTextKeyword)).setText(form.keyword);
	}

	private void goToNext() {
		final Intent i = new Intent(getApplicationContext(), TweetStreamActivity.class);
		final EditText userText = (EditText)findViewById(R.id.editTextUserId);
		i.putExtra("user", userText.getText().toString());
		final EditText passText = (EditText)findViewById(R.id.editTextPassword);
		i.putExtra("pass", passText.getText().toString());
		final EditText keywordText = (EditText)findViewById(R.id.editTextKeyword);
		i.putExtra("keyword", keywordText.getText().toString());
		
		final Form form = new Form(userText.getText().toString(), passText.getText().toString(), keywordText.getText().toString());
		FileUtil.save(Config.formSaveName, form, this);
		
		startActivity(i);
	}
	
	public static class Form implements Serializable {
		private static final long serialVersionUID = 1L;
		private final String user;
		private final String pass;
		private final String keyword;
		private Form(final String user, final String pass, final String keyword) {
			this.user = user;
			this.pass = pass;
			this.keyword = keyword;
		}
		public String getUser() {
			return user;
		}
		public String getPass() {
			return pass;
		}
		public String getKeyword() {
			return keyword;
		}
	}
}
