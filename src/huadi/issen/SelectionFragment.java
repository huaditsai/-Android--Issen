package huadi.issen;

import java.util.ArrayList;

import huadi.issen.Drawer.DrawerItemAdapter;
import huadi.issen.Drawer.DrawerListMoedel;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class SelectionFragment extends Fragment
{
	private LinearLayout profileLayout;
	private ProfilePictureView profilePictureView;
	private TextView userNameView;

	// drawer
	private DrawerLayout drawerLayout;
	private ListView drawerList;

	//selected item
	private ListView listView;

	//location
	private LocationClient locationClient;
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(1000) // 1 seconds
	.setFastestInterval(16) // 16ms = 60fps
	.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.selection, container, false);

		profileLayout = (LinearLayout) view.findViewById(R.id.layout_selection_profile);
		// Find the user's profile picture custom view
		profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
		profilePictureView.setCropped(true);
		// Find the user's name view
		userNameView = (TextView) view.findViewById(R.id.txt_selection_user_name);

		SetDrawer(view);

		// Find the list view
		listView = (ListView) view.findViewById(R.id.selection_list);
		SetSelectItemList();

		// Bottom Bar
		SetBottomBar(view);

		// Check for an open session
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened())
		{
			// Get the user's data
			makeMeRequest(session);
		}

		return view;
	}

	void SetSelectItemList()
	{
		// Set up the list view items

		PlaceListAdapter adapter = new PlaceListAdapter(getActivity(), PlaceList.selectedPlaceLists);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> a, View v, int which, long id)
			{
				if (PlaceList.selectedPlaceLists.get(which).isToRandom)
				{
					PlaceList.selectedPlaceLists.get(which).isToRandom = false;
					v.setBackgroundColor(android.graphics.Color.TRANSPARENT);
				}
				else
				{
					PlaceList.selectedPlaceLists.get(which).isToRandom = true;
					v.setBackgroundColor(android.graphics.Color.parseColor(getActivity().getResources().getString(R.string.color_selected)));
				}
			}
		});
	}

	private void SetBottomBar(View view)
	{
		final TextView text_random = (TextView) view.findViewById(R.id.text_random);
		final TextView text_clear = (TextView) view.findViewById(R.id.text_clear);

		text_random.setText("Issen");
		text_random.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					text_random.setBackgroundColor(android.graphics.Color.parseColor("#51c2e5"));
				return false;
			}
		});
		text_random.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				text_random.setBackgroundColor(android.graphics.Color.TRANSPARENT);

				if (PlaceList.selectedPlaceLists.size() > 0)
				{
					ArrayList<Place> selectedPlaceLists = new ArrayList<Place>();
					for (Place place : PlaceList.selectedPlaceLists)
					{
						if (place.isToRandom)
							selectedPlaceLists.add(place);
					}
					if (selectedPlaceLists.size() > 0)
					{
						int randomNum = (int) (Math.random() * selectedPlaceLists.size());
						//Log.e("randomNum", "" + randomNum);
						ShowInfo("結果為", selectedPlaceLists.get(randomNum).name, false);
					}
					else
					{
						ShowInfo("錯誤", "請先選擇店家", false);
					}
				}
				else
				{
					ShowInfo("錯誤", "請先選擇店家", true);
				}
			}
		});

		text_clear.setText("Clear");
		text_clear.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					text_clear.setBackgroundColor(android.graphics.Color.parseColor("#51c2e5"));
				return false;
			}
		});
		text_clear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				text_clear.setBackgroundColor(android.graphics.Color.TRANSPARENT);
				
				PlaceList.selectedPlaceLists.clear();
				PlaceListAdapter sAdapter = (PlaceListAdapter) listView.getAdapter();
				sAdapter.notifyDataSetChanged();
				//				getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
				//				getActivity().finish();
			}
		});
	}

	private void ShowInfo(String title, String message, final boolean isToOpenDrawer)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.dismiss();
				if (isToOpenDrawer)
					drawerLayout.openDrawer(Gravity.LEFT);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback()
	{
		@Override
		public void call(final Session session, final SessionState state, final Exception exception)
		{
			onSessionStateChange(session, state, exception);
		}
	};

	private void makeMeRequest(final Session session) //me
	{
		// Make an API call to get user data and define a 
		// new callback to handle the response.
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback()
		{
			@Override
			public void onCompleted(GraphUser user, Response response)
			{
				// If the response is successful
				if (session == Session.getActiveSession())
				{
					if (user != null)
					{
						// Set the id for the ProfilePictureView
						// view that in turn displays the profile picture.
						profilePictureView.setProfileId(user.getId());
						// Set the Textview's text to the user's name.
						userNameView.setText(user.getName());
					}
				}
				if (response.getError() != null)
				{
					// Handle errors, will do so later.
				}
			}
		});
		request.executeAsync();
	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception)
	{
		if (session != null && session.isOpened())
		{
			// Get the user's data.
			makeMeRequest(session);
		}
	}

	private static final int REAUTH_ACTIVITY_CODE = 100;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE)
		{
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
		else if (resultCode == Activity.RESULT_OK)
		{
			// Do nothing for now
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		setUpLocationClientIfNeeded();
		locationClient.connect();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle)
	{
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (locationClient != null)
		{
			locationClient.disconnect();
		}
		uiHelper.onPause();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private void SearchingPlaceFQL(String keyword)
	{
		if (locationClient.getLastLocation() != null && locationClient.isConnected())
			new GetPlaceTask(getActivity()).execute(keyword, "" + locationClient.getLastLocation().getLatitude(), "" + locationClient.getLastLocation().getLongitude());
	}

	private void setUpLocationClientIfNeeded()
	{
		if (locationClient == null)
		{
			locationClient = new LocationClient(getActivity(), new ConnectionCallbacks()
			{
				@Override
				public void onDisconnected()
				{
				}

				@Override
				public void onConnected(Bundle arg0)
				{
					locationClient.requestLocationUpdates(REQUEST, new LocationListener()
					{
						@Override
						public void onLocationChanged(Location location)
						{
							//Log.e("location", "" + location);
						}
					});
				}
			}, new OnConnectionFailedListener()
			{
				@Override
				public void onConnectionFailed(ConnectionResult arg0)
				{
				}
			});
		}
	}

	public void SetDrawer(View view)
	{
		drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
		drawerList = (ListView) view.findViewById(R.id.left_drawer);

		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		DrawerListMoedel.LoadModel();
		String[] ids = new String[DrawerListMoedel.Items.size()];
		for (int i = 0; i < ids.length; i++)
			ids[i] = Integer.toString(i + 1);
		DrawerItemAdapter adapter = new DrawerItemAdapter(getActivity(), R.layout.drawer_list_item, ids);
		drawerList.setAdapter(adapter);

		drawerList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				switch (position)
				{
					case 7: //LogOut Facebook
						FragmentManager fm = getActivity().getSupportFragmentManager();
						FragmentTransaction transaction = fm.beginTransaction();
						transaction.hide(fm.findFragmentById(R.id.splashFragment));
						transaction.hide(fm.findFragmentById(R.id.selectionFragment));
						transaction.show(fm.findFragmentById(R.id.userSettingsFragment));
						transaction.addToBackStack(null);
						transaction.commit();
						break;
					default:
						SearchingPlaceFQL(DrawerListMoedel.Items.get(position).txt_keyword);
						break;
				}
				drawerLayout.closeDrawer(drawerList);
			}
		});

		//getActivity().getActionBar().hide();

		profileLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				drawerLayout.openDrawer(Gravity.LEFT);
			}
		});
	}

}
