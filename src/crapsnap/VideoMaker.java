package crapsnap;

import java.io.IOException;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

//Create the framework for the custom screen by extending the MainScreen 
//class. Declare an instance of the VideoRecorderThread class.  
class VideoMaker extends MainScreen {
	private VideoRecorderThread _recorderThread;

	// In the VideoRecordingDemoScreen constructor, invoke setTitle() to specify
	// the
	// title for the screen. Invoke addMenuItem() twice to add the menu items
	// that start and stop the recording.
	public VideoMaker() {
		setTitle("CrapSnap - Recording Video Snap.");

		addMenuItem(new StartRecording());
		addMenuItem(new StopRecording());
	}

	// In the VideoRecordingDemoScreen class, define the menu items to start and
	// stop
	// the recording. The menu items invoke the VideoRecorederThread.start() and
	// VideoRecorederThread.stop() methods.
	private class StartRecording extends MenuItem {
		public StartRecording() {
			super("Start recording", 0, 100);
		}

		public void run() {
			try {
				VideoRecorderThread newRecorderThread = new VideoRecorderThread();
				newRecorderThread.start();
				_recorderThread = newRecorderThread;
			} catch (Exception e) {
				Dialog.alert(e.toString());
			}
		}
	}

	private class StopRecording extends MenuItem {
		public StopRecording() {
			super("Stop recording", 0, 100);
		}

		public void run() {
			try {
				if (_recorderThread != null) {
					_recorderThread.stop();
				}
			} catch (Exception e) {
				Dialog.alert(e.toString());
			}
		}
	}

	// In the VideoRecordingDemo screen class, define an inner class that
	// extends
	// Thread and implements PlayerListener. In the class, create a variable of
	// type Player, and a variable of type RecordControl for recording media
	// from
	// Player. You do not need to record video in a separate thread because
	// recording operations are threaded by design.
	private class VideoRecorderThread extends Thread implements
			javax.microedition.media.PlayerListener {
		private Player _player;
		private RecordControl _recordControl;

		VideoRecorderThread() {
		}

		// In the VideoRecorderThread class, implement run(). In run() create a
		// try/catch
		// block and invoke Manager.createPlayer(String locator) to create a
		// Player object
		// to capture video, using as a parameter a value that specifies the
		// encoding
		// to use to record video.
		public void run() {
			try {
				_player = javax.microedition.media.Manager
						.createPlayer("capture://video?encoding=video/3gpp&audio_codec=AAC&video_codec=H264&rate=32000&video_rate=282000 ");
				// Invoke Player.addPlayerListener(). Specify this as a
				// parameter because
				// VideoRecorderThread implements PlayerListener.
				_player.addPlayerListener(this);

				// Invoke Player.realize() to initialize the VideoControl
				// object.
				_player.realize();

				// Invoke Player.getControl("VideoControl") to retrieve the
				// VideoControl object.
				VideoControl videoControl = (VideoControl) _player
						.getControl("VideoControl");
				_recordControl = (RecordControl) _player
						.getControl("RecordControl");

				// Invoke VideoControl.initDisplayMode(int mode, Object arg). To
				// initialize the mode
				// that a video field uses, pass an arg parameter to specify the
				// UI primitive that
				// displays the video. Cast the returned object as a Field
				// object. You can invoke
				// initDisplayMode() in different ways to return a Field (or an
				// Item to display a
				// video on a Canvas class in a MIDlet).
				Field videoField = (Field) videoControl.initDisplayMode(
						VideoControl.USE_GUI_PRIMITIVE,
						"net.rim.device.api.ui.Field");

				try {

					// In a try/catch block, invoke
					// VideoControl.setDisplaySize() to set the size of the
					// viewfinder to monitor your recording. In this example,
					// the size is the full screen.
					// Invoke add() to add the viewfinder to the screen.
					videoControl.setDisplaySize(Display.getWidth(),
							Display.getHeight());
				} catch (MediaException me) {
					// setDisplaySize is not supported
				}

				add(videoField);

				// Invoke RecordControl.setRecordLocation() to specify the
				// location on the device
				// to save the video recording.
				_recordControl
						.setRecordLocation("file:///store/home/user/VideoRecordingTest.mp4");

				// Invoke RecordControl.startRecord() to start recording the
				// video and start
				// playing the media from Player. Invoke Player.start() to start
				// Player.
				_recordControl.startRecord();
				_player.start();

			} catch (IOException e) {
				Dialog.alert(e.toString());
			} catch (MediaException e) {
				Dialog.alert(e.toString());
			}
		}

		// In VideoRecorderThread, implement the Thread interface's stop method.
		// Check that
		// Player is not null and invoke Player.close() to release the Player
		// object's
		// resources. Then set the Player to null.
		public void stop() {
			if (_player != null) {
				_player.close();
				_player = null;
			}

			// Check that RecordControl is not null and invoke
			// RecordControl.stopRecord() to
			// stop recording. In a try/catch block, invoke
			// RecordControl.commit() to save
			// the recording to a specified file. Then set RecordControl to
			// null.
			if (_recordControl != null) {
				_recordControl.stopRecord();

				try {
					_recordControl.commit();
				} catch (Exception e) {
					Dialog.alert(e.toString());
				}
				_recordControl = null;
			}
		}

		// In VideoRecorderThread, implement the PlayerListener interface's
		// playerUpdate() method, which is invoked when the Player object
		// generates an
		// event. In this example, information about the event is displayed.
		public void playerUpdate(Player player, String event, Object eventData) {
			Dialog.alert("Player " + player.hashCode() + " got event " + event
					+ ": " + eventData);
		}
	}
}
