package crapsnap;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ImageManipulator.ImageManipulator;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.system.PNGEncodedImage;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import net.rim.device.api.ui.picker.FilePicker;

public class SendSnap {

	SnapChatApiFx API;
	Cryptography crypto = new Cryptography();
	JSONObject Current = new JSONObject();
	UiApplication monApp;
	byte[] _encryptedImg;
	ViewSnap _viewSnap;

	public String ReceveirString = "";

	public SendSnap(JSONObject args, UiApplication app, ViewSnap viewSnap) {
		_viewSnap = viewSnap;
		monApp = app;
		Current = args;
		API = new SnapChatApiFx();
		friendselector = new FriendSelector(Current, monApp);
	}

	public boolean success = false;
	private FriendSelector friendselector;
	int receiver = 0;
	String caption = "";

	public int Do(String Id) {
		
		String selectedPath = "";
		InputStream is = null;
		byte[] OutData = null;
		try {
			FilePicker fp = FilePicker.getInstance();
			fp.setTitle(" CrapSnap - Select an Image");
			fp.setView(1);
			selectedPath = fp.show();

			if (selectedPath.length() == 0) {
				fp.cancel();
				return 0;
			}
			fp.cancel();
			fp.setListener(null);
			FileConnection fileConnection = (FileConnection) Connector
					.open(selectedPath);
			is = fileConnection.openInputStream();
			OutData = IOUtilities.streamToBytes(is);
			is.close();

			EncodedImage eImage = (JPEGEncodedImage) EncodedImage
					.createEncodedImage(OutData, 0, OutData.length);
			if (((JPEGEncodedImage) eImage).getOrientation() != 1) {
				ImageManipulator ImageManip = new ImageManipulator(
						eImage.getBitmap());
				ImageManip.transformByAngle(-90, false, false);
				Bitmap tmp = ImageManip.transformAndPaintBitmap();
				JPEGEncodedImage tmpeImage =  JPEGEncodedImage.encode(
				tmp, 100);
				eImage = (EncodedImage) tmpeImage;
//				eImage = PNGEncodedImage.encode(ImageManipulator.rotate(eImage.getBitmap(), 270));
			}

			int currentWidthFixed32 = Fixed32.toFP(eImage.getWidth());
			int currentHeightFixed32 = Fixed32.toFP(eImage.getHeight());
			int requiredWidthFixed32 = Fixed32.toFP(Display.getWidth());
			int requiredHeightFixed32 = Fixed32.toFP(Display.getHeight());
			int scaleXFixed32 = Fixed32.div(currentWidthFixed32,
					requiredWidthFixed32);
			int scaleYFixed32 = Fixed32.div(currentHeightFixed32,
					requiredHeightFixed32);
			if (eImage.getWidth() > Display.getWidth()
					|| eImage.getHeight() > Display.getHeight()) {
				if (Fixed32.mul(scaleXFixed32, currentHeightFixed32) < requiredHeightFixed32) {
					eImage = (JPEGEncodedImage) eImage.scaleImage32(
							scaleXFixed32, scaleXFixed32);
				} else {
					eImage = (JPEGEncodedImage) eImage.scaleImage32(
							scaleYFixed32, scaleYFixed32);
				}
			}

			EditSnap Editor = new EditSnap(monApp, eImage);
			monApp.pushModalScreen(Editor);
			if (Editor.IsCanceled() == true) {
				return 0;
			}
			eImage = (JPEGEncodedImage) Editor.GetImg();
			caption = Editor.caption;
			byte[] imgByt = eImage.getData();
			byte[] encryptedImg = crypto.encrypt(imgByt);

			String Recipent = new String();
			if (Id != null) {
				Recipent = Id;
				ThreadSendSnap senttrd = new ThreadSendSnap(encryptedImg,
						Recipent, Editor.GetTime());
				senttrd.start();
				ReceveirString = Recipent + "";
				receiver = 0;
			} else {
				try {
					monApp.pushModalScreen(friendselector);
					if (friendselector.IsCanceled() == true) {
						return 0;
					}
					Recipent = friendselector.GetNames();
					receiver = friendselector.Getnb();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (Recipent.equals("") && friendselector.IsStoryed() == false) {
					Dialog.inform("No Recipent Selected");
					return 0;
				} else {
					ThreadSendSnap senttrd = new ThreadSendSnap(encryptedImg,
							Recipent, Editor.GetTime());
					senttrd.start();
					success = true;
					ReceveirString = Recipent + "";
				}

			}
		} catch (IOException e) {
			Dialog.inform("IO Error");
			return 0;
		} catch (Exception e) {
			try {
				is.close();
			} catch (IOException e1) {
				return 0;
			}
			return 0;
		}
		return receiver;
	}

	private class ThreadSendSnap extends Thread {

		private byte[] encryptedImg;
		private String Recipent;
		private int time;

		ThreadSendSnap(byte[] _encryptedImg, String _Recipent, int _time) {
			time = _time;
			Recipent = _Recipent;
			encryptedImg = _encryptedImg;
		}

		public void run() {
			try {
				Thread.sleep(10);
				ReceveirString = Recipent + "";
				if (ReceveirString.equals("")) {
					ReceveirString = "My Story.";
				}
				synchronized (Application.getEventLock()) {
					_viewSnap.Snaplist.insert(5,
							new Object[] { _viewSnap._0img,
									"" + ReceveirString, "Sending..." });
				}
				_viewSnap.offsetint++;
				_viewSnap.Vectoroffset = _viewSnap.Vectoroffset + 1;
				if (friendselector.IsStoryed() == true) {
					Thread.sleep(10);
					if (API.SendStory(encryptedImg, 0, Recipent,
							Current.getString("username"),
							Current.getString("auth_token"), time, caption) == true && (API._rc == HttpConnection.HTTP_OK
							|| API._rc == HttpConnection.HTTP_ACCEPTED)) {
						synchronized (Application.getEventLock()) {
							_viewSnap.Snaplist.remove(5);
							if (ReceveirString.equals("")) {
								ReceveirString = "My Story.";
							}
							_viewSnap.Snaplist.insert(5, new Object[] {
									_viewSnap._0img, "" + ReceveirString,
									"Moment ago..." });
						}
						return;
					}
				} else {
					Thread.sleep(10);
					if (API.SendSnap(encryptedImg, 0, Recipent,
							Current.getString("username"),
							Current.getString("auth_token"), time) == true && (API._rc == HttpConnection.HTTP_OK
									|| API._rc == HttpConnection.HTTP_ACCEPTED)) {
						synchronized (Application.getEventLock()) {
							_viewSnap.Snaplist.remove(5);
							_viewSnap.Snaplist.insert(5, new Object[] {
									_viewSnap._0img, "" + ReceveirString,
									"Moment ago..." });
						}
						return;
					}
				}
				synchronized (Application.getEventLock()) {
					_viewSnap.Snaplist.remove(5);
					_viewSnap.Snaplist.insert(5, new Object[] {
							_viewSnap._0img, "" + ReceveirString,
							"Error : unable to send the snap." });
				}
			} catch (JSONException e) {
				e.printStackTrace();
				ReceveirString = Recipent + "";
				synchronized (Application.getEventLock()) {
					_viewSnap.Snaplist.remove(5);
					_viewSnap.Snaplist.insert(5, new Object[] {
							_viewSnap._0img, "" + ReceveirString,
							"Error : unable to send the snap." });
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
