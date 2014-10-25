package crapsnap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import net.rim.device.api.crypto.AESDecryptorEngine;
import net.rim.device.api.crypto.AESEncryptorEngine;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.PKCS5FormatterEngine;
import net.rim.device.api.crypto.PKCS5UnformatterEngine;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.DataBuffer;

public class Cryptography {
	public static AESKey key = null;
	private static final String AES_KEY = "M02cnQ51Ji97vwT4";

	public byte[] encrypt( byte[] messageBytes)
	{
		try {
			if (key == null)
				key = new AESKey(AES_KEY.getBytes());
			// Now, we want to encrypt the data.
			// First, create the encryptor engine that we use for the actual
			// encrypting of the data.
			AESEncryptorEngine engine = new AESEncryptorEngine(key);
			PKCS5FormatterEngine formatterEngine = new PKCS5FormatterEngine(
					engine);
			// Use the byte array output stream to catch the encrypted
			// information.
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BlockEncryptor encryptor = new BlockEncryptor(formatterEngine,
					outputStream);
			// Encrypt the actual data.
			encryptor.write(messageBytes);
			// Close the stream.
			encryptor.close();
			byte[] encryptedData = outputStream.toByteArray();

			return encryptedData;
		} catch (Exception e) {
			Dialog.inform(e.toString());
			return null;
		}
	}

	public byte[] decrypt(byte[] messageBytes) {
		DataBuffer buffer = new DataBuffer();
		try {
			if (key == null)
				key = new AESKey(AES_KEY.getBytes());
			AESDecryptorEngine engine = new AESDecryptorEngine(key);
			PKCS5UnformatterEngine unformatterEngine = new PKCS5UnformatterEngine(
					engine);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					messageBytes);
			BlockDecryptor decryptor = new BlockDecryptor(unformatterEngine,
					inputStream);
			byte[] temp = new byte[100];
			for (;;) {
				int bytesRead = decryptor.read(temp);
				buffer.write(temp, 0, bytesRead);
				if (bytesRead < 100) {
					break;
				}
			}
			inputStream.close();
			return buffer.toArray();

		} catch (Exception e) {
			try{return buffer.toArray();
					
			} catch (Exception ex) {
			return null;
			}
		}
	}
}