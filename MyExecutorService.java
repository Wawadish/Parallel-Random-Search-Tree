package question1;

import java.util.ArrayList;
import java.util.concurrent.*;

// Creates workers that poll tasks via a custom concurrency control mechanism to minimize synchronization overhead.
public class MyExecutorService implements Executor {
    // Each node has a queue (the queue will most 1 node, although this is not strictly enforced), we are not aware
    // of the exact node, but we know that all nodes have ids [0, n[, therefore we initialize a read-only array of
    // size n where each  "queue" in the array represents a task the concurrent queue does not block when the queue
    // is empty returns null instead.
    ArrayList<ConcurrentLinkedDeque<ExpansionTask>> expansionTasks = new ArrayList<>(q1.maxTreeSize);

    // Workers constantly poll for random node tasks, they keep count of their local number of tasks processed
    // which is later aggregated when the executor is shutdown.
    ArrayList<Worker> workers = new ArrayList<>();
    ArrayList<Thread> threads = new ArrayList<>();

    // When set to false, stops the execution.
    public volatile boolean isActive = true;

    // Should only be used to add the root node
    @Override
    public void execute(Runnable command) {
        ExpansionTask task = (ExpansionTask) command;
        expansionTasks.get(task.aNodeToExpand.id).add(task);
    }

    public MyExecutorService(int numThreads) {
        // Initialize node "queues" (will contain a single task at any given point in time)
        for (int i = 0; i < q1.maxTreeSize; i++) {
            expansionTasks.add(new ConcurrentLinkedDeque<>());
        }
        for (int i = 0; i < numThreads; i++) {
            workers.add(new Worker());
            threads.add(new Thread(workers.get(i)));
        }
        threads.forEach(thread -> thread.start());
    }

    // Get task count and join threads
    public void shutdown() {
        isActive = false;
        int totalTasks = 0;
        for (Worker w : workers) {
            totalTasks += w.getTasksProcessed();
        }
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Total tasks: " + totalTasks);
    }


    // Poll and execute random expansion tasks. Keep track of tasks processed.
    public class Worker implements Runnable {

        private volatile int tasksProcessed = 0;

        public int getTasksProcessed() {
            return tasksProcessed;

        }

        @Override
        public void run() {
            while (isActive) {
                ExpansionTask task = getRandomNodeExpansionTask();
                if (task == null) continue;
                try {
                    processTask(task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tasksProcessed++;
            }
        }

        private void processTask(ExpansionTask task) throws Exception {
            ExpansionTaskResult result = task.call();

            if (result.oldNode.size() < Node.aMaxBranchingFactor) {
                expansionTasks.get(result.oldNode.id).add(new ExpansionTask(result.oldNode));
            }

            if (result.newNode == null) {
                return;
            }

            if (result.newNode.id >= q1.maxTreeSize) {
                isActive = false;
                return;
            }
            expansionTasks.get(result.newNode.id).add(new ExpansionTask(result.newNode));

        }

        private ExpansionTask getRandomNodeExpansionTask() {
            int size = Node.treeSize.get();
            if(size >= q1.maxTreeSize){
                isActive = false;
                return null;
            }
            int index = ThreadLocalRandom.current().nextInt(size);
            return expansionTasks.get(index).pollFirst();
        }

    }


}
