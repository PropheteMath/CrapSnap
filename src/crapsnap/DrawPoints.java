package crapsnap;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;


class DrawPoint extends Object {
	int _xPoint = -1;
	int _yPoint = -1;
	 int TRACE_COLOR = Color.BLACK;
	static int CIRCLE_RADIUS = 7;
	static boolean FILL_MODE = true;

	public DrawPoint(int xPoint, int yPoint) {
		_xPoint = xPoint;
		_yPoint = yPoint;
	}
	

	public void paint(int lastXPoint, int lastYPoint, Graphics graphics, int currentcolor) {
		TRACE_COLOR = currentcolor;
		if (_xPoint < 0) {
			// Just a break....
			if (lastXPoint >= 0) {
				paintPoint(lastXPoint, lastYPoint, graphics);
			}
		} else if (lastXPoint >= 0) {
			System.out.println("Painting Point on Path");
			paintPath(new int[] { lastXPoint, _xPoint },
					new int[] { lastYPoint, _yPoint }, graphics);
		} else {
			paintPoint(_xPoint, _yPoint, graphics);
		}

	}

	public int getLastX() {
		return _xPoint;
	}

	public int getLastY() {
		return _yPoint;
	}

	 void paintPoint(int x, int y, Graphics graphics) {
		System.out.println("Painting Point - at: " + x + ":" + y);
		if (FILL_MODE) {
			graphics.setColor(TRACE_COLOR);
			graphics.fillArc(x, y, CIRCLE_RADIUS,
					CIRCLE_RADIUS, 0, 360);
			graphics.setColor(0x00000000);
		} else {
			graphics.setColor(TRACE_COLOR);
			graphics.drawPoint(x, y);
		}
	}

	 void paintPath(int[] xPoints, int[] yPoints,
				Graphics graphics) {
			System.out.println("Painting Points - from: " + xPoints[0] + ":"
					+ yPoints[0] + ", to: " + xPoints[xPoints.length - 1] + ":"
					+ yPoints[yPoints.length - 1]);
			if (FILL_MODE) {
				try {
					graphics.setColor(TRACE_COLOR);
					graphics.fillArc(xPoints[0], yPoints[0],
							CIRCLE_RADIUS,
							CIRCLE_RADIUS, 0, 360);
					for (int i = 0; i < xPoints.length - 1; i++) {
						/*
						 * if ( (i / 2) * 2 == i ) {
						 * graphics.setColor(0x00FF0000); } else {
						 * graphics.setColor(0x0000FF00); }
						 */
						int xDist = xPoints[i + 1] - xPoints[i];
						int yDist = yPoints[i + 1] - yPoints[i];
						int steps = ((Math
								.max(Math.abs(xDist), Math.abs(yDist)) + CIRCLE_RADIUS) / CIRCLE_RADIUS) * 3;
						int xBasePoint = xPoints[i];
						int yBasePoint = yPoints[i];
						int xPoint, yPoint, xOffset, yOffset;
						for (int j = 1; j < steps; j++) {
							xOffset = (xDist * j) / steps;
							yOffset = (yDist * j) / steps;
							xPoint = xBasePoint + xOffset;
							yPoint = yBasePoint + yOffset;
							graphics.fillArc(xPoint, yPoint,
									CIRCLE_RADIUS,
									CIRCLE_RADIUS, 0, 360);
						}
					}
					graphics.setColor(0x00000000);
				} catch (Exception e) {
					System.out.println(e.toString());
				}
			} else {
				graphics.drawPathOutline(xPoints, yPoints, null, null, false);
			}
}
}