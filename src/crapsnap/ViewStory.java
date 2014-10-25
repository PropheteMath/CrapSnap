package crapsnap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.crypto.AESDecryptorEngine;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.CBCDecryptorEngine;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.PKCS5UnformatterEngine;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ImageManipulator.ImageManipulator;

public class ViewStory extends NoPromtMainScreenSimple {

	private boolean mark = false; // pour savoir si on a une story perso à
									// gerer.
	private int rownb = 0; // pour savoir si on a une story perso à gerer.
	JSONObject _Current = new JSONObject();
	JSONObject _Story = new JSONObject();
	JSONArray _FriendStory = new JSONArray();
	UiApplication _monApp;
	SnapChatApiFx api = new SnapChatApiFx();
	RichListField FriendLST;
	StoryViewers who;
	VerticalFieldManager MainManager = new VerticalFieldManager();
	Bitmap _blank = EncodedImage.getEncodedImageResource("empty.png")
			.getBitmap();
	private JSONArray _MYStory;
	private MenuItem _deleteitem; // suprimmer ma story.
	private MenuItem _whoitem; // suprimmer ma story.
	private Vector StoryVector = new Vector();

	ViewStory(JSONObject Current, UiApplication monApp) {
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		_monApp = monApp;
		_Current = Current;
		this.setTitle(" CrapSnap - View Stories");
		Bitmap back = EncodedImage.getEncodedImageResource("snapBack.png")
				.getBitmap();
		this.setBackground(BackgroundFactory.createBitmapBackground(back));

		// {
		// username: "youraccount",
		// timestamp: 1373207221,
		// req_token: create_token(auth_token, 1373207221)
		// }

		populate();

	}

