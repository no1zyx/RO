
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.LinkedBlockingQueue;

class Main {
  public class NumbersProducer implements Runnable {
    private BlockingQueue<Integer> numbersQueue;
    private final int poisonPill;
    private final int poisonPillPerProducer;
    
    public NumbersProducer(BlockingQueue<Integer> numbersQueue, int poisonPill, int poisonPillPerProducer) {
        this.numbersQueue = numbersQueue;
        this.poisonPill = poisonPill;
        this.poisonPillPerProducer = poisonPillPerProducer;
    }
    public void run() {
        try {
            generateNumbers();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void generateNumbers() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            this.numbersQueue.put(ThreadLocalRandom.current().nextInt(100));
        }
        for (int j = 0; j < poisonPillPerProducer; j++) {
            this.numbersQueue.put(poisonPill);
        }
     }
  }

  public class NumbersConsumer implements Runnable {
    private BlockingQueue<Integer> queue;
    private final int poisonPill;
    
    public NumbersConsumer(BlockingQueue<Integer> queue, int poisonPill) {
        this.queue = queue;
        this.poisonPill = poisonPill;
    }
    public void run() {
        try {
            while (true) {
                Integer number = queue.take();
                if (number.equals(poisonPill)) {
                    return;
                }
                System.out.println(Thread.currentThread().getName() + " result: " + number);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
  }

  public void start() {
        // can now access non-static fields
    int BOUND = 10;
    int N_PRODUCERS = 4;
    int N_CONSUMERS = Runtime.getRuntime().availableProcessors();
    int poisonPill = Integer.MAX_VALUE;
    int poisonPillPerProducer = N_CONSUMERS / N_PRODUCERS;
    int mod = N_CONSUMERS % N_PRODUCERS;
    
    BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(BOUND);
    
    for (int i = 1; i < N_PRODUCERS; i++) {
        new Thread(new NumbersProducer(queue, poisonPill, poisonPillPerProducer)).start();
    }
    
    for (int j = 0; j < N_CONSUMERS; j++) {
        new Thread(new NumbersConsumer(queue, poisonPill)).start();
    }
    
    new Thread(new NumbersProducer(queue, poisonPill, poisonPillPerProducer + mod)).start();
  }
  
  public static void main(String[] args) {
    System.out.println("Hello world!");

    
    Main programm = new Main();
    programm.start();
    System.out.println("Hello world!");
    
  }
}