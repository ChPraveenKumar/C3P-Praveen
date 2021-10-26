package com.techm.orion.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techm.orion.utility.C3PCoreAppLabels;

/*Class to store uploaded file on local drive/system*/
@Service
public class StorageService {

	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	/* Root location to store file on system */	
	private final Path rootLocation = Paths.get(C3PCoreAppLabels.IMPORT_FILEPATH.getValue());

	/* Method call to store file */
	public void store(MultipartFile file, String fileNameAsImport) {

		try {

			Files.copy(file.getInputStream(),
					this.rootLocation.resolve(fileNameAsImport.concat("_").concat(file.getOriginalFilename())));

		} catch (Exception e) {

			throw new RuntimeException("FAIL!");

		}

	}

	/* Method call to load file */
	public Resource loadFile(String filename) {

		try {

			Path file = rootLocation.resolve(filename);

			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {

				return resource;

			} else {

				throw new RuntimeException("FAIL!");

			}

		} catch (MalformedURLException e) {

			throw new RuntimeException("FAIL!");

		}

	}

	/*
	 * public void deleteAll() {
	 * 
	 * FileSystemUtils.deleteRecursively(rootLocation.toFile());
	 * 
	 * }
	 */
	/* Method call to initialize file */
	public void init() {

		try {

			Files.createDirectory(rootLocation);

		} catch (IOException e) {

			throw new RuntimeException("Could not initialize storage!");

		}

	}

}
