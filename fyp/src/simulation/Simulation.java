package Simulation;
import java.applet.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.net.URL;



/*
 <applet code="Simulation.class" width=1000 height=500 name="Simulation">
 </applet>
 */
class Node {
	int x;
	int y;
	int delta_plus; /* edge starts from this node */
	int delta_minus;/* edge terminates at this node */
	int dist; /* distance from the start node */
	int prev; /* previous node of the shortest path */
	int p_edge;
	int w;
	int h;
	String name;
}

class Edge {
	int rndd_plus; /* initial vertex of this edge */
	int rndd_minus; /* terminal vertex of this edge */
	int capacity; /* capacity */
	int flow; /* flow */
	int st;
	String name;
}

class Global {
	public static int check = 0;
	public static int ind = 0;
	public static int vert = 0;
	public static int clear = 0;
	public static double Angle;
	public static int info = 0;
	public static int MAXNODES = 100;
	// public static final ReentrantReadWriteLock rw = new
	// ReentrantReadWriteLock();
	// public static final Lock read = rw.readLock();
	// public static final Lock write = rw.writeLock();
}

public class Simulation extends Applet {

	Button start = new Button("Start");
	Button stop = new Button("Reset");
	int j = 0, li = 0;

	// features lists
	Line[] mylines;
	MyChoice length = new MyChoice(
			new String[] { "10 km", "100 km", "1000 km" }, new double[] { 10E3,
					100E3, 1E6 }, 3);
	MyChoice rate = new MyChoice(new String[] { "512 kps", "1 Mbps", "10 Mbps",
			"100 Mbps" }, new double[] { 512E3, 1E6, 10E6, 100E6 }, 2);
	MyChoice size = new MyChoice(new String[] { "100 Bytes", "500 Bytes",
			"1 kBytes" }, new double[] { 8E2, 4E3, 8E3 }, 1);

	MyChoice source, dest1;

	// to simulate time
	Thread timerThread;

	TickTask timerTask;

	// communication line
	Line myLine;
	boolean simulationRunning = false;
	int cx = 0, cy = 0, cw1 = 0, ch1 = 0, cw2 = 0, ch2 = 0;
	int cs = 0;
	int ds1 = 0, ds2 = 0;

	// Line myLine2;
	final static Color LineColor = Color.BLUE;
	final static Color HighlightColor = Color.RED;
	final static Color NodeColor = Color.BLACK;
	final static int NODE_WIDTH = 40; // width of a node
	final static int NODE_HEIGHT = 40; // height of a node

	int n, m, sval = 0, tval = 4;
	int snode, tnode; /* start node, terminate node */
	int step;
	Node v[] = new Node[100];
	Edge e[] = new Edge[200];

