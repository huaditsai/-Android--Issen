package huadi.issen.Drawer;

import huadi.issen.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerItemAdapter extends ArrayAdapter<String>
{
	private final Context context;
	private final String[] Ids;
	private final int rowResourceId;

	public DrawerItemAdapter(Context context, int textViewResourceId, String[] objects)
	{
		super(context, textViewResourceId, objects);

		this.context = context;
		this.Ids = objects;
		this.rowResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(rowResourceId, parent, false);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img_drawer_item);
		TextView textView = (TextView) rowView.findViewById(R.id.text_drawer_item);

		int id = Integer.parseInt(Ids[position]);
		int icon = DrawerListMoedel.GetbyId(id).img_keywordImg;

		imageView.setBackgroundResource(icon);
		textView.setText(String.format("%s\n%s", DrawerListMoedel.GetbyId(id).txt_showText, DrawerListMoedel.GetbyId(id).txt_keyword));

		//		textView.setCompoundDrawablesWithIntrinsicBounds(icon, //left
		//			0, //top
		//			0, //right
		//			0);//bottom

		return rowView;

	}

}
