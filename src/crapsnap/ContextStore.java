package crapsnap;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

public class ContextStore {

	// Key : MiamHumlolol
	// long : 0x4d6c3a8b79a5a7cdL

	static PersistentObject store;
	static {
		store = PersistentStore.getPersistentObject(0x4d6c3a8b79a5a7cdL);
	}

	void StoreValues(String[] Values) {
		synchronized (store) {
			store.setContents(Values);
			store.commit();
		}
	}

	String[] GetStoredValues() {
		try {
			synchronized (store) {
			String[] currentinfo = (String[]) store.getContents();
			if (currentinfo == null) {
				return null;
			} else {
				return currentinfo;
			}
		}
		}catch (Exception e){
			return null;
		}
	}
}