	// String FILE_TO_READ="inpfile.txt";
	public void init() {
		int i;

		String mdname = "inpfile.txt";
		
		//setting singleton
		SimulationSingleton.init(this);
		if (mdname != null) {
			System.out.println("file fetched: " + mdname);
		} else {
			System.out.println("file not fetched");
		}
		try {
			InputStream is = new URL(getCodeBase(), mdname).openStream();
			input_graph(is);
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
		} catch (IOException e) {
			System.err.println("Cannot access file.");
		}

		mylines = new Line[m];
		if (sval != 0)
			snode = sval;
		else
			snode = 0;

		if (tval != 0)
			tnode = tval;
		else
			tnode = n - 1;
		setBackground(Color.white);
		source = new MyChoice(n);

		dest1 = new MyChoice(n);
		for (i = 0; i < n; i++)
			source.add(v[i].name);
		for (i = 0; i < n; i++)
			dest1.add(v[i].name);

		add(new Label("Length", Label.RIGHT));
		add(length);
		add(new Label("Rate", Label.RIGHT));
		add(rate);
		add(new Label("Packet size", Label.RIGHT));
		add(size);
		add(new Label("Source node", Label.RIGHT));
		add(source);
		add(new Label("Destination node 1", Label.RIGHT));
		add(dest1);

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				launchSim();
			}
		});
		add(start);
		// stop
		Button stop = new Button("Reset");
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				stopSim();
				// clear line
				mylines[li].sendTime(0);

				// redraw cleared line
				Global.clear = 1;
				Simulation.this.repaint();

			}
		});
		add(stop);
	}

	int findNode(String name) {
		for (int i = 0; i < n; i++)
			if (v[i].name.equals(name))
				return i;
		return -1;
	}

	void input_graph(InputStream is) throws IOException {
		int x, y, l;
		String s;
		StreamTokenizer st = new StreamTokenizer(is);
		st.commentChar('#');
		st.nextToken();
		n = (int) st.nval;
		st.nextToken();
		m = (int) st.nval;
		st.nextToken();
		s = st.sval;

		for (int i = 0; i < n; i++) {
			Node node = new Node();
			st.nextToken();
			node.name = st.sval;
			st.nextToken();
			node.x = (int) st.nval;
			st.nextToken();
			node.y = (int) st.nval;
			v[i] = node;
		}
		for (int i = 0; i < m; i++) {
			Edge edge = new Edge();
			st.nextToken();
			edge.name = st.sval;
			switch (st.nextToken()) {
			case StreamTokenizer.TT_NUMBER:
				edge.rndd_plus = (int) st.nval;
				break;
			case StreamTokenizer.TT_WORD:
				edge.rndd_plus = findNode(st.sval);
				break;
			default:
				break;
			}
			switch (st.nextToken()) {
			case StreamTokenizer.TT_NUMBER:
				edge.rndd_minus = (int) st.nval;
				break;
			case StreamTokenizer.TT_WORD:
				edge.rndd_minus = findNode(st.sval);
				break;
			default:
				break;
			}
			st.nextToken();
			edge.capacity = (int) st.nval;
			edge.flow = 0;
			e[i] = edge;
			v[e[i].rndd_plus].delta_plus = i;
			v[e[i].rndd_minus].delta_minus = i;
		}
		step = 1;
	}

	public void paint(Graphics g) {
		System.out.println("paint called");
		update(g);
	}

	public void update(Graphics g) {
		System.out.println("updating");
		if (Global.info != 0) {
			Dimension d = getSize();
			Font font = new Font("Arial", Font.PLAIN, 14);
			g.setFont(font);
			g.setColor(Color.BLACK);
			g.drawString("Source is " + v[cs].name, d.width - 200,
					d.height - 500);
			g.drawString("Destination is " + v[ds1].name, d.width - 200,
					d.height - 400);
		}
		if (Global.clear == 1) {
			Dimension d = getSize();
			g.setColor(Color.white);
			g.fillRect(0, 0, d.width, d.height);
			Global.clear = 0;
			Global.check = 0;
		}
		FontMetrics fm = g.getFontMetrics();
		for (int i = 0; i < n; i++)
			paintNode(g, v[i], fm);
		for (int i = 0; i < m; i++)
			paintEdge(g, e[i], fm);
		if (Global.check != 0) {
			myLine.drawLine(g);

		}
	}

	public void paintNode(Graphics g, Node n, FontMetrics fm) {
		String s;
		int x = n.x;
		int y = n.y;
		int w = fm.stringWidth(n.name) + 10;
		int h = fm.getHeight() + 4;
		n.w = w;
		n.h = h;
		Color c;
		if (n.dist < 0)
			c = Color.GRAY;
		else
			c = Color.BLACK;
		g.setColor(c);
		g.drawRect(x - w / 2, y - h / 2, w + 3, h + 3);
		g.setColor(Color.BLACK);
		g.fillRect(x - w / 2 + 1, y - h / 2 + 1, w - 1 + 3, h - 1 + 3);
		g.setColor(Color.WHITE);
		g.drawString(n.name, x - (w - 10) / 2,
				(y - (h - 4) / 2) + fm.getAscent());
	}

	int[] xy(int a, int b, int w, int h) {
		int x[] = new int[2];
		if (Math.abs(w * b) >= Math.abs(h * a)) {
			x[0] = ((b >= 0) ? 1 : -1) * a * h / b / 2;
			x[1] = ((b >= 0) ? 1 : -1) * h / 2;
		} else {
			x[0] = ((a >= 0) ? 1 : -1) * w / 2;
			x[1] = ((a >= 0) ? 1 : -1) * b * w / a / 2;
		}
		return x;
	}

	public void paintEdge(Graphics g, Edge e, FontMetrics fm) {
		Node v1 = v[e.rndd_plus];
		Node v2 = v[e.rndd_minus];
		Color c;

		int a = v1.x - v2.x;
		int b = v1.y - v2.y;

		int x1[] = xy(-a, -b, v1.w, v1.h);
		int x2[] = xy(a, b, v2.w, v2.h);

		if (e.st > 0)
			c = Color.red;
		else if ((v1.dist >= 0) && (v2.dist >= 0))
			c = Color.blue;
		else
			c = Color.gray;
		g.setColor(c);
		drawArrow(g, v1.x + x1[0], v1.y + x1[1], v2.x + x2[0], v2.y + x2[1]);
	}

	void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
		int a = x1 - x2;
		int b = y1 - y2;
		int temp, temp1;
		;
		double aa = Math.sqrt(a * a + b * b) / 16.0;
		double bb = b / aa;
		aa = a / aa;
		g.setColor(Color.blue);
		g.drawLine(x2, y2, x2 + (int) ((aa * 12 + bb * 5) / 13), y2
				+ (int) ((-aa * 5 + bb * 12) / 13));

		g.drawLine(x2, y2, x2 + (int) ((aa * 12 - bb * 5) / 13), y2
				+ (int) ((aa * 5 + bb * 12) / 13));
		g.drawLine(x1, y1, x2, y2);
	}

	class MyChoice extends Choice {
		private double vals[];

		public MyChoice(String items[], double values[], int defaultValue) {
			for (int i = 0; i < items.length; i++) {
				super.addItem(items[i]);
			}
			vals = values;
			select(defaultValue - 1);
		}

		public MyChoice(int n) {
			vals = new double[n];
			for (int i = 0; i < n; i++)
				vals[i] = ((double) i);
		}

		public double getVal() {
			return vals[super.getSelectedIndex()];
		}
	}

	// tickTask
	class TickTask implements Runnable {
		private double counter;
		private double length;
		private double tick;

		public TickTask(double t, double l) {
			length = l;
			tick = t;
			counter = 0;
		}

		public void run() {
			while (Simulation.this.simulationRunning) {
				counter += tick;
				// System.out.println("yes");
				Simulation.this.myLine.sendTime(counter);
				Simulation.this.repaint();
				if (counter >= length) {
					Simulation.this.myLine.clearPackets();
					Simulation.this.timerThread.suspend();
					// Global.read.unlock();
					// Global.next=1;
				}

				try {
					Simulation.this.timerThread.sleep(50);

				} catch (Exception e) {
				}
			}
		}

		public void endNow() {
			length = counter;
		}
	}

	private void launchSim() {
		Global.check = 1;
		cs = (int) source.getVal();
		ds1 = (int) dest1.getVal();
		// ds2=(int)dest2.getVal();
		Global.info = 1;
		for (int i = 0; i < m; i++) {
			if (ds1 == e[i].rndd_minus) {
				getpath(i);
				break;
			}
		}
		System.out.println("no of paths: " + (j));
		setupEnabled(false);
		//setup transmission
		TransmitThread tt= new TransmitThread();
		tt.init();
		Thread t = new Thread(tt);
		simulationRunning=true;
		t.start();
	}

	private void stopSim() {
		timerTask.endNow();

		simulationRunning = false;
		setupEnabled(true);
	}

	void getpath(int i) {
		int prev = e[i].rndd_plus;
		if (prev == cs) {
			int tempyy = e[i].rndd_minus;
			System.out.println(" " + v[cs].name + "-->" + v[tempyy].name);
			fixPosDim(cs, tempyy);

		} else {
			int tempx = prev, tempy = e[i].rndd_minus;
			getpath(v[prev].delta_minus);
			System.out.println(" " + v[tempx].name + "-->" + v[tempy].name);
			fixPosDim(tempx, tempy);
		}
	}

	void fixPosDim(int cs, int ds1) {

		int tempx1 = 0, tempy1 = 0, tempx2 = 0, tempy2 = 0;
		cx = v[cs].x;
		cy = v[cs].y;
		tempx1 = v[ds1].x;
		tempy1 = v[ds1].y;

		if ((cx < tempx1) && (cy == tempy1)) {

			cw1 = tempx1 - cx;
			ch1 = 10;
			mylines[j] = new Line(cx, cy, cw1, ch1, 0);
			System.out.println("path " + j + ": from " + mylines[j].gX + " "
					+ mylines[j].gY + " to " + tempx1 + " " + tempy1);
			j++;
		}

		else if ((cx == tempx1) && (cy > tempy1)) {

			Global.vert = 1;
			Line2D.Double line1 = new Line2D.Double(cx, cy, cx + 30, cy);
			Line2D.Double line2 = new Line2D.Double(cx, cy, tempx1, tempy1);

			Global.Angle = angleBetween2Lines(line1, line2);
			ch1 = 10;
			cw1 = cy - tempy1;
			mylines[j] = new Line(cx, cy, cw1, ch1, 1);
			System.out.println("path " + j + ": from " + mylines[j].gX + " "
					+ mylines[j].gY + " to " + tempx1 + " " + tempy1);
			j++;
		} else if ((cx == tempx1) && (cy < tempy1)) {

			Global.vert = 2;
			Line2D.Double line1 = new Line2D.Double(cx, cy, cx + 30, cy);
			Line2D.Double line2 = new Line2D.Double(cx, cy, tempx1, tempy1);

			Global.Angle = angleBetween2Lines(line1, line2);
			ch1 = 10;
			cw1 = tempy1 - cy;
			mylines[j] = new Line(cx, cy, cw1, ch1, 2);
			System.out.println("path " + j + ": from " + mylines[j].gX + " "
					+ mylines[j].gY + " to " + tempx1 + " " + tempy1);
			j++;
		} else if ((cx < tempx1) && (cy < tempy1)) {

			cw1 = tempx1 - cx;
			ch1 = 10;
			Global.ind = 1;
			Line2D.Double line1 = new Line2D.Double(cx, cy, cx + 30, cy);
			Line2D.Double line2 = new Line2D.Double(cx, cy, tempx1, tempy1);
			Global.Angle = angleBetween2Lines(line1, line2);
			mylines[j] = new Line(cx, cy, cw1, ch1, 3);
			System.out.println("path " + j + ": from " + mylines[j].gX + " "
					+ mylines[j].gY + " to " + tempx1 + " " + tempy1);
			j++;
		} else if ((cx < tempx1) && (cy > tempy1)) {

			cw1 = tempx1 - cx;
			ch1 = 10;
			Global.ind = 2;
			Line2D.Double line1 = new Line2D.Double(cx, cy, cx + 30, cy);
			Line2D.Double line2 = new Line2D.Double(cx, cy, tempx1, tempy1);
			Global.Angle = angleBetween2Lines(line1, line2);

			mylines[j] = new Line(cx, cy, cw1, ch1, 4);
			System.out.println("path " + j + ": from " + mylines[j].gX + " "
					+ mylines[j].gY + " to " + tempx1 + " " + tempy1);
			j++;
		}

	}

	public static double angleBetween2Lines(Line2D line1, Line2D line2) {
		double angle1 = Math.atan2(line1.getY1() - line1.getY2(), line1.getX1()
				- line1.getX2());
		double angle2 = Math.atan2(line2.getY1() - line2.getY2(), line2.getX1()
				- line2.getX2());
		return angle1 - angle2;
	}

	public void setupEnabled(boolean value) {
		start.setEnabled(value);
		length.setEnabled(value);
		rate.setEnabled(value);
		size.setEnabled(value);
		source.setEnabled(value);
	}
}

