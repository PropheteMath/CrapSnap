package crapsnap;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

public class Friends extends NoPromtMainScreenSimple {

	JSONObject _Current = new JSONObject();
	UiApplication _monApps;
	RichListField FriendLST;
	int AddFriendRow = -1;
	SplitButon AddButton = new SplitButon("Add new friend",1);
	VerticalFieldManager MainManager = new VerticalFieldManager();
	Bitmap margin = EncodedImage.getEncodedImageResource("empty_small.png")
			.getBitmap();
	
	Friends(UiApplication monApps, JSONObject Current) {
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		_monApps = monApps;
		_Current = Current;		
		this.setTitle(" CrapSnap - Manage Friends");
		Bitmap back = EncodedImage.getEncodedImageResource("Back.jpg").getBitmap();
		this.setBackground(BackgroundFactory.createBitmapBackground(back));
		populate();		
	}
	
	private void ShowScore(String Name)
	{
		SnapChatApiFx api = new SnapChatApiFx();
		try {
			JSONObject Score = api.GetScore(Name,_Current.getString("username"),_Current.getString("auth_token"));
			JSONObject Friend = Score.getJSONObject(Name);
			JSONArray FriendsArray = Friend.getJSONArray("best_friends");
			String BestString = new String();
			int i = 0;
			for ( i = 0; i < FriendsArray.length();i++)
			{
				BestString = BestString + " " +  FriendsArray.getString(i);
			}
			int score = Friend.getInt("score");
			Dialog.inform(Name + "'s bests friends :" + BestString + ". Score : " + score);
		} catch (JSONException e) {
			Dialog.inform("Unable to get Score.");
			e.printStackTrace();
		}
	}
	
	private void Delete(String Name, int row)
	{
		if (Dialog.ask(Dialog.D_OK_CANCEL, "Are you sure you want to delete a friend :/ ?", Dialog.CANCEL) == Dialog.OK)
		{
			SnapChatApiFx api = new SnapChatApiFx();
			try {
				api.ManageFriend(Name, _Current.getString("username"), 1,_Current.getString("auth_token"));
				if (api.IsSuccess == true){
				Dialog.inform("Done.");
				FriendLST.remove(row);
				}
				else {
					Dialog.inform("Error while deleting friend.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void Add()
	{
		BasicEditField inputField = new BasicEditField();
		Dialog d = new Dialog(Dialog.D_OK_CANCEL,
				"Enter new friend name", Dialog.OK, null,
				Dialog.DEFAULT_CLOSE);
		d.add(inputField);
		int i = d.doModal();
		if (i == Dialog.OK) {
			SnapChatApiFx api = new SnapChatApiFx();
			try {
				api.ManageFriend(inputField.getText(), _Current.getString("username"), 0,_Current.getString("auth_token"));
				if (api.IsSuccess == true){
					Dialog.inform("Done.");
					FriendLST.insert(0,new Object[] { margin, inputField.getText()});
				} else {
					Dialog.inform("Error while adding friend.");
				}
			} catch (Exception e) {
				Dialog.inform("Error while adding friend.");
				e.printStackTrace();
			}
		}
	}
	
	private void populate()
	{
		try {
			this.add(AddButton);
			FriendLST = new RichListField(MainManager, true, 1, 0, true);
			try {
				JSONArray friends = _Current.getJSONArray("friends");
				String[] names  = new String[friends.length()];
				for (int i = 0; i < friends.length(); i++) {
					JSONObject friend = friends.getJSONObject(i);
					names[i] = friend.getString("name");
				}
				Comparator strComparator = new Comparator() {
				    public int compare(Object o1, Object o2) {
				        return o1.toString().compareTo(o2.toString());
				    }
				};
				Arrays.sort(names, strComparator);
				
				for (int i = 0; i < names.length; i++) {
					FriendLST.add(new Object[] {margin, names[i] });
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			FriendLST.setCommand(new Command(new CommandHandler() {
				public void execute(ReadOnlyCommandMetadata metadata,
						Object context) {
					int row = FriendLST.getFocusRow();
					String Name = (String) FriendLST.get(row)[1];
					String view = new String("View best friends");
					String cancel = new String("Cancel");
					String delete = new String("Delete");
					int[] value = { 0, 1, 2 };
					Dialog dial = new Dialog(Name + " selected.", new Object[] {
							view, delete, cancel }, value, 0, Bitmap
							.getPredefinedBitmap(Bitmap.QUESTION),
							Dialog.FIELD_HCENTER);
					int result = dial.doModal();
					switch (result) {
					case 0:
						ShowScore(Name);
						break;
					case 1:
						Delete(Name, row);
						break;
					case 2: 
						//Cancel
						break;
					}
				}
			}));
			
			FieldChangeListener listener = new FieldChangeListener() {
		         public void fieldChanged(Field field, int context) {
		             Add();
		         }
		     };
			AddButton.setChangeListener(listener);
			this.add(MainManager);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
