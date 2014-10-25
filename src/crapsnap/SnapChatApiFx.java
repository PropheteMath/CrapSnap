package crapsnap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.crypto.DigestOutputStream;
import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;

import org.json.me.JSONObject;

public class SnapChatApiFx {

	// tokengen key
	private static String Secret = "iEk21fuwZApXlz93750dmW22pw389dPwOk";
	private static String Pattern = "0001110111101110001111010101111011010001001110011000110001000110";
	private static String StaticToken = "m198sOkJEn37DjqZ32lpRu76xmw288xSQ9";

	/**
	 * POST parameter keys for sending requests to Snapchat.
	 */
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String TIMESTAMP_KEY = "timestamp";
	private static final String REQ_TOKEN_KEY = "req_token";
	private static final String ID_KEY = "id";
	private static final String MEDIA_ID_KEY = "media_id";
	private static final String TYPE_KEY = "type";
	private static final String DATA_KEY = "data";
	private static final String ZIPPED_KEY = "zipped";
	private static final String TIME_KEY = "time";
	private static final String RECIPIENT_KEY = "recipient";

	/**
	 * Paths for various Snapchat actions, relative to BASE_URL.
	 */
	private static final String LOGIN_PATH = "bq/login";
	private static final String UPLOAD_PATH = "bq/upload";
	private static final String SEND_PATH = "ph/send";
	private static final String BLOB_PATH = "ph/blob";

	/**
	 * Static members for forming HTTP requests.
	 */
	private static final String BASE_URL = "https://feelinsonice-hrd.appspot.com/";
	private static final String JSON_TYPE_KEY = "accept";
	private static final String JSON_TYPE = "application/json";
	private static final String USER_AGENT_KEY = "User-Agent";
	private static final String USER_AGENT = "Snapchat/5.0.3 (Nexus 5; Android 19; gzip)";

	public SnapChatApiFx() {
	} // Constructeur vide

	// Requète de connection
	public JSONObject login(String _Username, String _Password) {
		String BAD = new String();
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		BAD = USERNAME_KEY + "=" + _Username + "&" + PASSWORD_KEY + "="
				+ _Password + "&" + TIMESTAMP_KEY + "=" + TimeStamp.toString()
				+ "&" + REQ_TOKEN_KEY + "="
				+ tokengen(StaticToken, TimeStamp).toString();
		return ExecReq(BAD, BASE_URL + LOGIN_PATH);
	}

	// {
	// username: "youraccount",
	// timestamp: 1373207221,
	// req_token: create_token(auth_token, 1373207221),
	// added_friends_timestamp: 1373206707,
	// json:
	// "{\"325922384426455124r\":{\"c\":0,\"t\":1385378843,\"replayed\":0}}",
	// events: "[]"
	// }

	public void NotifySnap(String Username, boolean ScrShoot,
			String Addedfriendts, String key, String Token) {
		int myInt = (ScrShoot) ? 1 : 0; // Because I'm God.
		try {
			String Request = new String();
			Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
			Request = USERNAME_KEY + "=" + Username + "&" + TIMESTAMP_KEY + "="
					+ TimeStamp.toString() + "&" + REQ_TOKEN_KEY + "="
					+ tokengen(Token, TimeStamp).toString() + "&"
					+ "added_friends_timestamp" + "=" + Addedfriendts + "&"
					+ "json=" + "{\"" + key + "\":{\"c\":" + myInt + ",\"t\":"
					+ TimeStamp.toString() + ",\"replayed\":" + "0" + "}}"
					+ "&events=\"[]\"";
			ExecReq(Request, BASE_URL + "bq/update_snaps");
		} catch (Exception e) {

		}
	}

	// Deleting story segments (/bq/delete_story) ¶
	// {
	// username: "youraccount",
	// timestamp: 1373207221,
	// req_token: create_token(auth_token, 1373207221),
	// story_id: "youraccount~1382716927240"
	// }

