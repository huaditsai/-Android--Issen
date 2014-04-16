package huadi.issen.Drawer;

public class DrawerListItem
{
	public int Id;
	public int img_keywordImg;
	public String txt_showText;
	public String txt_keyword;
	
	public DrawerListItem(int id, int img_keywordImg, String txt_showText, String txt_keyword)
	{
		Id = id;
		this.img_keywordImg = img_keywordImg;
		this.txt_showText = txt_showText;
		this.txt_keyword = txt_keyword;
	}
}
