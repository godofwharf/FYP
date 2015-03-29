package Queue;

public class MM1Queue { 
	
	private double lambda;
	private double mu;
	
	private double nextArrival;
	private double nextDeparture;
	
	public MM1Queue(double lambda, double mu) {
		this.lambda = lambda;
		this.mu = mu;
		this.nextArrival = StdRandom.exp(lambda); 
		this.nextDeparture = Double.POSITIVE_INFINITY;
	}
	
	public double getNextArrivalTime() {
		return nextArrival += StdRandom.exp(lambda);		
	}
	
	public double getNextDepartureTime() {
		return nextDeparture = nextArrival + StdRandom.exp(mu);
	}

    public static void main(String[] args) { 
        double lambda = Double.parseDouble(args[0]);  // arrival rate
        double mu     = Double.parseDouble(args[1]);  // service rate

        Queue<Double> q = new Queue<Double>();            // arrival times of customers
        double nextArrival   = StdRandom.exp(lambda);     // time of next arrival
        double nextDeparture = Double.POSITIVE_INFINITY;  // time of next departure

        // histogram object
        Histogram hist = new Histogram(60);

        // simulate an M/M/1 queue
        while (true) {

            // it's an arrival
            if (nextArrival <= nextDeparture) {
                if (q.isEmpty()) nextDeparture = nextArrival + StdRandom.exp(mu);
                q.enqueue(nextArrival);
                nextArrival += StdRandom.exp(lambda);
            }

            // it's a departure
            else {
                double wait = nextDeparture - q.dequeue();
                System.out.printf("Wait = %6.2f, queue size = %d\n", wait, q.size());
                hist.addDataPoint(Math.min(60,  (int) (Math.round(wait))));
                hist.draw();
                if (q.isEmpty()) nextDeparture = Double.POSITIVE_INFINITY;
                else             nextDeparture += StdRandom.exp(mu);
                
            }
        }

    }

}
