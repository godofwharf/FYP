package Simulation;



public class TransmitThread implements Runnable{

	
	private Simulation simulation;
	private double counter;
	private double length;
	private double tick;
	
	private Line[] lines;
	
	public void init() {
		simulation = SimulationSingleton.getSim();
		lines = simulation.mylines;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		for(int i=0; i<lines.length;i++) {
			counter = 0;
			tick = 1E-5;
			simulation.myLine = lines[i];
			if(simulation.myLine == null) System.out.println("Yes");
			simulation.myLine.setup(simulation.length.getVal(), simulation.rate.getVal() );
			simulation.myLine.emitPacket(simulation.size.getVal(),0);
			length = lines[i].totalTime();
			while (simulation.simulationRunning) {
				counter += tick;
				simulation.myLine.sendTime(counter);
				simulation.repaint();
				if (counter >= length) {
					simulation.myLine.clearPackets();
					break;
				}

				try {
					Thread.currentThread().sleep(50);

				} catch (Exception e) {
				}
			}
		}
			
	}

}
