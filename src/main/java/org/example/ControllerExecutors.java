package org.example;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.example.Executors.DirectorySearchTask;
import org.example.Executors.SourceAnalyser;
import org.example.Utilities.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;


public class ControllerExecutors implements InputListener, SourceAnalyser {

	private Flag stopFlag;
	private View view;
	
	public ControllerExecutors(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public synchronized void started(File dir, int nMaxFilesToRank, int nBands, int maxLoc){
		stopFlag.reset();
		
		//var master = new Master(nMaxFilesToRank, nBands, maxLoc, dir, stopFlag, view);
		//master.start();
	}

	public synchronized void stopped() {
		stopFlag.set();
	}

	@Override
	public CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
		List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
		try {
			return CompletableFuture.supplyAsync(() ->
					new Report(new ForkJoinPool().invoke(new DirectorySearchTask(directory, ranges)), ranges, longestFiles));

		} catch (Exception e){
			return null;
		}
	}

	@Override
	public void analyzeSources(String d) {

	}
}
