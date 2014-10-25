package crapsnap;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

public class NotifyContextStore {

	// Key : MiamHumlolol
	// long : 0x4d6c3a8b79a5a7cdL

	static PersistentObject store;
	static {
		store = PersistentStore.getPersistentObject(0x555c3a8b79a5a7cdL);
	}

	void StoreValues(int Value) {
		synchronized (store) {
			store.setContents(new String[] {Value + ""});
			store.commit();
		}
	}

	int GetStoredValues() {
		try {
			synchronized (store) {
			String[] currentinfo = (String[]) store.getContents();
			if (currentinfo == null) {
				return 300;
			} else {
				return Integer.parseInt(currentinfo[0]);
			}
		}
		}catch (Exception e){
			return 300;
		}
	}
}