	private void populate() {
		try {
			this.deleteAll();
			MainManager.deleteAll();
			Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
			_Story = api.ExecReq(
					"username="
							+ _Current.get("username")
							+ "&timestamp="
							+ TimeStamp.toString()
							+ "&req_token="
							+ SnapChatApiFx
									.tokengen(_Current.getString("auth_token"),
											TimeStamp).toString(),
					"https://feelinsonice-hrd.appspot.com/bq/stories");
			_FriendStory = _Story.getJSONArray("friend_stories");
			_MYStory = _Story.getJSONArray("my_stories");

			FriendLST = new RichListField(MainManager, true, 3, 0, true);

			if (_FriendStory.length() == 0 && _MYStory.length() == 0) {
				FriendLST.add(new Object[] { _blank, "No Story :/", "", "" });
				this.add(MainManager);
				return;
			}

			if (_FriendStory.length() != 0) {
				for (int i = 0; i < _FriendStory.length(); i++) {
					Vector thisStory = new Vector();
					JSONObject CurrentStorie = new JSONObject();
					CurrentStorie = (JSONObject) _FriendStory.get(i);
					JSONArray StoryArray = new JSONArray();
					StoryArray = (JSONArray) CurrentStorie.get("stories");
					JSONObject DFirstStory = (JSONObject) StoryArray.get(0);
					JSONObject FirstStory = DFirstStory.getJSONObject("story");
					byte[] img = getImage(
							FirstStory.getString("thumbnail_url"),
							FirstStory.getString("thumbnail_iv"),
							FirstStory.getString("media_key"));
					Bitmap bmp = null;
					String caption = "";
					for (int j = 0; j < StoryArray.length(); j++) {
						try {
							JSONObject DFirstStory2 = (JSONObject) StoryArray
									.get(j);
							JSONObject FirstStory2 = DFirstStory2
									.getJSONObject("story");
							byte[] img2 = getImage(
									FirstStory2.getString("media_url"),
									FirstStory2.getString("media_iv"),
									FirstStory2.getString("media_key"));
							thisStory.addElement(img2);
						} catch (Exception e) {
							thisStory.addElement(null);
						}
					}
					try {
						EncodedImage eImage = EncodedImage.createEncodedImage(
								img, 0, img.length);
						bmp = eImage.getBitmap();
					} catch (Exception e) {
						bmp = _blank;
					}
					try {
						caption = FirstStory.getString("caption_text_display");
						FriendLST
								.add(new Object[] {
										bmp,
										CurrentStorie.getString("username")
												+ "'s story...",
										caption,
										"Tap to view ! (" + StoryArray.length()
												+ ")" });
					} catch (Exception e) {
						FriendLST.add(new Object[] {
								bmp,
								CurrentStorie.getString("username")
										+ "'s story...",
								"Tap to view ! (" + StoryArray.length() + ")",
								"" });
					}
					StoryVector.addElement(thisStory);
					rownb++;
				}
			}
			if (_MYStory.length() == 0) {
				mark = false;
				FriendLST.add(new Object[] { _blank,
						"Your story is currently empty.", "", "" });
				rownb++;
			} else {
				mark = true;
				Vector thisStory = new Vector();
				who = new StoryViewers(_MYStory);
				_deleteitem = new MenuItem("Delete my stories", 110, 10) {
					public void run() {
						SnapChatApiFx api = new SnapChatApiFx();
						for (int i = 0; i < _MYStory.length(); i++) {
							int j = i + 1;
							Status.show("Deleting Story " + j + "/"
									+ _MYStory.length() + "...");
							try {
								JSONObject CurrentStory = _MYStory
										.getJSONObject(i);
								JSONObject CurrentStorycore = CurrentStory
										.getJSONObject("story");
								api.deletestory(_Current.getString("username"),
										CurrentStorycore.getString("id"),
										_Current.getString("auth_token"));
							} catch (JSONException e) {
								Dialog.inform(e.getMessage());
								e.printStackTrace();
							}
						}
						populate();
					}
				};
				this.addMenuItem(_deleteitem);
				_whoitem = new MenuItem("Who has viewed my story ?", 110, 10) {
					public void run() {
						_monApp.pushScreen(who);
					}
				};
				this.addMenuItem(_whoitem);
				JSONObject FirstStory = _MYStory.getJSONObject(0);
				// Voir la story
				JSONObject FirstStorycore = FirstStory.getJSONObject("story");
				byte[] img = getImage(
						FirstStorycore.getString("thumbnail_url"),
						FirstStorycore.getString("thumbnail_iv"),
						FirstStorycore.getString("media_key"));
				for (int j = 0; j < _MYStory.length(); j++) {
					try {
						JSONObject DFirstStory2 = (JSONObject) _MYStory.get(j);
						JSONObject FirstStory2 = DFirstStory2
								.getJSONObject("story");
						byte[] img2 = getImage(
								FirstStory2.getString("media_url"),
								FirstStory2.getString("media_iv"),
								FirstStory2.getString("media_key"));
						thisStory.addElement(img2);
					} catch (Exception e) {
						thisStory.addElement(null);
					}
				}
				StoryVector.addElement(thisStory);
				Bitmap bmp = null;
				String caption = "";
				try {
					EncodedImage eImage = EncodedImage.createEncodedImage(img,
							0, img.length);
					bmp = eImage.getBitmap();
				} catch (Exception e) {
					bmp = _blank;
				}
				try {
					caption = FirstStory.getString("caption_text_display");
					FriendLST.add(new Object[] { bmp, "My story...", caption,
							"Tap to view ! (" + _MYStory.length() + ")" });
					mark = true;
				} catch (Exception e) {
					FriendLST.add(new Object[] { bmp, "My story...",
							"Tap to view ! (" + _MYStory.length() + ")", "" });
					mark = true;
				}
			}
			if (_FriendStory.length() == 0) {
				FriendLST.add(new Object[] { _blank,
						"No friend story avaliable yet", "", "" });
				rownb = 0;
			}
			FriendLST.setCommand(new Command(new CommandHandler() {
				public void execute(ReadOnlyCommandMetadata metadata,
						Object context) {
					int row = FriendLST.getFocusRow();
					if (row == rownb && mark == true) {
						boolean breaked = false;
						Vector thisStory = (Vector) StoryVector
								.elementAt(StoryVector.size() - 1);
						for (int i = 0; i < _MYStory.length(); i++) {
							if (breaked == true) {
								return;
							}
							try {
								JSONObject DFirstStory = _MYStory
										.getJSONObject(i);
								JSONObject FirstStory = DFirstStory
										.getJSONObject("story");
								byte[] img;
								try{
									img = (byte[]) thisStory.elementAt(i);
								} catch (Exception e){
									 img = getImage(
											FirstStory.getString("media_url"),
											FirstStory.getString("media_iv"),
											FirstStory.getString("media_key"));
								}
								if (FirstStory.getInt("media_type") == 0) {
									EncodedImage eImage = EncodedImage
											.createEncodedImage(img, 0,
													img.length);
									EncodedImage tmpImage = eImage;
									if (((JPEGEncodedImage) eImage).getOrientation() != 1) {	
										ImageManipulator ImageManip = new ImageManipulator(eImage.getBitmap());
										ImageManip.transformByAngle(-90, false, false);
										eImage = JPEGEncodedImage.encode(ImageManip.transformAndPaintBitmap(), 100);
									}
									int currentHeightFixed32 = Fixed32
											.toFP(eImage.getHeight());
									int requiredHeightFixed32 = Fixed32
											.toFP(Display.getHeight());
									int scaleYFixed32 = Fixed32.div(
											currentHeightFixed32,
											requiredHeightFixed32);
									eImage = eImage.scaleImage32(scaleYFixed32,
											scaleYFixed32);
									Bitmap bmp = null;
									bmp = eImage.getBitmap();
									DisplayStory Displayer = new DisplayStory(
											tmpImage, bmp, _monApp, FirstStory
													.getInt("time"));
									_monApp.pushModalScreen(Displayer);
									if (Displayer.exited == true) {
										breaked = true;
									}
								} else {
									VideoPlay player = new VideoPlay(img,
											_monApp);
									_monApp.pushModalScreen(player);
								}
							} catch (Exception e) {
								Dialog.inform("Unable to load the story : "
										+ e.getMessage());
								e.printStackTrace();
							}
						}
					} else {
						JSONObject CurrentStorie = new JSONObject();
						try {
							CurrentStorie = (JSONObject) _FriendStory.get(row);
							JSONArray StoryArray = new JSONArray();
							StoryArray = (JSONArray) CurrentStorie
									.get("stories");
							boolean breaked = false;
							Vector ThisStory = (Vector) StoryVector
									.elementAt(row);
							for (int i = 0; i < StoryArray.length(); i++) {
								if (breaked == true) {
									return;
								}
								try {
									JSONObject DFirstStory = (JSONObject) StoryArray
											.get(i);
									JSONObject FirstStory = DFirstStory
											.getJSONObject("story");
									byte[] img;
									try{
										img = (byte[]) ThisStory.elementAt(i);
									} catch (Exception e){
										 img = getImage(
												FirstStory.getString("media_url"),
												FirstStory.getString("media_iv"),
												FirstStory.getString("media_key"));
									}
									if (FirstStory.getInt("media_type") == 0) {
										EncodedImage eImage = EncodedImage
												.createEncodedImage(img, 0,
														img.length);
										int currentHeightFixed32 = Fixed32
												.toFP(eImage.getHeight());
										int requiredHeightFixed32 = Fixed32
												.toFP(Display.getHeight());
										int scaleYFixed32 = Fixed32.div(
												currentHeightFixed32,
												requiredHeightFixed32);
										EncodedImage tmpImage = eImage;
										eImage = eImage.scaleImage32(
												scaleYFixed32, scaleYFixed32);
										Bitmap bmp = null;
										bmp = eImage.getBitmap();
										DisplayStory Displayer = new DisplayStory(
												tmpImage, bmp, _monApp,
												FirstStory.getInt("time"));
										_monApp.pushModalScreen(Displayer);
										if (Displayer.exited == true) {
											breaked = true;
										}
									} else {
										VideoPlay player = new VideoPlay(img,
												_monApp);
										if (player.error == false) {
											_monApp.pushModalScreen(player);
										}
									}
								} catch (Exception e) {
									Dialog.inform("Unable to load the story : "
											+ e.getMessage());
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}));

		} catch (Exception e) {
			Dialog.inform("Unable to load the story : " + e.getMessage());
			e.printStackTrace();
		}
		this.add(MainManager);
	}

	private byte[] getImage(String URL, String IV, String KEY) {
		try {

			HttpConnection c = null;
			InputStream is = null;
			MyConnectionFactory factory = new MyConnectionFactory();
			factory.setConnectionTimeout(5000L);
			ConnectionDescriptor connDesc = factory.getConnection(URL);
			c = (HttpConnection) connDesc.getConnection();
			c.setRequestMethod(HttpConnection.GET);
			int rc = c.getResponseCode();
			if (rc != HttpConnection.HTTP_OK
					&& rc != HttpConnection.HTTP_ACCEPTED) {
				throw new IOException("HTTP " + rc);
			}
			is = c.openInputStream();
			byte[] thumbdata = IOUtilities.streamToBytes(is);
			byte[] ivData = Base64InputStream.decode(IV);
			byte[] keyData = Base64InputStream.decode(KEY);
			AESKey key = new AESKey(keyData);// Create a new DES key
												// with the given data.
			AESDecryptorEngine aesEngine = new AESDecryptorEngine(key); // Create
																		// the
																		// DES
																		// engine.
			InitializationVector iv = new InitializationVector(ivData);
			CBCDecryptorEngine cbcEngine = new CBCDecryptorEngine(aesEngine, iv);// Create
			// the
			// CBC
			// engine.
			PKCS5UnformatterEngine unformatter = new PKCS5UnformatterEngine(
					cbcEngine);// Create the PKCS5 Decoder engine.
			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					thumbdata);// Create a stream from the input byte
								// array.
			BlockDecryptor decryptor = new BlockDecryptor(unformatter,
					inputStream);// Create the Decryptor using the
									// CBCDecryptorEngine.
			byte[] decryptedata = new byte[thumbdata.length];
			decryptor.read(decryptedata);
			decryptor.close();
			inputStream.close();
			return decryptedata;
		} catch (IOException e) {
			Dialog.inform(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (CryptoTokenException e) {
			Dialog.inform(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (CryptoUnsupportedOperationException e) {
			Dialog.inform(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