	public void deletestory(String Username, String StoryId, String Token) {
		try {
			String Request = new String();
			Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
			Request = USERNAME_KEY + "=" + Username + "&" + TIMESTAMP_KEY + "="
					+ TimeStamp.toString() + "&" + REQ_TOKEN_KEY + "="
					+ tokengen(Token, TimeStamp).toString() + "&" + "story_id"
					+ "=" + StoryId;
			ExecReq(Request, BASE_URL + "/bq/delete_story");
		} catch (Exception e) {

		}
	}

	// {
	// username: "youraccount",
	// timestamp: 1373207221,
	// req_token: create_token(auth_token, 1373207221),
	// friend_usernames: "['teamsnapchat','another_username']",
	// }
	public JSONObject GetScore(String Name, String Username, String Token) {
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		String Request = USERNAME_KEY + "=" + Username + "&" + TIMESTAMP_KEY
				+ "=" + TimeStamp.toString() + "&" + REQ_TOKEN_KEY + "="
				+ tokengen(Token, TimeStamp).toString() + "&"
				+ "friend_usernames" + "=[\"" + Name + "\"]";
		return ExecReq(Request, BASE_URL + "bq/bests");
	}

	// {
	// username: "youraccount",
	// timestamp: 1373207221,
	// req_token: create_token(auth_token, 1373207221),
	// action: "add",
	// friend: "someguy"
	// }

	// What type of action you're taking: add, delete, block, unblock, or
	// display.

	public JSONObject ManageFriend(String Name, String Username, int action,
			String Token) {
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		String Action = new String();
		switch (action) {
		case 0: // add
			Action = "add";
			break;
		case 1: // delete
			Action = "delete";
			break;
		case 2: // block
			Action = "block";
			break;
		case 3: // unblock
			Action = "unblock";
			break;
		case 4:// display
			Action = "display";
			break;
		}
		String Request = USERNAME_KEY + "=" + Username + "&" + TIMESTAMP_KEY
				+ "=" + TimeStamp.toString() + "&" + REQ_TOKEN_KEY + "="
				+ tokengen(Token, TimeStamp).toString() + "&" + "action" + "="
				+ Action + "&" + "friend" + "=" + Name;
		return ExecReq(Request, BASE_URL + "ph/friend");
	}

	// {
	// username: "youraccount",
	// timestamp: 1373207221,
	// req_token: create_token(auth_token, 1373207221)
	// }

	public void ClearFeed(String UserName, String Token) {
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		String reqtoken = tokengen(Token, TimeStamp).toString();
		String request = "username=" + UserName + "&timestamp=" + TimeStamp
				+ "&req_token=" + reqtoken;
		ExecReq(request, BASE_URL + "/ph/clear");
		Dialog.inform("Done. Update to see changes.");
	}

	public void UpDate(String UserName, String Token, String Key, String Value) {
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		String reqtoken = tokengen(Token, TimeStamp).toString();
		String request = "username=" + UserName + "&timestamp=" + TimeStamp
				+ "&req_token=" + reqtoken;
		String param = new String();
		if (Key.equals("email")) {
			param = "&action=updateEmail&email=" + Value;
		} else if (Key.equals("Birthday")) {
			param = "&action=updateBirthday&birthday=&" + Value;
		} else if (Key.equals("StoriesP")) {
			param = "&action=updateStoryPrivacy&privacySetting=" + Value;
		} else if (Key.equals("Mature")) {
			param = "&action=updateCanViewMatureContent&canViewMatureContent="
					+ Value;
		} else if (Key.equals("SnapP")) {
			param = "&action=updatePrivacy&privacySetting=" + Value;
		}
		request = request + param;
		ExecReq(request, BASE_URL + "/ph/settings");
	}

	public void SetBestFriendNumber(String UserName, String Token, String Value) {
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		String reqtoken = tokengen(Token, TimeStamp).toString();
		String request = "username=" + UserName + "&timestamp=" + TimeStamp
				+ "&req_token=" + reqtoken + "&num_best_friends=" + Value;
		ExecReq(request, BASE_URL + "/bq/set_num_best_friends");
	}

