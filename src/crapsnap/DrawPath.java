package crapsnap;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;

public class DrawPath extends Object {
		int[] _xPoints = null;
		int[] _yPoints = null;
		int TRACE_COLOR = Color.YELLOW;
		static int CIRCLE_RADIUS = 7;
		static boolean FILL_MODE = true;

		public DrawPath(int[] xPoints, int[] yPoints) {
			_xPoints = xPoints;
			_yPoints = yPoints;
		}

		public void paint(int lastXPoint, int lastYPoint, Graphics graphics, int currentcolor) {
			TRACE_COLOR = currentcolor;
			int[] paintXpoints = _xPoints;
			int[] paintYpoints = _yPoints;
			if (lastXPoint > 0) {
				paintXpoints = new int[_xPoints.length + 1];
				paintYpoints = new int[_yPoints.length + 1];
				paintXpoints[0] = lastXPoint;
				paintYpoints[0] = lastYPoint;
				for (int i = 1; i < paintXpoints.length; i++) {
					paintXpoints[i] = _xPoints[i - 1];
					paintYpoints[i] = _yPoints[i - 1];
				}
			}
			paintPath(paintXpoints, paintYpoints, graphics);
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

