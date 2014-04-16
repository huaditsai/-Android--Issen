package huadi.issen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainActivity extends FragmentActivity
{
	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS + 1;

	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

	private boolean isResumed = false;

	private MenuItem settings;

	//	private MainFragment mainFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();
		fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
		fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++)
		{
			transaction.hide(fragments[i]);
		}
		transaction.commit();

		//				// Add code to print out the key hash
		//				try
		//				{
		//					PackageInfo info = getPackageManager().getPackageInfo("huadi.issen", PackageManager.GET_SIGNATURES);
		//					for (Signature signature : info.signatures)
		//					{
		//						MessageDigest md = MessageDigest.getInstance("SHA");
		//						md.update(signature.toByteArray());
		//						Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		//					}
		//				}
		//				catch (NameNotFoundException e)
		//				{
		//					Log.e("NameNotFoundException:", e.toString());
		//				}
		//				catch (NoSuchAlgorithmException e)
		//				{
		//					Log.e("NameNotFoundException:", e.toString());
		//				}

		//		if (savedInstanceState == null)
		//		{
		//			// Add the fragment on initial activity setup
		//			mainFragment = new MainFragment();
		//			getSupportFragmentManager().beginTransaction().add(android.R.id.content, mainFragment).commit();
		//		}
		//		else
		//		{
		//			// Or set the fragment from restored state info
		//			mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
		//		}
	}

	private void showFragment(int fragmentIndex, boolean addToBackStack)
	{
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++)
		{
			if (i == fragmentIndex)
			{
				transaction.show(fragments[i]);
			}
			else
			{
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack)
		{
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
		// Only make changes if the activity is visible
		if (isResumed)
		{
			FragmentManager manager = getSupportFragmentManager();
			// Get the number of entries in the back stack
			int backStackSize = manager.getBackStackEntryCount();
			// Clear the back stack
			for (int i = 0; i < backStackSize; i++)
			{
				manager.popBackStack();
			}
			if (state.isOpened())
			{
				// If the session state is open:
				// Show the authenticated fragment
				showFragment(SELECTION, false);
			}
			else if (state.isClosed())
			{
				// If the session state is closed:
				// Show the login fragment
				showFragment(SPLASH, false);
			}
		}
	}

	@Override
	protected void onResumeFragments()
	{
		super.onResumeFragments();
		Session session = Session.getActiveSession();

		if (session != null && session.isOpened())
		{
			// if the session is already open,
			// try to show the selection fragment
			showFragment(SELECTION, false);
		}
		else
		{
			// otherwise present the splash screen
			// and ask the person to login.
			showFragment(SPLASH, false);
		}
	}

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback()
	{
		@Override
		public void call(Session session, SessionState state, Exception exception)
		{
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// only add the menu when the selection fragment is showing
		if (fragments[SELECTION].isVisible())
		{
			if (menu.size() == 0)
			{
				settings = menu.add(R.string.settings);
			}
			return true;
		}
		else
		{
			menu.clear();
			settings = null;
		}
		return false;
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.equals(settings))
		{
			showFragment(SETTINGS, true);
			return true;
		}
		return false;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

}
