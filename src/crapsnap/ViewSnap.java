package crapsnap;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ImageManipulator.ImageManipulator;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class ViewSnap extends MainScreen {

	JSONObject Current;
	VerticalFieldManager MainManager = new VerticalFieldManager();

	boolean[] DoNotLoadSnapAnymore;

	int offsetint = 0;

	private UiApplication monApp;

	HorizontalFieldManager SnapManager = new HorizontalFieldManager();
	RichListField Snaplist;
	SnapChatApiFx API = new SnapChatApiFx();
	Cryptography crypto = new Cryptography();
	String currentcontact = new String();
	String _pwd = new String();
	String _user = new String();
	long lastst = 0;

	private int nonviewed = 0;

	BitmapField bmp = new BitmapField();

	// bitmaps des icones.
	Bitmap errorimg = EncodedImage.getEncodedImageResource("icon.png")
			.getBitmap();
	Bitmap _0img = EncodedImage.getEncodedImageResource("sended.png")
			.getBitmap();
	Bitmap _1img = EncodedImage.getEncodedImageResource("nonopenned.png")
			.getBitmap();
	Bitmap _11img = EncodedImage.getEncodedImageResource("vnonopenned.png")
			.getBitmap();
	Bitmap _2img = EncodedImage.getEncodedImageResource("openned.png")
			.getBitmap();
	Bitmap _3img = EncodedImage.getEncodedImageResource("screenshoot.png")
			.getBitmap();
	Bitmap _4img = EncodedImage.getEncodedImageResource("newfriend.png")
			.getBitmap();
	Bitmap _5img = EncodedImage.getEncodedImageResource("update.png")
			.getBitmap();
	Bitmap _6img = EncodedImage.getEncodedImageResource("Settings.png")
			.getBitmap();
	Bitmap _7img = EncodedImage.getEncodedImageResource("icon_friend.png")
			.getBitmap();
	Bitmap _8img = EncodedImage.getEncodedImageResource("Send mail.png")
			.getBitmap();
	Bitmap _9img = EncodedImage.getEncodedImageResource("play.png").getBitmap();
	Bitmap _20img = EncodedImage.getEncodedImageResource("stories.png")
			.getBitmap();

	private Notify notification = new Notify(0);
	Setting set;
	Friends FriendView;

	public boolean unable = false;
	public Vector BinarySnaps = new Vector();
	private Timer t;
	NotifyContextStore notifycontext = new NotifyContextStore();
	private long time = 1000 * notifycontext.GetStoredValues();

	public ViewSnap(String PWD, String USR, UiApplication app, String jsonstr) {

		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		this.setTitle(" CrapSnap");
		Bitmap back = EncodedImage.getEncodedImageResource("snapBack.png")
				.getBitmap();
		this.setBackground(BackgroundFactory.createBitmapBackground(back));
		_pwd = PWD;
		_user = USR;
		monApp = app;
		// Affichage de la liste des snaps

		try {
			if (jsonstr != "") {
				Current = new JSONObject(jsonstr);
			} else {
				Current = API.login(_user, _pwd);
				ContextStore storage = new ContextStore();
				try {
					storage.StoreValues(new String[] {
							storage.GetStoredValues()[0],
							storage.GetStoredValues()[1],
							storage.GetStoredValues()[2], Current.toString() });
				} catch (Exception e) {
				}
				offsetint = 0;
			}
			MainManager.deleteAll();
			JSONArray snaps = new JSONArray();
			try {
				snaps = Current.getJSONArray("snaps");
			} catch (Exception e) {
				unable = true;
				return;
			}
			set = new Setting(monApp, Current);
			FriendView = new Friends(monApp, Current);
			PopulateThread pt = new PopulateThread(snaps);
			pt.run();
			this.addMenuItem(menuitem);
			this.add(MainManager);
			this.addMenuItem(Help);
			if (time != 0) {
				t = new Timer();
				t.scheduleAtFixedRate(new Chronometer(), time, time);
			}
		} catch (Exception e) {
			unable = true;
		}
	}

	private boolean isupdating = false;
	private int OldNonviewed = 0;
	
	private MenuItem Help = new MenuItem("Need Help ?", 110, 10) {
		public void run() {
			Dialog dial = new Dialog(Dialog.OK, "Looking for help ? Want to give us your feedback ?", Dialog.OK, Bitmap.getPredefinedBitmap(Bitmap.QUESTION), 0);
			 ActiveRichTextField txtact = new  ActiveRichTextField("Do not hesitate to contact us at : crap.snap@laposte.net", Field.FIELD_HCENTER);
			 dial.add(txtact);
			 dial.show();
		}
	};

	private void load() {
		if (isupdating == true)
			return;
		isupdating = true;
		try {
			Snaplist.remove(0);
			Snaplist.insert(0,
					new Object[] { _5img, "UpDate ! ", "Updating..." });

			nonviewed = 0;
			OldNonviewed = nonviewed;
			PopulateThread pt = new PopulateThread(null);
			pt.start();
		} catch (Exception e) {
			Snaplist.remove(0);
			Snaplist.insert(0, new Object[] { _5img, "UpDate ! ",
					"Error occured, tap to retry." });
			notification.update(nonviewed);
		}
		isupdating = false;
	}

	boolean scheduled = false;

	private class Chronometer extends TimerTask {

		Chronometer() {

		}

		public void run() {
			synchronized (UiApplication.getEventLock()) {
				load();
			}
		}
	}

	int Vectoroffset = 0;
	private boolean firstlaunch = true;

	private class PopulateThread extends Thread {

		JSONArray snaps;

		PopulateThread(JSONArray _snaps) {
			snaps = _snaps;
		}

		public void run() {
			try {
				offsetint = 0;
				if (snaps == null) {
					Current = API.login(_user, _pwd);
					offsetint = 0;
					snaps = new JSONArray();
					snaps = Current.getJSONArray("snaps");
				}
				set = new Setting(monApp, Current);
				FriendView = new Friends(monApp, Current);

				for (int i = 0; i < snaps.length(); i++) {
					try {
						JSONObject snap = new JSONObject();
						snap = snaps.getJSONObject(i);
						String sn = snap.getString("sn");
						int st = snap.getInt("st");
						if (st == 1) {
//							nonviewed++;
						}
					} catch (Exception e) {
					}
				}
				
				boolean downagain = true;

				

				// LISTVIEW UPDATE

				RichListField Snaplist2 = new RichListField(true, 2, 0, false);
				Snaplist2.add(new Object[] { _5img, "UpDate !", "" });
				Snaplist2.add(new Object[] { _8img, "Send a Snap", "" });
//				Snaplist2.add(new Object[] { _9img, "Send a Video Snap", "" });
				Snaplist2.add(new Object[] { _7img, "Friends", "" });
				Snaplist2.add(new Object[] { _20img, "Stories", "" });
				Snaplist2.add(new Object[] { _6img, "Parameters", "" });
				Snaplist2.setFocusPolicy(TableController.ROW_FOCUS);
				setsnaplistcmd(Snaplist2);
				for (int i = 0; i < snaps.length(); i++) {
					JSONObject snap = new JSONObject();
					snap = snaps.getJSONObject(i);
					Bitmap img;
					int snaptime = snap.getInt("ts");
					int currentime = Current.getInt("current_timestamp");
					int timedif = ((currentime - snaptime) / 1000);
					String timeString = new String();
					if (timedif < 60) {
						timeString = timedif + " seconds";
					} else if (timedif < 3600) {
						if (((timedif - (timedif % 60)) / 60) == 1) {
							timeString = ((timedif - (timedif % 60)) / 60)
									+ " minute ago";
						} else {
							timeString = ((timedif - (timedif % 60)) / 60)
									+ " minutes ago";
						}
					} else if (timedif < 86400) {
						if (((timedif - (timedif % 3600)) / 3600) == 1) {
							timeString = ((timedif - (timedif % 3600)) / 3600)
									+ " hour ago";
						} else {
							timeString = ((timedif - (timedif % 3600)) / 3600)
									+ " hours ago";
						}
					} else {
						if (((timedif - (timedif % 86400)) / 86400) == 1) {
							timeString = ((timedif - (timedif % 86400)) / 86400)
									+ " day ago";
						} else {
							timeString = ((timedif - (timedif % 86400)) / 86400)
									+ " days ago";
						}
					}
					int st = -1;
					String txt = new String();
					String SubTxt = new String();
					String sn = null;
					st = snap.getInt("st");
					Thread.sleep(5);
					try {
						sn = snap.getString("sn");
						switch (st) {
						case 1:
							int m = snap.getInt("m");
							if (m == 3) {
								img = _4img;
								txt = sn;
								SubTxt = timeString + " - Added YOU !";
							} else {
								if (snap.getInt("m") == 1
										|| snap.getInt("m") == 2) {
									img = _11img;
								} else {
									img = _1img;
								}
								txt = sn;
								nonviewed++;
								SubTxt = timeString + " - tap to view";
							}

							break;
						case 2:
							img = _2img;
							txt = sn;
							SubTxt = timeString + " - tap to reply";
							break;
						case 3:
							img = _3img;
							txt = sn;
							SubTxt = timeString + " - ScreenShoted !";
							break;
						default:
							img = errorimg;
							txt = sn;
							SubTxt = "ERROR";
						}
						Thread.sleep(5);
						Snaplist2.add(new Object[] { img, txt, SubTxt });
					} catch (Exception e) {
						try {
							sn = snap.getString("rp");
							switch (st) {
							case 0:
								img = _0img;
								txt = sn;
								SubTxt = timeString + " - Sent";
								break;
							case 1:
								img = _0img;
								txt = sn;
								SubTxt = timeString + " - Delivered";
								break;
							case 2:
								img = _0img;
								txt = sn;
								SubTxt = timeString + " - Opened";
								break;
							case 3:
								img = _3img;
								txt = sn;
								SubTxt = timeString + " - ScreenShot !";
								break;
							default:
								img = errorimg;
								txt = sn;
								SubTxt = "ERROR";
							}
							Thread.sleep(5);
							Snaplist2.add(new Object[] { img, txt, SubTxt });
						} catch (Exception ee) {

						}
						Thread.sleep(5);
					}
					// FIN
				}
				
				int j = 0;
				// Update vector only if there is new snap to load
				if (nonviewed != OldNonviewed || firstlaunch == true) {
					firstlaunch = false;
					// VectorUpdate;
					Vector BinarySnaps2 = new Vector();
					for ( j = 0; j < snaps.length(); j++) {
						//JSONObject snap = new JSONObject();
						JSONObject snap2 = snaps.getJSONObject(j);
						int st2 = snap2.getInt("st");
						try {
							String sn2 = snap2.getString("sn");
							switch (st2) {
							case 1:
								int m = snap2.getInt("m");
								if (m == 3) {
									BinarySnaps2.addElement(new byte[] { 1, 1,
											1 });
								} else {
									byte[] snapdata;
									try {
										snapdata = API
												.GetSnap(
														Current.getString("username"),
														snap2.getString("id"),
														Current.getString("auth_token"));
										BinarySnaps2.addElement(snapdata);
									} catch (Exception e) {
										BinarySnaps2.addElement(new byte[] { 1,
												1, 1 });
									}
								}
								break;
							case 2:
								BinarySnaps2.addElement(new byte[] { 1, 1, 1 });
								break;
							case 3:
								BinarySnaps2.addElement(new byte[] { 1, 1, 1 });
								break;
							default:
								BinarySnaps2.addElement(new byte[] { 1, 1, 1 });
							}
						} catch (Exception e) {
							try {
								BinarySnaps2.addElement(new byte[] { 1, 1, 1 });
							} catch (Exception ee) {

							}

						}
					}
					BinarySnaps = BinarySnaps2;
					DoNotLoadSnapAnymore = new boolean[ j + 10];
				}
				// FIN
				
				synchronized (Application.getEventLock()) {
					MainManager.deleteAll();
					Thread.sleep(5);
					Snaplist = Snaplist2;
					Thread.sleep(5);
					MainManager.add(Snaplist);
					Thread.sleep(5);
					Vectoroffset = 0;
					notification = new Notify(nonviewed);
					notification.update(nonviewed);
				}
			} catch (Exception e) {
				Snaplist.remove(0);
				Snaplist.insert(0, new Object[] { _5img, "UpDate ! ",
						"Error occured, tap to retry." });
				e.printStackTrace();
			}
		}
	}

	protected ViewSnap getthis() {
		return this;
	}

	public void close() {
		UiApplication.getUiApplication().requestBackground();
	}

	private MenuItem menuitem = new MenuItem("Quit", 110, 10) {
		public void run() {
			notification.delete();
			System.exit(0);
		}
	};

	private class ThreadSendNotification extends Thread {

		private boolean _isscrshtd;
		private String _ID;

		ThreadSendNotification(String ID, boolean isscrshtd) {
			_ID = ID;
			_isscrshtd = isscrshtd;
		}

		public void run() {
			try {
				Thread.sleep(10);
				API.NotifySnap(Current.getString("username"), _isscrshtd,
						Current.getString("added_friends_timestamp"), _ID,
						Current.getString("auth_token"));
			} catch (JSONException e) {
				e.printStackTrace();
				Dialog.inform("Unable to notify this snap :/");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private boolean isstoryloaded = false;
	private boolean isstoryloading = false;;
	ViewStory story;

	private class GetStory extends Thread {

		GetStory() {
		}

		public void run() {
			if (isstoryloading == false) {
				isstoryloading = true;
				synchronized (Application.getEventLock()) {
					Snaplist.remove(3);
					Snaplist.insert(3, new Object[] { _20img, "Stories",
							"Loading..." });
				}
				try {
					story = new ViewStory(Current, monApp);
					synchronized (Application.getEventLock()) {
						isstoryloaded = true;
						Snaplist.remove(3);
						Snaplist.insert(3, new Object[] { _20img, "Stories",
								"Loaded. Tap to view !" });
					}
				} catch (Exception e) {
					synchronized (Application.getEventLock()) {
						isstoryloaded = false;
						Snaplist.remove(3);
						Snaplist.insert(3, new Object[] { _20img, "Stories",
								"Error occured. Please retry later." });
					}
				}
				isstoryloading = false;
			}
		}
	}

	private RichListField setsnaplistcmd(RichListField field) {
		field.setCommand(new Command(new CommandHandler() {
			public void execute(ReadOnlyCommandMetadata metadata, Object context) {
				int row = Snaplist.getFocusRow();
				boolean testbool;
				try {
					testbool = DoNotLoadSnapAnymore[row - offsetint];
				} catch (Exception e){
					 testbool = false;
				}
				java.util.Date date = new java.util.Date();
				long Currentst = date.getTime();
				if (Currentst - lastst < 1000) {
					return;
				}
				if (row == 0 && isupdating == false) {
					if (Current != null) {
						load();
						if (time != 0) {
							t = new Timer();
							t.scheduleAtFixedRate(new Chronometer(), time, time);
						}
					}
				} else if (testbool) {
					try {
						JSONArray snaps = Current.getJSONArray("snaps");
						JSONObject snap = new JSONObject();
						snap = snaps.getJSONObject(row - 5 - offsetint);
						SendSnap Sender = new SendSnap(Current, monApp,
								getthis());
						int k = Sender.Do(snap.getString("sn"));
						java.util.Date date1 = new java.util.Date();
						lastst = date1.getTime();
						offsetint = offsetint + k;
						Vectoroffset = Vectoroffset + k;
					} catch (JSONException e) {
						Dialog.inform(e.getMessage());
						e.printStackTrace();
					}
				} else if (row == 1)// send
				{
					try {
						SendSnap Sender = new SendSnap(Current, monApp,
								getthis());
						int k = Sender.Do(null);
						java.util.Date date2 = new java.util.Date();
						lastst = date2.getTime();
					} catch (Exception e) {
						return;
					}
//				} else if (row == 2)// send
//				{
//					try {
//						SendVideoSnap Sender = new SendVideoSnap(Current,
//								monApp, getthis());
//						Sender.Do(null);
//						java.util.Date date2 = new java.util.Date();
//						lastst = date2.getTime();
//					} catch (Exception e) {
//					}
				} else if (row == 2) // friends
				{
					if (Current != null) {
						monApp.pushModalScreen(FriendView);
					} else {
						Dialog.inform("Unable fetch Data. Please Update.");
					}
				} else if (row == 3) // story
				{
					if (Current != null) {
						if (isstoryloaded == false) {
							GetStory gs = new GetStory();
							gs.start();
							isstoryloaded = true;
						} else {
							monApp.pushScreen(story);
							Snaplist.remove(3);
							Snaplist.insert(3, new Object[] { _20img,
									"Stories", "" });
							isstoryloaded = false;
						}
					} else {
						Dialog.inform("Unable to fecth Data. Please Update");
					}
				} else if (row == 4) // settings
				{
					if (Current != null) {
						monApp.pushModalScreen(set);
						NotifyContextStore notifycontext = new NotifyContextStore();
						time = 1000 * notifycontext.GetStoredValues();
						if (time != 0) {
							t = new Timer();
							t.scheduleAtFixedRate(new Chronometer(), time, time);
						} else {
							try{
								t = new Timer();
								t.cancel();
							} catch (Exception e){
								
							}
						}
					} else {
						Dialog.inform("Unable to fetch data. Please update.");
					}
				} else {
					try {
						JSONArray snaps = Current.getJSONArray("snaps");
						JSONObject snap = new JSONObject();
						snap = snaps.getJSONObject(row - 5 - offsetint);
						int st = snap.getInt("st");
						String test = snap.getString("sn");
						if (st == 1) {
							int m = snap.getInt("m");
							if (m == 0) {
								byte[] decryptedata;
								EncodedImage image = null;
								try {
									decryptedata = crypto
											.decrypt((byte[]) BinarySnaps
													.elementAt(row - 5));
									image = EncodedImage.createEncodedImage(
											decryptedata, 0,
											decryptedata.length);
								} catch (Exception e) {
									Status.show("Fetching snap...");
									byte[] snapdata2 = API.GetSnap(
											Current.getString("username"),
											snap.getString("id"),
											Current.getString("auth_token"));
									decryptedata = crypto.decrypt(snapdata2);
									image = EncodedImage.createEncodedImage(
											decryptedata, 0,
											decryptedata.length);
								}
								if (((JPEGEncodedImage) image).getOrientation() != 1) {
									ImageManipulator ImageManip = new ImageManipulator(
											image.getBitmap());
									ImageManip.transformByAngle(-90, false,
											false);
									image = JPEGEncodedImage.encode(ImageManip
											.transformAndPaintBitmap(), 100);
								}
								int currentHeightFixed32 = Fixed32.toFP(image
										.getHeight());
								int requiredHeightFixed32 = Fixed32
										.toFP(Display.getHeight());
								int scaleYFixed32 = Fixed32.div(
										currentHeightFixed32,
										requiredHeightFixed32);
								image = image.scaleImage32(scaleYFixed32,
										scaleYFixed32);
								Bitmap bmp;
								bmp = image.getBitmap();
								DisplaySnap snapdisp = new DisplaySnap(image,
										bmp, snap.getInt("timer"), monApp,
										Current, snap.getString("id"));
								monApp.pushModalScreen(snapdisp);
								Object[] rowcontent = Snaplist.get(row);
								Snaplist.remove(row);
								String[] datet = Tools.split(
										rowcontent[2].toString(), '-');
								Snaplist.insert(row, new Object[] { _2img,
										(String) rowcontent[1],
										datet[0] + "- tap to reply" });
								DoNotLoadSnapAnymore[row - offsetint] = true; // désactive
								ThreadSendNotification trnotif = new ThreadSendNotification(
										snap.getString("id"),
										snapdisp.isscrshtd);
								nonviewed--;
								notification.update(nonviewed);
								trnotif.start();
							} else if (m == 3) {
								if (Dialog.ask(
										Dialog.D_OK_CANCEL,
										"Do you want to add "
												+ snap.getString("sn") + "?",
										Dialog.CANCEL) == Dialog.OK) {
									SnapChatApiFx api = new SnapChatApiFx();
									try {
										api.ManageFriend(snap.getString("sn"),
												Current.getString("username"),
												0,
												Current.getString("auth_token"));
										Dialog.inform("Done.");
									} catch (Exception e) {
										e.printStackTrace();
										Dialog.inform("Error while adding friend");
									}
								}
							} else if (m == 1 || m == 2) {
								byte[] snapdata;
								try {
									snapdata = (byte[]) BinarySnaps
											.elementAt(row - 5);
								} catch (Exception e) {
									Status.show("Fetching snap...");
									snapdata = API.GetSnap(
											Current.getString("username"),
											snap.getString("id"),
											Current.getString("auth_token"));
								}
								byte[] decryptedata;
								if (test.equals("teamsnapchat")) {
									decryptedata = snapdata;
								} else {
									decryptedata = crypto.decrypt(snapdata);
								}

								VideoPlay video = new VideoPlay(decryptedata,
										monApp);
								if (video.error == false) {
									monApp.pushModalScreen(video);
								}

								Object[] rowcontent = Snaplist.get(row);
								Snaplist.remove(row);
								String[] datet = Tools.split(
										rowcontent[2].toString(), '-');
								Snaplist.insert(row, new Object[] { _2img,
										(String) rowcontent[1],
										datet[0] + "- tap to reply" });
								DoNotLoadSnapAnymore[row - offsetint] = true; // désactive
								ThreadSendNotification trnotif = new ThreadSendNotification(
										snap.getString("id"), video.isscrshtd);
								nonviewed--;
								notification.update(nonviewed);
								trnotif.start();
							}
						} else if (st == 2) {
							try {
								SendSnap Sender = new SendSnap(Current, monApp,
										getthis());
								int k = Sender.Do(snap.getString("sn"));
								java.util.Date date3 = new java.util.Date();
								lastst = date3.getTime();
								offsetint = offsetint + k;
								Vectoroffset = Vectoroffset + k;
							} catch (Exception e) {
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}));
		return field;
	}
}