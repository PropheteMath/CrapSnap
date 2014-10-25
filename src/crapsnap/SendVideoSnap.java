package crapsnap;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.picker.FilePicker;

public class SendVideoSnap {

	SnapChatApiFx API;
	Cryptography crypto = new Cryptography();
	JSONObject Current = new JSONObject();
	UiApplication monApp;
	byte[] _encryptedImg;
	public String ReceveirString = "";
	protected boolean success = false;
	private ViewSnap _viewSnap;
	private FriendSelector friendselector;

	public SendVideoSnap(JSONObject args, UiApplication app, ViewSnap viewSnap) {
		_viewSnap = viewSnap;
		monApp = app;
		Current = args;
		API = new SnapChatApiFx();
		friendselector = new FriendSelector(Current, monApp);
	}

	String Recipent = new String();

	public int Do(String Id) {
		int w = Dialog.ask(Dialog.D_OK_CANCEL, "Warning on video : This may consume lot of data : choose a low quality.");
		int receiver = 0;
		String selectedPath = "";
		InputStream is = null;
		byte[] OutData = null;
		
		VideoMaker filmer = new VideoMaker();
		monApp.pushModalScreen(filmer);
		FilePicker fp = FilePicker.getInstance();
		fp.setTitle("CrapSnap - Select a Video");
		fp.setView(FilePicker.VIEW_VIDEOS);
		selectedPath = fp.show();
		if (selectedPath.length() == 0) {
			fp.cancel();
			return 0;
		}
		FileConnection fileConnection;
		try {
			fileConnection = (FileConnection) Connector.open(selectedPath);
			is = fileConnection.openInputStream();
			OutData = IOUtilities.streamToBytes(is);
			is.close();
		} catch (IOException e) {
			return 0;
		}
		byte[] encryptedImg = crypto.encrypt(OutData);
		monApp.pushModalScreen(friendselector);
		Recipent = friendselector.GetNames();
		if (friendselector.IsCanceled() == true) {
			return 0;
		} else if (Recipent.equals("") && friendselector.IsStoryed() == false) {
			Dialog.inform("No Recipent Selected");
			return 0;
		} else {
			Recipent = friendselector.GetNames();
			receiver = friendselector.Getnb();
			ThreadSendSnap sendstr = new ThreadSendSnap( encryptedImg, Recipent, 10);
			sendstr.start(); 

		}
		return 0;
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

		public void  run() {
			try {
				Thread.sleep(10);
				ReceveirString = Recipent + "";
				 synchronized (Application.getEventLock()) {
				_viewSnap.Snaplist.insert( 6, new Object[] { _viewSnap._0img,
						"" + ReceveirString, "Sending..." });}
				_viewSnap.offsetint++;
				_viewSnap.Vectoroffset = _viewSnap.Vectoroffset + 1;
				if (friendselector.IsStoryed() == true) {
					Thread.sleep(10);
					if (API.SendStory(encryptedImg, 0, Recipent,
							Current.getString("username"),
							Current.getString("auth_token"), time, friendselector.caption) == true) {
						 synchronized (Application.getEventLock()) {
						_viewSnap.Snaplist.remove(6);
						_viewSnap.Snaplist.insert(6,
								new Object[] { _viewSnap._0img, "" + ReceveirString,
										"Moment ago..." });
						 }
						return;
					}
				} else {
					Thread.sleep(10);
					if (API.SendSnap(encryptedImg, 0, Recipent,
							Current.getString("username"),
							Current.getString("auth_token"), time) == true) {
						 synchronized (Application.getEventLock()) {
						_viewSnap.Snaplist.remove(6);
						_viewSnap.Snaplist.insert(6,
								new Object[] { _viewSnap._0img, "" + ReceveirString,
										"Moment ago..." });}
						return;
					}
				}
				 synchronized (Application.getEventLock()) {
					 _viewSnap.Snaplist.remove(6);
					 _viewSnap.Snaplist.insert(6,
						new Object[] { _viewSnap._0img, "" + ReceveirString,
								"Error : unable to send the snap." });}
			} catch (JSONException e) {
				e.printStackTrace();
				ReceveirString = Recipent + "";
				 synchronized (Application.getEventLock()) {
					 _viewSnap.Snaplist.remove(6);
					 _viewSnap.Snaplist.insert(6,
						new Object[] { _viewSnap._0img, "" + ReceveirString,
								"Error : unable to send the snap." });}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
