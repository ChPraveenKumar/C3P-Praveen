package com.techm.orion.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
@Component
public class UtilityMethods {

	private static final Logger logger = LogManager.getLogger(UtilityMethods.class);
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) 
	  {
	    final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();
	     
	    return t -> 
	    {
	      final List<?> keys = Arrays.stream(keyExtractors)
	                  .map(ke -> ke.apply(t))
	                  .collect(Collectors.toList());
	       
	      return seen.putIfAbsent(keys, Boolean.TRUE) == null;
	    };
	  }
	
	public static String readFirstLineFromFile(String path) throws IOException {
		 String line = null;
	     StringBuilder lineData = new StringBuilder();
	    try (BufferedReader br =
	                   new BufferedReader(new FileReader(path))) {
	    	 while ((line = br.readLine()) != null) {
	    		 lineData.append(line);
	            }
	        return lineData.toString();
	    }
	}

	public static void sleepThread(int time) {		
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				logger.error("Exception occure at the time of Thread sleep");
			}			
	}
}
