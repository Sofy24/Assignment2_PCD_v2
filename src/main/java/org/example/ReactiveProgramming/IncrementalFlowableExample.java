package org.example.ReactiveProgramming;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

public class IncrementalFlowableExample {
	private static int lastProcessedValue = 0;

	public static void main(String[] args) {
		Flowable<Integer> flowable = Flowable.range(1, 1000)
				.filter(value -> value > lastProcessedValue);  // Skip events until the last processed value

		Disposable disposable = flowable.subscribe(
				value -> {
					// Handle onNext event
					System.out.println(value);
					lastProcessedValue = value;  // Update the last processed value
				},
				error -> {
					// Handle onError event
					System.out.println("Error: " + error.getMessage());
				},
				() -> {
					// Handle onComplete event
					System.out.println("Completed");
				}
		);

		// Stop the subscription and cancel the flow of events
		disposable.dispose();
	}
}
