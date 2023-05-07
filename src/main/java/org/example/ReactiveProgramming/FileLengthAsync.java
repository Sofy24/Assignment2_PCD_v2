package org.example.ReactiveProgramming;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.example.Utilities.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileLengthAsync {
	public static Flowable<ComputedFile> getFileLengthsAsync(String directory, List<LongRange> ranges) {
		return Flowable.fromCallable(() -> FileSearcher.getJavaSourceFiles(directory))
				.subscribeOn(Schedulers.io())
				.flatMapIterable(files -> files)
				.map(file -> directory + System.getProperty("file.separator") + file)
				.flatMap(file -> Flowable.fromCallable(() -> {
					Long fileLen = Files.lines(Paths.get(file), StandardCharsets.UTF_8).count();
					for (LongRange range: ranges) {
						if (range.isInRange(fileLen))
								return new ComputedFile(new FilePath(file),
										range.getMin(),
										fileLen);
					}
					return null;
				}).subscribeOn(Schedulers.io()));
	}

	public static Flowable<Set<String>> getSubDirectory(String directory) {
		try {
			return Flowable.fromCallable(() -> Stream.of(Objects.requireNonNull(new File(directory).listFiles()))
					.filter(File::isDirectory)
					.map(File::getAbsolutePath)
					.collect(Collectors.toSet()));
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static void main(String[] args) {
		String directory = "C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\file50";
		List<LongRange> ranges = CreateRange.generateRanges(200, 5);
		getSubDirectory(directory).subscribe(
				dirs -> {
					Set<String> directories = new HashSet<>(dirs);
					directories.add(directory);
					directories.forEach(dir -> getFileLengthsAsync(dir, ranges)
							.subscribe(
									computedFile -> System.out.println(computedFile),
									error -> System.err.println("Error: " + error),
									() -> System.out.println(dir+"All files processed")
							)
					);
				},
				error -> System.err.println("Error: " + error),
				() -> System.out.println("ALL FINISHED")
				);

		// Keep the program alive for a while to allow asynchronous processing
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

