package crapsnap;

import java.util.Vector;

class DrawPointsVector extends Vector {
public DrawPointsVector(int initialSize) {
	super(initialSize);
}

public void addDrawItem(DrawPath object) {
	synchronized (this) {
		this.addElement(object);
	}
}

public void addDrawItem(DrawPoint object) {
	synchronized (this) {
		this.addElement(object);
	}
}

public Object getNext() {
	Object returnElement = null;
	synchronized (this) {
		if (this.size() > 0) {
			returnElement = this.elementAt(0);
			this.removeElementAt(0);
		}
	}
	return returnElement;
}
}
