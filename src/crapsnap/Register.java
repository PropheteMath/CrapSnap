package crapsnap;

import java.io.InputStream;
import java.util.Enumeration;

import org.json.me.JSONObject;

import zipme.ZipArchive;
import zipme.ZipEntry;
import zipme.ZipException;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

public class Register {

	UiApplication _App;
	SnapChatApiFx api = new SnapChatApiFx();
	private String authtoken = "";
	private String email = "";
	private String solution = "";
	private String id = "";
	private String pwd = "";
	private String user = "";

	Register(UiApplication app) {
		_App = app;
	}

	public void Do() {

		boolean aop = false;
		boolean bop = false;
		boolean cop = false;
		JSONObject obj = new JSONObject();

		try {

			while (aop == false) {
				RegisterScreen1 scr1 = new RegisterScreen1(_App);
				_App.pushModalScreen(scr1);
				if (scr1.returntouch == true)
					return;
				email = scr1.email;

				try {
					Status.show("Please wait...");
					obj = api.RegisterRequest(scr1.age, scr1.email, scr1.pwd,
							scr1.birthday);
					pwd = scr1.pwd;
					authtoken = obj.getString("auth_token");
					aop = true;
				} catch (Exception e) {
					try {
						Dialog.inform(obj.getString("message"));
					} catch (Exception ex) {
						Dialog.inform("Unable to connect to SnapChat servers. Pleasy retry registering process.");
						return;
					}
				}
			}

			while (bop == false) {
				EncodedImage[] imgtab = null;
				int i = 0;
				byte[] zipped = null;
				try {
					Status.show("Please wait...");
					zipped = api.RegisterCaptcha(email, authtoken);
					imgtab = new EncodedImage[9];
					String Content_Disposition = api.Content_Disposition;
					id = Content_Disposition.substring(20,
							Content_Disposition.length() - 4);
					try {
						ZipArchive archive = new ZipArchive(zipped);
						Enumeration entries = archive.entries();
						while (entries.hasMoreElements()) {
							ZipEntry current = (ZipEntry) entries.nextElement();
							InputStream currentis = archive
									.getInputStream(current);
							byte[] currentbyte = IOUtilities
									.streamToBytes(currentis);
							EncodedImage img = EncodedImage.createEncodedImage(
									currentbyte, 0, currentbyte.length);
							imgtab[i] = img;
							i++;
						}
						RegisterScreen2 scr2 = new RegisterScreen2(_App, imgtab);
						_App.pushModalScreen(scr2);
						if (scr2.returntouch == true)
							return;
						solution = scr2.solution;
						Status.show("Please wait...");
						api.solvecaptcha(email, authtoken, solution, id);
						if (api._rc != 200) {
							Dialog.inform("Bad capchat answer. Please select ALL images containing with a ghost");
						} else {
							bop = true;
						}
					} catch (ZipException e) {
						e.printStackTrace();
					} catch (Exception e) {
						Dialog.inform("Unable to connect to SnapChat servers. Pleasy retry registering process.");
						return;
					}
				} catch (Exception e) {
					Dialog.inform(obj.getString("Unable to get the CapChat"));
				}
			}

			while (cop == false) {
				try {
					RegisterScreen3 scr3 = new RegisterScreen3(_App);
					_App.pushModalScreen(scr3);
					if (scr3.returntouch == true)
						return;
					try {
						user = scr3.username;
						Status.show("Please wait...");
						obj = api.setusername(email, authtoken, scr3.username);
						if (api._rc != 200) {
							try {
								Dialog.inform(obj.getString("message"));
							} catch (Exception e) {
								Dialog.inform("Unable to connect to SnapChat servers. Pleasy retry registering process.");
								return;
							}
						}
					} catch (Exception e) {
						Dialog.inform("Error while attaching username.");
					}
					ViewSnap _snap = new ViewSnap(pwd, user, _App, "");
					if (_snap.unable == true) {
						Dialog.inform("Username already in use : choose another one.");
					} else {
						ContextStore storage = new ContextStore();
						String[] userinfo = { user, pwd, "true", "" };
						storage.StoreValues(userinfo);
						NotifyContextStore notify = new NotifyContextStore();
						notify.StoreValues(300);
						_App.popScreen(_App.getActiveScreen());
						_App.pushScreen(_snap);
						cop = true;
					}
				} catch (Exception e) {
					Dialog.inform("Unable to connect to SnapChat servers. Pleasy retry registering process.");
					return;
				}
			}

		} catch (Exception e) {
			Dialog.inform("Unable to connect to SnapChat servers. Pleasy retry registering process.");
			return;
		}

	}
}
