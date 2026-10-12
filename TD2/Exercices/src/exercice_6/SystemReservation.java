package exercice_6;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SystemReservation {
	private int MAX_PLACES = 100;
	private int[] places;	// -1 = libre, sinon id du propriétaire
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	
	public SystemReservation(int nb) {
		 places = new int[nb];
	      for (int i = 0; i < nb; i++) places[i] = -1;
	}
	
	// places[i] : 1--> occupe, 0-->libre
	public  boolean isAvailable(int place) {
		lock.readLock().lock();
		try {
            return places[place] == -1;
        } finally {
            lock.readLock().unlock();
        }
	}
	
	public boolean reserver(int place, int userId)  {
		lock.writeLock().lock();
		try {
			while(places[place] != -1) {
				// pas de wait et notify avec les locks
				
				return false;
			}
			this.places[place] = userId;
			return true;
		
		} finally{
			lock.writeLock().unlock();
		}
	}
	
	public boolean cancel(int place, int userId) {
		lock.writeLock().lock();
		try {
			this.places[place] = -1;
			return true; 
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	
	public int getOwner(int place) {
		lock.readLock().lock();
		try {
			return places[place];
		} finally {
		lock.readLock().unlock();
		}
	}
	
	public int size() {return places.length;}
	
}
