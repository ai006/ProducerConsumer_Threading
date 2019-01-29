

import java.util.concurrent.Semaphore;
import java.util.*;

public class ProducerConsumer
{

	private static int numInCriticalSection = 1;
	private static Semaphore empty = new Semaphore(5);
    private static Semaphore full = new Semaphore(0);
	private static Semaphore mutex = new Semaphore(numInCriticalSection);
	
	static class Buffer
	{
		ArrayList<Integer> buffer;
		public Buffer(int size)
		{
			buffer = new ArrayList<Integer>(size);
			
		}
		
		public void insert(String name,int x)
		{
			System.out.println(name +" : is inserting : " + x);
			buffer.add(x);
			return;
		}
		
		public void remove(String name)
		{
			int zero = 0;
			System.out.println(name +" : is Removing : " + buffer.get(zero));
			buffer.remove(zero);
			return;
		}
			
		
		
	}
	
	static class Consumer extends Thread 
	{
		private final static Random generator = new Random();
		private String name = "";
		Buffer buffer;
		
		public Consumer(String name, Buffer thatBuffer)
		{
			this.name = name;
			this.buffer = thatBuffer;
		}
		
		@Override
		public void run()
		{
			int count = 0;
			while(count < 100)
			{
				try{
						full.acquire();
						System.out.println(name + " : available Semaphore slots before: " + full.availablePermits());
						System.out.println(name + " : available mutex slots before: " + mutex.availablePermits());
						System.out.println(name + " : acquiring lock.");
						mutex.acquire();
						System.out.println(name + " : got the free slot!");
						
						Thread.sleep(generator.nextInt(3000));
						buffer.remove(this.name);
						
						 System.out.println(name + " : releasing lock.");
						mutex.release();
						System.out.println(name + " : available mutex slots after: " + mutex.availablePermits());
						
						empty.release();
						
				}catch (Exception e){
					e.printStackTrace();
						
				}
				count++;
			}	
		}	
	}
	
	static class Producer extends Thread 
	{
		private String name = "";
		private final static Random generator = new Random();
		Buffer buffer;
		
		public Producer(String name, Buffer thatBuffer)
		{
			this.name = name;
			this.buffer = thatBuffer;
		}
		
		@Override
		public void run()
		{
			int count = 0;
			while(count < 100)
			{
				try{
						empty.acquire();
						System.out.println(name + " : available Semaphore slots before: " + empty.availablePermits());
						System.out.println(name + " : available mutex slots before: " + mutex.availablePermits());
						System.out.println(name + " : acquiring lock.");
						mutex.acquire();
						System.out.println(name + " : got the free slot!");
						
						Thread.sleep(generator.nextInt(3000));
						buffer.insert(this.name,generator.nextInt());
						
						System.out.println(name + " : releasing lock.");
						mutex.release();
						System.out.println(name + " : available mutex slots after: " + mutex.availablePermits());
						
						full.release();
						
				}catch (Exception e){
					e.printStackTrace();
						
				}
				count++;
			}	
		}	
	}

	public static void main(String[] args)	throws InterruptedException 
	{
		int number = 5;
		
		int sleepTime =Integer.parseInt(args[0]);
		int producerThreads =  Integer.parseInt(args[1]);
		int consumerThreads = Integer.parseInt(args[2]);
		
        Producer[] producer = new Producer[producerThreads];
		Consumer[] consumer = new Consumer[consumerThreads];
		Buffer buffer = new Buffer(5);
        
        System.out.println("Semaphore Example");
        System.out.println();
        System.out.println("Total available slots: " + mutex.availablePermits());
        
        // create and start 5 threads of one type
        for(int i=0; i<producerThreads; i++)
		{
            producer[i] = new Producer("Producer "+Integer.toString(i),buffer);
            producer[i].start();
        }
		for(int i=0; i<consumerThreads; i++)
		{
            consumer[i] = new Consumer("Consumer "+Integer.toString(i),buffer);
            consumer[i].start();
        }
		
		//Thread.sleep(sleepTime * 1000);
		//System.exit(0);
		
	}


}