class Packet {
	double size;
	double emissionTime;

	Packet(double s, double eT) {
		size = s;
		emissionTime = eT;
	}
}

class Line {
	// graphic variables
	public int gX;
	public int gY;
	public int gWidth;
	public int gHeight;
	public int no;
	private Packet myPacket;
	int inc = 0;
	// characteristic variables
	final double celerity = 2.8E+8;
	private double length;
	private double rate;
	// simulation variables
	private double time;

	public Line(int x, int y, int w, int h) {
		// graphic init
		gX = x;
		gY = y;
		gWidth = w;
		gHeight = h;

	}

	public Line(int x, int y, int w, int h, int sno) {
		gX = x;
		gY = y;
		gWidth = w;
		gHeight = h;
		no = sno;

	}

	public void setup(double l, double r) {
		length = l;
		rate = r;
	}

	void sendTime(double now) {
		time = now; // update time
		removeReceivedPackets(now);
		
	}

	void emitPacket(double s, double eT) {
		myPacket = new Packet(s, eT);
	}

	private void removeReceivedPackets(double now) {
		if (!(myPacket == null)) {
			if (now > myPacket.emissionTime + (myPacket.size / rate) + length
					* celerity) {
				clearPackets();
			}
		}
	}

	public void clearPackets() {
		// System.out.println("entered clearpackets function");
		myPacket = null;
	}

