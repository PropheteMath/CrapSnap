package crapsnap;

import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.device.api.system.EncodedImage;

public class Notify {

	MessageNotif notif = new MessageNotif();
	private int _unviewed;

	public Notify(int i) {
		try {
			_unviewed = i;
			if (i > 0) {
				putnotification();
				setIcon(true);
			} else {
				setIcon(false);
			}
		} catch (Exception e) {
			return;
		}

	}

	public void update(int i) {
		if (i != _unviewed) {
			_unviewed = i;
			if (i < 1) {
				deletenotification();
				setIcon(false);
			} else {
				deletenotification();
				setIcon(true);
				putnotification();
			}
		}
	}

	public int GetUniviewed() {
		return _unviewed;
	}

	public void delete() {
		update(0);
	}

	private void putnotification() {

			}

	private void deletenotification() {

	}

	private void setIcon(boolean IsNonViewed) {
		EncodedImage NotifiedIcon;
		if (IsNonViewed == true) {
			NotifiedIcon = EncodedImage
					.getEncodedImageResource("NotifyIcon.png");
		} else {
			NotifiedIcon = EncodedImage
					.getEncodedImageResource("Snapchat_logo_small.png");
		}
		HomeScreen.updateIcon(NotifiedIcon.getBitmap());
	}
}
