package crapsnap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.decor.BackgroundFactory;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

public class StoryViewers extends NoPromtMainScreenSimple{
	
	RichListField list = new RichListField(this, true, 3, 0, true);
	StoryViewers(JSONArray MyStory){
		this.setTitle("Who has viewed my story ?");
		Bitmap back = EncodedImage.getEncodedImageResource("Back.jpg").getBitmap();
		this.setBackground(BackgroundFactory.createBitmapBackground(back));
		for (int i = 0; i < MyStory.length(); i++){
			JSONObject Storycore;
			try {
				Storycore = MyStory.getJSONObject(i).getJSONObject("story");
				byte[] img = getImage(
					Storycore.getString("thumbnail_url"),
					Storycore.getString("thumbnail_iv"),
					Storycore.getString("media_key"));
				String Caption = Storycore.getString("caption_text_display");
				if (Caption.equals("")){
					Caption = "Not Named Story.";
				}
				JSONObject StoryExtra = MyStory.getJSONObject(i).getJSONObject("story_extras");
				int viewcount = StoryExtra.getInt("view_count");
				JSONArray StoryNotes = MyStory.getJSONObject(i).getJSONArray("story_notes");
				String ViwerString = "";
				for (int j = 0; j < StoryNotes.length(); j++ ){
					if (ViwerString.equals("")){
						ViwerString = StoryNotes.getJSONObject(j).getString("viewer");
					} else {
						ViwerString =  ViwerString + ", " + StoryNotes.getJSONObject(j).getString("viewer") ;
					}
				}
				Bitmap bmp = EncodedImage.createEncodedImage(img, 0, img.length).getBitmap();
				list.add( new Object[] {bmp,Caption, "Viewed " + viewcount + " time(s)",ViwerString});
			} catch (JSONException e) {
				e.printStackTrace();
			}
			list.setCommand(new Command(new CommandHandler(){
				public void execute(ReadOnlyCommandMetadata metadata,
						Object context) {
					String viewers = (String) list.get(list.getFocusRow())[3];
					Dialog.inform("Viewed by : " + viewers);
				}
			}));
		}
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
