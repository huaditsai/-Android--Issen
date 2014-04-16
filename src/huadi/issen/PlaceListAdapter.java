package huadi.issen;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceListAdapter extends BaseAdapter
{
	Context context;
	ArrayList<Place> list;
	private LayoutInflater inflater;

	public PlaceListAdapter(Context context, ArrayList<Place> list)
	{
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Place getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Holder holder;

		if (convertView == null)
		{
			holder = new Holder();
			convertView = inflater.inflate(R.layout.place_listitem, null);
			holder.img_pic = (ImageView) convertView.findViewById(R.id.img_pic);
			holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
			holder.txt_display_subtext = (TextView) convertView.findViewById(R.id.txt_display_subtext);

			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}
		
		holder.img_pic.setImageBitmap(list.get(position).pic);
		holder.txt_name.setText(list.get(position).name);
		//holder.txt_title.getPaint().setFakeBoldText(true);		
		holder.txt_display_subtext.setText(list.get(position).display_subtext);
		
		if (!list.get(position).isSelected)
		{
			convertView.setBackgroundColor(android.graphics.Color.TRANSPARENT);			
		}
		else
		{
			convertView.setBackgroundColor(android.graphics.Color.parseColor(context.getResources().getString(R.string.color_selected)));
		}

		return convertView;
	}

	protected class Holder
	{
		ImageView img_pic;
		TextView txt_name;
		TextView txt_display_subtext;		
	}
}
