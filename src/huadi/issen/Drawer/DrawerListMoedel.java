package huadi.issen.Drawer;

import huadi.issen.R;

import java.util.ArrayList;

public class DrawerListMoedel
{
	public static ArrayList<DrawerListItem> Items;

	public static void LoadModel()
	{
		Items = new ArrayList<DrawerListItem>();
		
		Items.add(new DrawerListItem(1, R.drawable.drawer_coffee, "咖啡店", "Coffee"));		
		Items.add(new DrawerListItem(2, R.drawable.drawer_reakfast, "早餐店", "Breakfast"));
		Items.add(new DrawerListItem(3, R.drawable.drawer_brunch, "早午餐", "Brunch"));
		Items.add(new DrawerListItem(4, R.drawable.drawer_italian, "義式料理", "Italian"));
		Items.add(new DrawerListItem(5, R.drawable.drawer_pizza, "比薩", "Pizza"));
		Items.add(new DrawerListItem(6, R.drawable.drawer_burger, "漢堡", "Burger"));
		Items.add(new DrawerListItem(7, R.drawable.drawer_food, "不分類", "Food"));
		Items.add(new DrawerListItem(8, R.drawable.drawer_setting, "設定", "LogOut"));
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
