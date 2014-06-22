import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Listens for changes in datastructures and stores
 * them on the local hard drive.
 * @author sminny
 *
 */
public class DataObserver implements Runnable{
	private PriorityBlockingQueue<Item> pbq;
	private Server server;
	private Item nextInLine;
	private long timeToWait;
	private boolean executing = true;
	public DataObserver(Server server,ArrayList<Item> initial){
		this.server= server;
		pbq = new PriorityBlockingQueue<Item>();
		for(Item i: initial){
			pbq.add(i);
		}
	}
	public synchronized void trackItem(Item item){
		pbq.add(item);
		this.notify();
	}
	public synchronized void removeFromQueue(Item item){
		pbq.remove(item);
		this.notify();
	}
	@Override
	public synchronized void run() {
		if(pbq.isEmpty()){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(executing){
			try {
				nextInLine = pbq.take();
				pbq.add(nextInLine);
				timeToWait = nextInLine.getCloseTime().getTime()-System.currentTimeMillis();
				try{
					this.wait(timeToWait);
				}catch(IllegalArgumentException e){
					server.closeItem(nextInLine);
					pbq.remove(nextInLine);
					continue;
				}
				if(pbq.peek().getCloseTime().compareTo(new Date(System.currentTimeMillis()))<=0){
					server.closeItem(pbq.take());
				}
			} catch (InterruptedException e) {
				break;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	public void interupt() {
		executing = false;
		pbq.clear();
	}

}