	public double totalTime() {
		double emmissionTime = (myPacket.size / rate);
		double onLineTime = (length / celerity);
		return (emmissionTime + onLineTime);
	}

	public void drawLine(Graphics g) {
		System.out.println(no);
		if (no == 3) {
			System.out.println("entered diagonal downward");
			g.setColor(Color.black);
			Graphics2D gr = (Graphics2D) g;
			Rectangle rect = new Rectangle(gX, gY, gWidth, gHeight);

			gr.translate(gX, gY);
			gr.rotate(-Global.Angle);
			gr.translate(-gX, -gY);
			gr.setColor(Color.white);
			gr.fill(rect);
			gr.setColor(Color.black);
			gr.draw(rect);
			drawPackets(g);
		} else if (no == 4) {
			System.out.println("entered diagonal upward");
			Graphics2D gr = (Graphics2D) g;
			Rectangle rect = new Rectangle(gX, gY, gWidth, gHeight);

			gr.translate(gX, gY);
			gr.rotate(Global.Angle);
			gr.translate(-gX, -gY);
			gr.setColor(Color.white);
			gr.fill(rect);

			gr.setColor(Color.black);
			gr.draw(rect);
			drawPackets(g);
		} else if ((no == 1) || (no == 2)) {
			System.out.println("entered vertical");
			Graphics2D gr = (Graphics2D) g;
			Rectangle rect = new Rectangle(gX, gY, gWidth, gHeight);
			Rectangle rect1 = new Rectangle(gX, gY + 1, gWidth, gHeight - 2);
			gr.translate(gX, gY);
			gr.rotate(-Global.Angle);
			gr.translate(-gX, -gY);
			gr.setColor(Color.white);
			gr.fill(rect1);

			gr.setColor(Color.black);
			gr.draw(rect);
			drawPackets(g);

		} else {
			System.out.println("entered horizontal");
			g.setColor(Color.white);
			g.fillRect(gX, gY, gWidth, gHeight);

			g.setColor(Color.black);
			g.drawRect(gX, gY, gWidth, gHeight);

			drawPackets(g);
		}

	}


	private void drawPackets(Graphics g) {

		if (!(myPacket == null)) {

			double xfirst;
			double xlast;
			// compute time units
			xfirst = time - myPacket.emissionTime;
			xlast = xfirst - (myPacket.size / rate);
			// compute position
			xfirst = xfirst * celerity * gWidth / length;
			xlast = xlast * celerity * gWidth / length;
			if (xlast < 0) {
				xlast = 0;
			}
			if (xfirst > gWidth) {
				xfirst = gWidth;
			}
			// draw
			g.setColor(Color.red);

			g.fillRect(gX + (int) (xlast), gY + 1, (int) (xfirst - xlast),
					gHeight - 2);

		}
	}

	private String timeToString(double now) {
		String res = Double.toString(now * 1000);
		int dot = res.indexOf('.');
		String deci = res.substring(dot + 1) + "000";
		deci = deci.substring(0, 3);
		String inte = res.substring(0, dot);
		return inte + "." + deci + " ms";
	}
}
