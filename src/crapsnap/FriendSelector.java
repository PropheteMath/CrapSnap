package crapsnap;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

public class FriendSelector extends NoPromtMainScreenSimple {

	UiApplication monApp;

	private boolean exitbol = true;
	private boolean storybol = false;

	VerticalFieldManager ListManager = new VerticalFieldManager();
	VerticalFieldManager BtnManager = new VerticalFieldManager();
	VerticalFieldManager MainManager = new VerticalFieldManager();

	CheckboxField CheckBox = new CheckboxField("Add to story", false);

	SplitButon Send = new SplitButon("Send !", 1);

	FriendSelector(JSONObject Current, UiApplication app) {
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		this.setTitle(" CrapSnap - Select Receiver...");
		Bitmap back = EncodedImage.getEncodedImageResource("Back.jpg")
				.getBitmap();
		this.setBackground(BackgroundFactory.createBitmapBackground(back));
		monApp = app;
		Send.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				exitbol = false;
				if (CheckBox.getChecked() == true) {
					storybol = true;
				}
				quit();
			}
		});
		
		String[] names;
		try {
			JSONArray friends = Current.getJSONArray("friends");
			names  = new String[friends.length()];
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
				ListManager.add(new FriendSelectorObject(names[i]));
				ListManager.add(new SeparatorField());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.addMenuItem(AddMenuItem);
		this.addMenuItem(CancelMenuItem);
		BtnManager.add(Send);
		MainManager.add(ListManager);
		HorizontalFieldManager StoryManager = new  HorizontalFieldManager();
		CheckBox.setFont(CheckBox.getFont().derive(Font.BOLD, 9, Ui.UNITS_pt));
		StoryManager.add(new LabelField("  "));
		StoryManager.add(CheckBox);
		MainManager.add(StoryManager);
		MainManager.add(BtnManager);
		this.add(MainManager);
	}
	
	private MenuItem AddMenuItem = new MenuItem("Add another receiver...", 110, 10) {
		public void run() {
			BasicEditField inputField = new BasicEditField();
			Dialog d = new Dialog(Dialog.D_OK_CANCEL,
					"Enter Reicever Name", Dialog.OK, null,
					Dialog.DEFAULT_CLOSE);
			d.add(inputField);
			int i = d.doModal();
			if (i == Dialog.OK) {
				ListManager.add(new FriendSelectorObject(inputField
						.getText()));
				ListManager.add(new SeparatorField());
			}
		}
	};
	

	
	private MenuItem CancelMenuItem = new MenuItem("Cancel", 110, 10) {
		public void run() {
			exitbol = true;
			quit();
		}
	};

	private void quit() {
		if (monApp.getActiveScreen() == this) {
			monApp.popScreen(this);
		}
	}

	public boolean IsCanceled() {
		try {
			return exitbol;
		} catch (Exception e) {
			return false;
		}
	}

	String caption = new String();

	public boolean IsStoryed() {
		return storybol;
	}

	int nb = 0;

	public String GetNames() {
		String lst = new String();
		for (int i = 0; i < ListManager.getFieldCount(); i = i + 2) {
			FriendSelectorObject CurrentTest = (FriendSelectorObject) ListManager
					.getField(i);
			if (CurrentTest.IsSelected() == true) {
				nb++;
				if (lst.equals("")) {
					lst = CurrentTest.GetName();
				} else {
					lst = lst + "," + (String) CurrentTest.GetName();
				}

			}
		}
		return lst;
	}

	public int Getnb() {
		return nb;
	}
}
