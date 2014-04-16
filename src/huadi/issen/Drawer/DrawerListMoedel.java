package huadi.issen.Drawer;

import huadi.issen.R;

import java.util.ArrayList;

public class DrawerListMoedel
{
	public static ArrayList<DrawerListItem> Items;

	public static void LoadModel()
	{
		Items = new ArrayList<DrawerListItem>();
		
		Items.add(new DrawerListItem(1, R.drawable.drawer_coffee, "�@�ة�", "Coffee"));		
		Items.add(new DrawerListItem(2, R.drawable.drawer_reakfast, "���\��", "Breakfast"));
		Items.add(new DrawerListItem(3, R.drawable.drawer_brunch, "�����\", "Brunch"));
		Items.add(new DrawerListItem(4, R.drawable.drawer_italian, "�q���Ʋz", "Italian"));
		Items.add(new DrawerListItem(5, R.drawable.drawer_pizza, "����", "Pizza"));
		Items.add(new DrawerListItem(6, R.drawable.drawer_burger, "�~��", "Burger"));
		Items.add(new DrawerListItem(7, R.drawable.drawer_food, "������", "Food"));
		Items.add(new DrawerListItem(8, R.drawable.drawer_setting, "�]�w", "LogOut"));
	}

	public static DrawerListItem GetbyId(int id)
	{
		for (DrawerListItem item : Items)
		{
			if (item.Id == id)
			{
				return item;
			}
		}
		return null;
	}
}
