package run;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sum {

	private static int[] numbers;

	static {
		Random random = new Random(514);
		numbers = new int[10000];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = random.nextInt(500);
		}
	}

	private static class Task implements Runnable {

		private final int initialIndex;
		private final int finalIndex;

		private long result;

		public Task(int initialIndex, int finalIndex) {
			this.initialIndex = initialIndex;
			this.finalIndex = finalIndex;
		}

		@Override
		public void run() {
			for (int i = initialIndex; i < finalIndex; i++) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result += numbers[i];
			}
		}

		long getResult() {
			return result;
		}

	}

	public static void main(String[] args) throws InterruptedException {

		Thread bobona = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("Sem nada para fazer");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}, "Bobona");

		bobona.setDaemon(true);
		bobona.start();

		long initialTime = System.currentTimeMillis();
		long sum = 0;
		for (int i = 0; i < numbers.length; i++) {
			Thread.sleep(1);
			sum += numbers[i];
		}
		long endTime = System.currentTimeMillis() - initialTime;
		System.out.println("Duração sequencial: " + endTime);
		System.out.println("Resultado sequencial: " + sum);

		initialTime = System.currentTimeMillis();

		int numberOfTasks = 10;
		int numberOfElements = numbers.length / numberOfTasks;
		int initialIndex = 0;

		List<Task> tasks = new ArrayList<Task>();
		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 1; i <= numberOfTasks; i++) {
			final int finalIndex;
			if (i == numberOfTasks) {
				finalIndex = numbers.length;
			} else {
				finalIndex = initialIndex + numberOfElements;
			}
			final Task task = new Task(initialIndex, finalIndex);
			tasks.add(task);
			initialIndex = finalIndex;

			Thread thread = new Thread(task, "Trabalhador " + i);
			threads.add(thread);
			thread.start();
		}

		for (Thread t : threads) {
			t.join();
		}

		long result = 0;
		for (Task t : tasks) {
			result += t.getResult();
		}

		endTime = System.currentTimeMillis() - initialTime;
		System.out.println("Duração paralela: " + endTime);
		System.out.println("Resultado paralela: " + result);
	}

}