	public void LogOut(String UserName, String Token) {
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		String reqtoken = tokengen(Token, TimeStamp).toString();
		String request = "username=" + UserName + "&timestamp=" + TimeStamp
				+ "&req_token=" + reqtoken;
		ExecReq(request, BASE_URL + "/ph/logout");
	}

	/*
	 * { username: "youraccount", timestamp: 1373207221, req_token:
	 * create_token(auth_token, 1373207221), id: "97117373178635038r" }
	 */
	public byte[] GetSnap(String UserName, String id, String Token) {
		String str = new String();
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		str = USERNAME_KEY + "=" + UserName + "&" + ID_KEY + "="
				+ id.toString() + "&" + TIMESTAMP_KEY + "="
				+ TimeStamp.toString() + "&" + REQ_TOKEN_KEY + "="
				+ tokengen(Token, TimeStamp).toString();
		return ExecReqBin(str, BASE_URL + BLOB_PATH);
	}

	// {
	// username: "youraccount",
	// timestamp: 1373207221,
	// req_token: create_token(auth_token, 1373207221)
	// media_id: "YOURACCOUNT~9c0b0193-de58-4b8d-9a09-60039648ba7f",
	// type: 0,
	// data: ENCRYPTED_SNAP_DATA
	// }
	public boolean SendSnap(byte[] snap, int type, String receiver,
			String UserName, String Token, int time) {
		String uuid = UserName.toUpperCase() + "~" + generateGUID();
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);

		RequestParam UserParam = new RequestParam(USERNAME_KEY, UserName);
		RequestParam TSParam = new RequestParam(TIMESTAMP_KEY,
				TimeStamp.toString());
		RequestParam TokenParam = new RequestParam(REQ_TOKEN_KEY, tokengen(
				Token, TimeStamp).toString());
		RequestParam UUIDParam = new RequestParam(MEDIA_ID_KEY, uuid);
		RequestParam TypeParam = new RequestParam(TYPE_KEY,
				Integer.toString(type));
		BinaryRequestParam imageParam = new BinaryRequestParam(DATA_KEY, uuid
				+ "jpg", "application/octet-stream", snap);

		PostPrameters params = new PostPrameters();
		params.addParameter(UserParam);
		params.addParameter(TSParam);
		params.addParameter(TokenParam);
		params.addParameter(UUIDParam);
		params.addParameter(TypeParam);
		params.addParameter(imageParam);

