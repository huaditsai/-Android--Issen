package huadi.issen;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class GetPlaceTask extends AsyncTask<String, Integer, String> // <肚把计, 矪瞶い穝ざ把计, 矪瞶肚把计>
{
	private ProgressDialog dialog;

	Activity activity;
	Response response;
	
	public static ArrayList<Place> placeLists;

	public GetPlaceTask(Activity activity)
	{
		super();
		this.activity = activity;

		dialog = new ProgressDialog(activity);
	}

	@Override
	protected String doInBackground(String... keyword)
	{
		//Log.e("aaa", "Result: " + response.toString());
		
		String fqlQuery = "SELECT page_id, pic, name, display_subtext, latitude, longitude " 
			+ "FROM place " 
			+ "WHERE distance(latitude, longitude, \""+ keyword[1] + "\", \"" + keyword[2] +"\") < 500 "
			//+ "AND checkin_count > 50 "
			+ "AND CONTAINS(\"" + keyword[0] + "\") " ;
			//+ "order by checkin_count DESC " + "LIMIT 10";
		
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Session session = Session.getActiveSession();
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback()
		{
			public void onCompleted(Response response)
			{
				try
				{
					placeLists = new ArrayList<Place>();
					
					GraphObject graphObject = response.getGraphObject();

					JSONObject jsonObject = graphObject.getInnerJSONObject(); //{}JSONObject
					JSONArray dataJsonArray = jsonObject.getJSONArray("data"); //[]JSONArray

					for (int i = 0; i < dataJsonArray.length(); i++)
					{
						Place p = new Place();

						String page_id = dataJsonArray.getJSONObject(i).getString("page_id");
						Bitmap pic = GetImgFromUrl(dataJsonArray.getJSONObject(i).getString("pic"));
						String name = dataJsonArray.getJSONObject(i).getString("name");
						String display_subtext = dataJsonArray.getJSONObject(i).getString("display_subtext");
						String latitude = dataJsonArray.getJSONObject(i).getString("display_subtext");
						String longitude = dataJsonArray.getJSONObject(i).getString("display_subtext");

						p.page_id = page_id;
						p.name = name;
						p.pic = pic;
						p.display_subtext = display_subtext;
						p.latitude = latitude;
						p.longitude = longitude;

						placeLists.add(p);

						//Log.e("aaa", "p: " + p.name);
					}

					//					Collections.sort(list, new Comparator<PlaceList>() //逼尿
					//		{
					//			public int compare(PlaceList o1, PlaceList o2)
					//			{
					//				return Float.valueOf(o1.page_id).compareTo(Float.valueOf(o2.page_id));
					//			}
					//		});
				}
				catch (JSONException e)
				{
					Log.e("JSONException", "" + e);
				}
			}
		});
		Request.executeBatchAndWait(request);

		return null;
	}

	private Bitmap GetImgFromUrl(String imageFileURL)
	{
		Bitmap bitmap = null;
		try
		{
			URL url = new URL(imageFileURL);
			URLConnection conn = url.openConnection();

			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				InputStream inputStream = httpConn.getInputStream();

				bitmap = BitmapFactory.decodeStream(inputStream);
				inputStream.close();
			}
		}
		catch (MalformedURLException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return bitmap;
	}

	protected void ActionAlertDialog() //紆翴
	{
		//ArrayList<PP> list = initData();
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.place_listview, (ViewGroup) activity.findViewById(R.id.layout_myview));

		ListView myListView = (ListView) layout.findViewById(R.id.mylistview);
		PlaceListAdapter adapter = new PlaceListAdapter(activity, placeLists);
		myListView.setAdapter(adapter);

		DisplayMetrics dm = new DisplayMetrics(); // ミDisplayMetricsン
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm); // 眔杆竚戈癟
		int Width = dm.widthPixels;
		int Height = dm.heightPixels;
		LayoutParams lp = (LayoutParams) myListView.getLayoutParams();
		lp.width = (int) (Width * 0.85);
		lp.height = (int) (Height * 0.8);
		myListView.setLayoutParams(lp);

		myListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> a, View v, int which, long id)
			{				
				if (placeLists.get(which).isSelected)
				{
					placeLists.get(which).isSelected = false;
					placeLists.get(which).isToRandom = false;
					v.setBackgroundColor(android.graphics.Color.TRANSPARENT);
					
					PlaceList.selectedPlaceLists.remove(placeLists.get(which));
				}
				else
				{
					placeLists.get(which).isSelected = true;
					placeLists.get(which).isToRandom = true;
					v.setBackgroundColor(android.graphics.Color.parseColor(activity.getResources().getString(R.string.color_selected)));
					
					PlaceList.selectedPlaceLists.add(placeLists.get(which));
				}
			}
		});

		builder = new AlertDialog.Builder(activity);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{				
				dialog.dismiss();
				activity.startActivity(new Intent(activity, MainActivity.class));
				activity.finish();
			}
		});
		builder.setView(layout);
		builder.setCancelable(false);

		alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);

		ActionAlertDialog();

		if (dialog.isShowing())
			dialog.dismiss();
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();

		dialog.setMessage("Loading...");
		dialog.setCancelable(false);
		dialog.show();
	}

}
