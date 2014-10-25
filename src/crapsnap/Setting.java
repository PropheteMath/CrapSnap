package crapsnap;

import org.json.me.JSONException;
import org.json.me.JSONObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.text.TextFilter;

public class Setting extends MainScreen {

	JSONObject Current = new JSONObject();
	UiApplication monApp;
	private FixedTextUiElement UserName;
	private FixedTextUiElement PhoneNumber;
	private TextUiElement Email;
	private IntUiElement BestFriend;
	private BoolUiElement ReceiveFrom;
	private BoolUiElement StoryFrom;
	private BoolUiElement Mature;
	private FixedTextUiElement anniversaire;
	private ContextBoolUiElement SaveSnap;
	private ContextBoolUiElement Refresh;
	private TextField time;

	Setting(UiApplication App, JSONObject Params) {
		Current = Params;
		monApp = App;

		this.setTitle(" CrapSnap - Settings");

		try {

			UserName = new FixedTextUiElement("  Username : ",
					Current.getString("username"));
			this.add(UserName);
			if (!Current.getString("mobile").equals("")) {
				PhoneNumber = new FixedTextUiElement("  Phone number : ",
						Current.getString("mobile"));
				this.add(PhoneNumber);
			}

			Email = new TextUiElement("  Email : ", Current.getString("email"));
			this.add(Email);
			anniversaire = new FixedTextUiElement("  Birthday : ",
					Current.getString("birthday"));
			this.add(anniversaire);

			this.add(new SeparatorField());

			BestFriend = new IntUiElement(
					"  Bests Friends Number (3, 5 or 7) : ", Current
							.getJSONArray("bests").length());
			boolean Receivebool = false;
			if (Current.getInt("snap_p") == 0) {
				Receivebool = true;
			}
			ReceiveFrom = new BoolUiElement("  Receive from Everyone : ",
					Receivebool);
			this.add(ReceiveFrom);

			boolean Storybool = false;
			if (Current.getString("story_privacy").equals("EVERYONE")) {
				Storybool = true;
			}
			StoryFrom = new BoolUiElement("  Share Story with everyone : ",
					Storybool);
			this.add(StoryFrom);

			Mature = new BoolUiElement("  Mature Content : ",
					Current.getBoolean("can_view_mature_content"));
			this.add(Mature);

			this.add(new SeparatorField());

			try {
				if ((new NotifyContextStore().GetStoredValues()) != 0) {
					Refresh = new ContextBoolUiElement("  Auto-update : ",
							Boolean.TRUE.toString());
				} else {
					Refresh = new ContextBoolUiElement("  Auto-update : ",
							Boolean.FALSE.toString());
				}
				HorizontalFieldManager timemana = new HorizontalFieldManager();
				time = new TextField();
				time.setLabel("  Minutes between update : ");
				time.setText(Integer.toString(new NotifyContextStore()
								.GetStoredValues() / 60));
				time.setFilter(TextFilter.get(TextFilter.NUMERIC));
				timemana.add(time);
				timemana.add(new LabelField(" minutes."));
				this.add(Refresh);
				this.add(timemana);
			} catch (Exception e) {
				Refresh = new ContextBoolUiElement("  Auto-update : ", "false");
				HorizontalFieldManager timemana = new HorizontalFieldManager();
				time = new TextField();
				time.setLabel("  Minutes between update : ");
				time.setText(Integer.toString(300 / 60));
				time.setFilter(TextFilter.get(TextFilter.NUMERIC));
				timemana.add(time);
				timemana.add(new LabelField(" minutes."));
				this.add(Refresh);
				this.add(timemana);
			}
			time.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					try{
						if (Integer.parseInt(time.getText()) <= 0){
						time.setText("0");
						time.setEnabled(false);
						CheckboxField chbx = (CheckboxField) Refresh.getField(1);
						chbx.setChecked(false);
						}
					} catch (Exception e){
					}
				}
			});
			CheckboxField chbxtmp =  (CheckboxField) Refresh.getField(1);
			chbxtmp.setChangeListener(null);
			chbxtmp.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					Refresh.IsModif = true;
					if (((CheckboxField) field).getChecked() == false) {
						time.setEnabled(false);
						time.setText("0");
					} else {
						time.setEnabled(true);
						time.setText("5");
					}
				}
			});

			this.add(new SeparatorField());

			try {
				SaveSnap = new ContextBoolUiElement(
						"  Notify screenshot to sender : ",
						new ContextStore().GetStoredValues()[2]);
				this.add(SaveSnap);
			} catch (Exception e) {

			}

			this.add(new SeparatorField());

			this.add(new LabelField(""));
			this.add(new LabelField("  # Snap sent: "
					+ Current.getString("sent")));
			this.add(new LabelField("  # Snap Received: "
					+ Current.getString("received")));
			this.add(new LabelField("  Your SnapChat Score : "
					+ Current.getString("score")));
			this.add(new LabelField(""));

			this.add(new SeparatorField());

			VerticalFieldManager BtnManager = new VerticalFieldManager(
					Field.USE_ALL_WIDTH);
			SplitButon logout = new SplitButon("Log out", 1);
			logout.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					SnapChatApiFx api = new SnapChatApiFx();
					try {
						ContextStore context2 = new ContextStore();
						context2.StoreValues(new String[] { "", "", "true", "" });
						try {
							monApp.popScreen();
							monApp.popScreen();
							monApp.popScreen();
						} catch (Exception e) {

						}
						monApp.pushScreen(new Home(monApp, false));
						api.LogOut(Current.getString("username"),
								Current.getString("auth_token"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			SplitButon save = new SplitButon("Save", 1);
			save.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					Save();
				}
			});
			SplitButon clear = new SplitButon("Clear feed", 1);
			clear.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					Status.show("Clearing...");
					SnapChatApiFx api = new SnapChatApiFx();
					try {
						api.ClearFeed(Current.getString("username"),
								Current.getString("auth_token"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			BtnManager.add(logout);
			BtnManager.add(save);
			BtnManager.add(clear);
			this.add(BtnManager);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	void Save() {
		SnapChatApiFx api = new SnapChatApiFx();
		Status.show("Saving...");
		try {
			if (!SaveSnap.GetNewValue().equals(null)) {
				new ContextStore().StoreValues(new String[] {
						new ContextStore().GetStoredValues()[0],
						new ContextStore().GetStoredValues()[1],
						SaveSnap.GetNewValue().toString(),
						new ContextStore().GetStoredValues()[3] });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new NotifyContextStore().StoreValues(Integer.parseInt(time
					.getText()) * 60);
			if (Refresh.GetNewValue() == Boolean.TRUE) {
				if (Integer.parseInt(time.getText()) != 0) {
					new NotifyContextStore().StoreValues(Integer.parseInt(time
							.getText()) * 60);
				} else {
					new NotifyContextStore().StoreValues(5 * 60);
				}
			} else {
				new NotifyContextStore().StoreValues(0);
				time.setText(0 + "");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (!Email.GetNewValue().equals(null)) {
				api.UpDate(Current.getString("username"),
						Current.getString("auth_token"), "email",
						Email.GetNewValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (!BestFriend.GetNewValue().equals(null)) {
				if (Integer.parseInt(BestFriend.GetNewValue()) == 3
						|| Integer.parseInt(BestFriend.GetNewValue()) == 5
						|| Integer.parseInt(BestFriend.GetNewValue()) == 7) {
					api.SetBestFriendNumber(Current.getString("username"),
							Current.getString("auth_token"),
							BestFriend.GetNewValue());
				} else {
					Dialog.inform("Number of best friend can only be 3, 5 or 7");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (!ReceiveFrom.GetNewValue().equals(null)) {
				int value = 0;
				if (ReceiveFrom.GetNewValue().booleanValue() == true) {
					value = 0;
				} else {
					value = 1;
				}
				api.UpDate(Current.getString("username"),
						Current.getString("auth_token"), "SnapP",
						Integer.toString(value));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (!StoryFrom.GetNewValue().equals(null)) {
				String word = new String();
				if (StoryFrom.GetNewValue().booleanValue() == true) {
					word = "EVERYONE";
				} else {
					word = "FRIENDS";
				}
				api.UpDate(Current.getString("username"),
						Current.getString("auth_token"), "StoriesP", word);
			}
		} catch (Exception e) {
			// Dialog.inform(e.getMessage());
			e.printStackTrace();
		}
		try {
			if (!Mature.GetNewValue().equals(null)) {
				api.UpDate(Current.getString("username"), Current
						.getString("auth_token"), "Mature", Mature
						.GetNewValue().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		monApp.popScreen();
	}

	public class TextUiElement extends HorizontalFieldManager {
		private EditField field = new EditField();
		private boolean IsModif = false;

		TextUiElement(String Name, String Value) {
			this.add(new LabelField(Name));
			field.setText(Value);
			field.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					IsModif = true;
				}
			});
			this.add(field);
		}

		String GetNewValue() {
			if (IsModif == true) {
				return field.getText();
			} else {
				return null;
			}
		}
	}

	public class IntUiElement extends HorizontalFieldManager {
		private EditField field = new EditField();
		private boolean IsModif = false;

		IntUiElement(String Name, int Value) {
			this.add(new LabelField(Name));
			field.setText(Integer.toString(Value));
			field.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					try {
						IsModif = true;

					} catch (Exception e) {
						Dialog.inform("Number of best friend can only be 3, 5 or 7");
					}
				}
			});
			this.add(field);
		}

		String GetNewValue() {
			if (IsModif == true) {
				return field.getText();
			} else {
				return null;
			}
		}
	}

	public class FixedTextUiElement extends HorizontalFieldManager {
		private LabelField field = new LabelField();
		private boolean IsModif = false;

		FixedTextUiElement(String Name, String Value) {
			this.add(new LabelField(Name));
			field.setText(Value);
			this.add(field);
		}
	}

	public class BoolUiElement extends HorizontalFieldManager {
		private CheckboxField field = new CheckboxField();
		private boolean IsModif = false;

		BoolUiElement(String Name, boolean Value) {
			this.add(new LabelField(Name, Field.FIELD_VCENTER));
			field.setChecked(Value);
			field.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					IsModif = true;
				}
			});

			this.add(field);
		}

		Boolean GetNewValue() {
			if (IsModif == true) {
				if (field.getChecked() == true) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}

			} else {
				return null;
			}
		}
	}

	public class ContextBoolUiElement extends HorizontalFieldManager {
		private CheckboxField field = new CheckboxField();
		private boolean IsModif = false;

		ContextBoolUiElement(String Name, String Value) {
			this.add(new LabelField(Name, Field.FIELD_VCENTER));
			boolean bolValue;
			if (Value.equals("true")) {
				bolValue = true;
			} else {
				bolValue = false;
			}
			field.setChecked(bolValue);
			field.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					IsModif = true;
				}
			});

			this.add(field);
		}

		Boolean GetNewValue() {
			if (IsModif == true) {
				if (field.getChecked() == true) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}

			} else {
				return null;
			}
		}
	}

	protected boolean onSave(){
		Save();
		return true;
	}
}