		try {
			Sender.send(params.getBody(), BASE_URL + UPLOAD_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		String SendStr = USERNAME_KEY + "=" + UserName + "&" + TIMESTAMP_KEY
				+ "=" + TimeStamp.toString() + "&" + REQ_TOKEN_KEY + "="
				+ tokengen(Token, TimeStamp).toString() + "&" + MEDIA_ID_KEY
				+ "=" + uuid + "&" + RECIPIENT_KEY + "=" + receiver + "&"
				+ TIME_KEY + "=" + time + "&" + ZIPPED_KEY + "=0";
		try {
			JSONObject confirm = ExecReq(SendStr, BASE_URL + SEND_PATH);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public JSONObject RegisterRequest(int age, String email, String password,
			String bithday) {
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		String SendStr = "age=" + age + "&timestamp=" + TimeStamp
				+ "&req_token=" + tokengen(StaticToken, TimeStamp).toString()
				+ "&email=" + email + "&birthday=" + bithday + "&password="
				+ password + "";
		return ExecReq(SendStr, BASE_URL + "bq/register");
	}

	public byte[] RegisterCaptcha(String email, String authtoken) {
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		String SendStr = "timestamp=" + TimeStamp + "&req_token="
				+ tokengen(authtoken, TimeStamp).toString() + "&username="
				+ email;
		return ExecReqBin(SendStr, BASE_URL + "bq/get_captcha");
	}

	public JSONObject solvecaptcha(String email, String authtoken,
			String Solution, String id) {
		Long TimeStamp = new Long(System.currentTimeMillis());
		String SendStr = "captcha_solution=" + Solution + "&captcha_id=" + id
				+ "&username=" + email + "&timestamp=" + TimeStamp
				+ "&req_token=" + tokengen(authtoken, TimeStamp).toString();
		return ExecReq(SendStr, BASE_URL + "bq/solve_captcha");
	}

	public JSONObject setusername(String email, String authtoken,
			String username) {
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		String SendStr = "selected_username=" + username + "&timestamp="
				+ TimeStamp + "&req_token="
				+ tokengen(authtoken, TimeStamp).toString() + "&username="
				+ email;
		return ExecReq(SendStr, BASE_URL + "bq/register_username");
	}

	public boolean SendStory(byte[] snap, int type, String receiver,
			String UserName, String Token, int time, String caption) {
		String uuid = UserName.toUpperCase() + "~" + generateGUID();
		Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
		RequestParam UserParam = new RequestParam(USERNAME_KEY, UserName);
		RequestParam TSParam = new RequestParam(TIMESTAMP_KEY,
				TimeStamp.toString());
		RequestParam TokenParam = new RequestParam(REQ_TOKEN_KEY, tokengen(
				Token, TimeStamp).toString());
		RequestParam UUIDParam = new RequestParam(MEDIA_ID_KEY, uuid);
		RequestParam TypeParam = new RequestParam(TYPE_KEY,
				Integer.toString(type));
		BinaryRequestParam imageParam = new BinaryRequestParam(DATA_KEY, uuid
				+ "jpg", "application/octet-stream", snap);
		PostPrameters params = new PostPrameters();
		params.addParameter(UserParam);
		params.addParameter(TSParam);
		params.addParameter(TokenParam);
		params.addParameter(UUIDParam);
		params.addParameter(TypeParam);
		params.addParameter(imageParam);
		try {
			Sender.send(params.getBody(), BASE_URL + UPLOAD_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// {
		// username: "youraccount",
		// timestamp: 1373207221,
		// req_token: create_token(auth_token, 1373207221),
		// media_id: "YOURACCOUNT~9c0b0193-de58-4b8d-9a09-60039648ba7f",
		// client_id: "YOURACCOUNT~9c0b0193-de58-4b8d-9a09-60039648ba7f",
		// recipient: "teamsnapchat,someguy",
		// caption_text_display: "Foo, bar, baz!",
		// thumbnail_data: ENCRYPTED_THUMBNAIL_DATA, OPTIONAL BITCHES !!!
		// type: 0,
		// time: 5
		// }

		String SendStr = USERNAME_KEY + "=" + UserName + "&" + TIMESTAMP_KEY
				+ "=" + TimeStamp.toString() + "&" + REQ_TOKEN_KEY + "="
				+ tokengen(Token, TimeStamp).toString() + "&" + MEDIA_ID_KEY
				+ "=" + uuid + "&" + "client_id" + "=" + uuid + "&"
				+ RECIPIENT_KEY + "=" + receiver + "&"
				+ "caption_text_display=" + caption + "&" + TIME_KEY + "="
				+ time + "&" + "type=0";
		ExecReq(SendStr, BASE_URL + "bq/double_post");
		return true;
	}

	public static String generateGUID() {
		Long intiud = new Long(UIDGenerator.getUID() * 100000000000000000L);
		String UID = intiud.toString();
		return UID + "-" + UID.substring(4, 8) + "-" + UID.substring(4, 8)
				+ "-" + UID.substring(3, 7) + "-" + UID.substring(0, 12);
	}

	public int _rc = -1;

	public static String getConnectionString() {

		// read the coverage type
		boolean isSimulator = DeviceInfo.isSimulator();
		boolean isWifi = WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED;
		boolean isMDS = CoverageInfo
				.isCoverageSufficient(CoverageInfo.COVERAGE_MDS);
		boolean isBIS = CoverageInfo
				.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B);
		boolean isDirect = CoverageInfo
				.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT);
		boolean noCoverage = CoverageInfo.getCoverageStatus() == CoverageInfo.COVERAGE_NONE;

		// get the connection string
		String connectionString = "";
		if (isSimulator) {
			connectionString = ";deviceside=false";
		} else if (isWifi) {
			connectionString = ";interface=wifi";
		} else if (isBIS) {
			connectionString = ";deviceside=false;##REMOVED##";
		} else if (isMDS) {
			connectionString = ";deviceside=false";
		} else if (isDirect) {
			String carrierUid = getCarrierBIBSUid();
			if (carrierUid == null) {
				connectionString = ";deviceside=true";
			} else {
				connectionString = ";deviceside=false;ConnectionUID="
						+ carrierUid + ";ConnectionType=mds_public"; 
			}
		} else if (noCoverage) {
			connectionString = "";
		} else {
			connectionString = ";deviceside=true";
		}
		return connectionString;
	}

	private static String getCarrierBIBSUid() {
		ServiceRecord[] records = ServiceBook.getSB().getRecords();
		int currentRecord;
		for (currentRecord = 0; currentRecord < records.length; currentRecord++) {
			if (records[currentRecord].getCid().toLowerCase().equals("ippp")) {
				if (records[currentRecord].getName().toLowerCase()
						.indexOf("bibs") >= 0) {
					return records[currentRecord].getUid();
				}
			}
		}
		return null;
	}

	
	public boolean IsSuccess;
	// Execute la requète JSON et renvoit la réponse du serveur.
	public JSONObject ExecReq(String Request, String REQURL) {
		try {
			HttpConnection c = null;
			InputStream is = null;
			OutputStream os = null;
			int rc = -1;
			MyConnectionFactory factory = new MyConnectionFactory();
			try {
				ConnectionDescriptor connDesc = factory.getConnection(REQURL);
				c = (HttpConnection) connDesc.getConnection();
			} catch (Exception e) {
				try {
					Dialog.inform("Unable to login. Check your network settings.");
					IsSuccess = false;
				} catch (Exception ee ){
					IsSuccess = false;
				}
				IsSuccess = false;
				return null;
			}
			c.setRequestMethod(HttpConnection.POST);
			c.setRequestProperty(JSON_TYPE_KEY, JSON_TYPE);
			c.setRequestProperty(USER_AGENT_KEY, USER_AGENT);
			c.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			byte[] postData = Request.toString().getBytes("UTF-8");
			c.setRequestProperty("Content-Length",
					Integer.toString(postData.length));
			os = c.openOutputStream();
			os.write(postData);
			os.flush();
			os.close();
			rc = c.getResponseCode();
			_rc = rc;
			if (rc != HttpConnection.HTTP_OK
					&& rc != HttpConnection.HTTP_ACCEPTED) {
				IsSuccess = false;
				throw new IOException("HTTP " + rc);
			}
			is = c.openInputStream();
			try {
				byte[] InData = IOUtilities.streamToBytes(is);
				String InString = new String(InData, "UTF-8");
				JSONObject InObj = new JSONObject(InString);
				IsSuccess = true;
				return InObj;
			} catch (Exception E) {
				IsSuccess = false;
				return null;
			}
		} catch (Exception e) {
			// Dialog.inform("Unable to connect. Verify your connection and retry.");
			IsSuccess = false;
			if (e.getMessage().equals("HTTP 415")){
				IsSuccess = false;
				Dialog.inform("Bad File Format.");
			}
			return null;
		}
	}

	public String Content_Disposition = "";

	public boolean SendByte(byte[] request, String REQURL) {
		HttpConnection c = null;
		OutputStream os = null;
		int rc = -1;
		try {
			MyConnectionFactory factory = new MyConnectionFactory();
			factory.setConnectionTimeout(5000L);
			ConnectionDescriptor connDesc = factory.getConnection(REQURL);
			c = (HttpConnection) connDesc.getConnection();
			c.setRequestMethod(HttpConnection.POST);
			c.setRequestProperty(JSON_TYPE_KEY, JSON_TYPE);
			c.setRequestProperty(USER_AGENT_KEY, USER_AGENT);
			c.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			byte[] postData = request;
			c.setRequestProperty("Content-Length",
					Integer.toString(postData.length));
			os = c.openOutputStream();
			os.write(postData);
			os.flush();
			os.close();
			rc = c.getResponseCode();
			if (rc != HttpConnection.HTTP_OK
					&& rc != HttpConnection.HTTP_ACCEPTED) {
				throw new IOException("HTTP " + rc);
			}
			return true;
		} catch (Exception e) {
			Dialog.inform("Unable to connect. Verify your connection and retry.");
			return false;
		}
	}

	public byte[] ExecReqBin(String Request, String REQURL) {
		HttpConnection c = null;
		InputStream is = null;
		OutputStream os = null;
		int rc = -1;
		try {
			MyConnectionFactory factory = new MyConnectionFactory();
			factory.setConnectionTimeout(5000L);
			ConnectionDescriptor connDesc = factory.getConnection(REQURL);
			c = (HttpConnection) connDesc.getConnection();
			c.setRequestMethod(HttpConnection.POST);
			c.setRequestProperty(JSON_TYPE_KEY, JSON_TYPE);
			c.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			byte[] postData = Request.toString().getBytes("UTF-8");
			c.setRequestProperty("Content-Length",
					Integer.toString(postData.length));
			os = c.openOutputStream();
			os.write(postData);
			os.flush();
			os.close();
			rc = c.getResponseCode();

			try {
				Content_Disposition = c.getHeaderField("Content-Disposition");
			} catch (Exception E) {

			}

			if (rc != HttpConnection.HTTP_OK
					&& rc != HttpConnection.HTTP_ACCEPTED) {
				throw new IOException("HTTP " + rc);
			}
			is = c.openInputStream();
			byte[] InData = IOUtilities.streamToBytes(is);
			return InData;
		} catch (Exception e) {
			Dialog.inform("Unable to connect. Verify your connection and retry");
			return null;
		}
	}

	// génération de clés.
	public static String tokengen(String Token, Long TimeStamp) {
		// Create bytesToHex of secret + authToken
		String firstHex = hexDigest(Secret + Token);

		// Create bytesToHex of timestamp + secret
		String secondHex = hexDigest(TimeStamp.toString() + Secret);

		// Combine according to pattern
		StringBuffer sb = new StringBuffer();
		char[] patternChars = Pattern.toCharArray();
		for (int i = 0; i < patternChars.length; i++) {
			char c = patternChars[i];
			if (c == '0') {
				sb.append(firstHex.charAt(i));
			} else {
				sb.append(secondHex.charAt(i));
			}
		}
		return sb.toString();
	}

	private static String hexDigest(String toDigest) {
		SHA256Digest sha256 = new SHA256Digest();
		byte[] todigestedbyte = toDigest.getBytes();

		DigestOutputStream outputStream = new DigestOutputStream(sha256, null);
		try {
			outputStream.write(todigestedbyte);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] digested = sha256.getDigest();
		return bytesToHex(digested);
	}

	private static String bytesToHex(byte[] digested) {
		char[] hexArray = "0123456789abcdef".toCharArray();
		char[] hexChars = new char[digested.length * 2];

		for (int i = 0; i < digested.length; i++) {
			int v = digested[i] & 0xFF;
			hexChars[i * 2] = hexArray[v >>> 4];
			hexChars[(i * 2) + 1] = hexArray[v & 0x0F];
		}
		return (new String(hexChars));
	}
}
// Woot 500 lines !
