package com.techm.c3p.core.validator.config.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.common.net.InetAddresses;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.techm.c3p.core.entitybeans.CSVHeaderCOBEntity;
import com.techm.c3p.core.repositories.CSVHeaderCOBRepository;
import com.techm.c3p.core.repositories.ErrorValidationRepository;

/*Class to validate file against predefine set of rules like file format, mandatory column name and position, mandatory values*/
@Service
public class ExcelFileValidation {
	
	@Autowired
	public ErrorValidationRepository errorValidationRepository;
	@Autowired
	CSVHeaderCOBRepository csvHeader;
	
	private static final Logger logger = LogManager.getLogger(ExcelFileValidation.class);

	/* Method call to validate column of xlsx file */
	public String validateColumnXLSX(Resource filePath) throws EncryptedDocumentException, InvalidFormatException {
		String jsonArray = "";
		try {

			boolean flag = false;
			File SAMPLE_XLSX_FILE_PATH = filePath.getFile();

			if (!SAMPLE_XLSX_FILE_PATH.exists()) {
				SAMPLE_XLSX_FILE_PATH.createNewFile();
			}
			Workbook workbook = WorkbookFactory.create(SAMPLE_XLSX_FILE_PATH);

			Iterator<Sheet> sheetIterator = workbook.sheetIterator();
			boolean isChecked = true;
			while (sheetIterator.hasNext()) {
				Sheet sheet1 = sheetIterator.next();

				Sheet sheet = workbook.getSheetAt(0);
				Row row;

				if (isChecked) {

					row = (Row) sheet.getRow(0);

					/*
					 * for (Row row1 : sheet) { for (Cell cell : row1) {
					 * 
					 * //String scheduled = cell.getRichStringCellValue().toString();
					 * 
					 * if(cell.getRichStringCellValue().toString().equalsIgnoreCase
					 * ("To Be Scheduled")) { int i = cell.getColumnIndex();
					 * 
					 * 
					 * 
					 * 
					 * } } }
					 */

					String cell0 = row.getCell(0).getStringCellValue();

					if (cell0.equals("SR#")) {
						flag = true;
					} else {

						flag = false;
						break;

					}
					/*
					 * String cell1 = row.getCell(1).getStringCellValue();
					 * 
					 * if (cell1.equals("Request Number")) { flag = true; } else { flag = false;
					 * break; } String cell2 = row.getCell(2).getStringCellValue();
					 * 
					 * if (cell2.equals("Hostname")) { flag = true; } else { flag = false; break; }
					 */
					String cell3 = row.getCell(3).getStringCellValue();

					if (cell3.equals("Customer Name*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}
					String cell4 = row.getCell(4).getStringCellValue();

					if (cell4.equals("Site ID*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}
					String cell5 = row.getCell(5).getStringCellValue();

					if (cell5.equals("Region*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}
					String cell6 = row.getCell(6).getStringCellValue();

					if (cell6.equals("Vendor*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}
					String cell7 = row.getCell(7).getStringCellValue();

					if (cell7.equals("Device Type*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}
					String cell8 = row.getCell(8).getStringCellValue();

					if (cell8.equals("Model*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}
					String cell9 = row.getCell(9).getStringCellValue();

					if (cell9.equals("OS*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}
					String cell10 = row.getCell(10).getStringCellValue();

					if (cell10.equals("OS Version*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}
					String cell11 = row.getCell(11).getStringCellValue();

					if (cell11.equals("Management IP*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}

					String cell12 = row.getCell(12).getStringCellValue();

					if (cell12.equals("Service*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}

					String cell13 = row.getCell(13).getStringCellValue();

					if (cell13.equals("Network Type*")) {
						flag = true;
					} else {
						flag = false;
						break;
					}

					isChecked = false;
				}

			}
			if (flag) {
				jsonArray = "Valid";
			} else {
				jsonArray = "Invalid";
			}

		} catch (IOException e) {

			logger.error("Exception in validateColumnXLSX method "+e.getMessage());
		}

		return jsonArray;

	}

	/* Method call to validate single or bulk SR */
	public String validateNoOfRequestXLSX(Resource filePath) throws EncryptedDocumentException, InvalidFormatException {
		String jsonArray = "";

		try {
			File SAMPLE_XLSX_FILE_PATH = filePath.getFile();

			Workbook workbook = WorkbookFactory.create(SAMPLE_XLSX_FILE_PATH);
			String fileExtension = FilenameUtils.getExtension(filePath.getFilename());

			Sheet sheet = workbook.getSheetAt(0);
			Row row;

			int ctr = 1;

			int noOfRows = 0;
			Cell cell = null;
			boolean isNull = true;
			boolean isFlag = false;
			boolean isCheck = true;
			while (isNull) {
				if ((row = sheet.getRow(ctr)) == null) {
					noOfRows = ctr - 1;

					isNull = false;
					break;
				} else if ((row = sheet.getRow(ctr)) != null) {
					cell = row.getCell(0);

					if (cell.toString().isEmpty()) {

						noOfRows = ctr - 1;

						isNull = false;
						break;

					}

					else {
						ctr++;
					}
				}

			}

			while (isCheck) {
				try {
					/* Iterating over single SR */
					if ((noOfRows) == 1) {

						row = sheet.getRow(noOfRows);
						cell = row.getCell(0);
						String cell3 = row.getCell(3).getStringCellValue();
						if (cell3 != null) {
							isFlag = true;

						} else {
							isFlag = false;

							break;
						}
						String cell4 = row.getCell(4).getStringCellValue();
						if (cell4 != null) {
							isFlag = true;

						} else {
							isFlag = false;

							break;
						}
						String cell5 = row.getCell(5).getStringCellValue();
						if (cell5 != null) {
							isFlag = true;

						} else {
							isFlag = false;

							break;
						}
						String cell6 = row.getCell(6).getStringCellValue();
						if (cell6 != null) {
							isFlag = true;

						} else {
							isFlag = false;

							break;
						}
						String cell7 = row.getCell(7).getStringCellValue();
						if (cell7 != null) {
							isFlag = true;

						} else {
							isFlag = false;

							break;
						}
						if (fileExtension.equals("csv")) {
							String cell8 = row.getCell(8).getStringCellValue();
							if (cell8 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}

						} else {
							Double cell8 = row.getCell(8).getNumericCellValue();
							if (cell8 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}
						}
						String cell9 = row.getCell(9).getStringCellValue();
						if (cell9 != null) {
							isFlag = true;

						} else {
							isFlag = false;

							break;
						}
						if (fileExtension.equals("csv")) {
							String cell10 = row.getCell(10).getStringCellValue();
							if (cell10 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}

						} else {
							Double cell10 = row.getCell(10).getNumericCellValue();
							if (cell10 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}
						}
						String cell11 = row.getCell(11).getStringCellValue();
						if (cell11 != null) {
							isFlag = true;

						} else {
							isFlag = false;

							break;
						}
						String cell13 = row.getCell(13).getStringCellValue();
						if (cell13 != null) {
							isFlag = true;

						} else {
							isFlag = false;

							break;
						}

					} else if (noOfRows == 0) {
						jsonArray = "No Service Request";
					}

					else {
						/* Iterating over multiple SR */
						for (int i = 1; i < ctr; i++) {
							row = sheet.getRow(i);
							cell = row.getCell(0);
							String cell3 = row.getCell(3).getStringCellValue();
							if (cell3 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}
							String cell4 = row.getCell(4).getStringCellValue();
							if (cell4 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}
							String cell5 = row.getCell(5).getStringCellValue();
							if (cell5 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}
							String cell6 = row.getCell(6).getStringCellValue();
							if (cell6 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}
							String cell7 = row.getCell(7).getStringCellValue();
							if (cell7 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}
							if (fileExtension.equals("csv")) {
								String cell8 = row.getCell(8).getStringCellValue();
								if (cell8 != null) {
									isFlag = true;

								} else {
									isFlag = false;

									break;
								}

							} else {
								Double cell8 = row.getCell(8).getNumericCellValue();
								if (cell8 != null) {
									isFlag = true;

								} else {
									isFlag = false;

									break;
								}
							}
							String cell9 = row.getCell(9).getStringCellValue();
							if (cell9 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}
							if (fileExtension.equals("csv")) {
								String cell10 = row.getCell(10).getStringCellValue();
								if (cell10 != null) {
									isFlag = true;

								} else {
									isFlag = false;

									break;
								}

							} else {
								Double cell10 = row.getCell(10).getNumericCellValue();
								if (cell10 != null) {
									isFlag = true;

								} else {
									isFlag = false;

									break;
								}
							}
							String cell11 = row.getCell(11).getStringCellValue();
							if (cell11 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}
							String cell13 = row.getCell(13).getStringCellValue();
							if (cell13 != null) {
								isFlag = true;

							} else {
								isFlag = false;

								break;
							}

						}

					}

				} catch (Exception e) {
					isFlag = false;
					logger.error("Exception in validateNoOfRequestXLSX method "+e.getMessage());
				}
				if (isFlag == true && noOfRows == 1) {
					jsonArray = "Valid Single Request";
				} else if (isFlag == false && noOfRows == 1) {
					jsonArray = "Invalid Single Request";
				} else if (isFlag == true && noOfRows > 1) {

					jsonArray = "Valid Multiple Request";

				} else if (isFlag == false && noOfRows > 1) {
					jsonArray = "Invalid Multiple Request";
				}

				isCheck = false;
			}
		} catch (Exception e) {
			logger.error("Exception in validateNoOfRequestXLSX method "+e.getMessage());
		}
		return jsonArray;
	}

	/* Method call to validate number of columns in csv file */
	public String validateColumnCSV(Resource filePath) throws IOException {
		String jsonArray = "";

		boolean flag = false;
		File SAMPLE_XLSX_FILE_PATH = filePath.getFile();

		InputStream inputStream = new FileInputStream(SAMPLE_XLSX_FILE_PATH);
		Reader inputStreamReader = new InputStreamReader(inputStream);

		CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).build();
		List<String[]> rows = csvReader.readAll();
		List<String> header = null;
		List<String> rowValue = null;
		boolean isFlag = true;
		String jsonarray = "";

		int rowSize = rows.size();

		if (rowSize == 2) {
			for (int i = 0; i < (rows.size() - 1); i++) {
				header = Arrays.asList(rows.get(i));
				logger.info(header);

			}
		} else {
			for (int i = (rows.size() - 1); i >= 0; i--) {
				header = Arrays.asList(rows.get(i));
				logger.info(header);

			}
		}
		while (isFlag) {
			String cell0 = header.get(0).toString();

			if (cell0.equals("SR#")) {
				flag = true;
			} else {

				flag = false;
				break;

			}

			String cell3 = header.get(3).toString();

			if (cell3.equals("Customer Name*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}
			String cell4 = header.get(4).toString();

			if (cell4.equals("Site ID*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}
			String cell5 = header.get(5).toString();

			if (cell5.equals("Region*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}
			String cell6 = header.get(6).toString();

			if (cell6.equals("Vendor*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}
			String cell7 = header.get(7).toString();

			if (cell7.equals("Device Type*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}
			String cell8 = header.get(8).toString();

			if (cell8.equals("Model*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}
			String cell9 = header.get(9).toString();

			if (cell9.equals("OS*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}
			String cell10 = header.get(10).toString();

			if (cell10.equals("OS Version*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}
			String cell11 = header.get(11).toString();

			if (cell11.equals("Management IP*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}

			String cell12 = header.get(12).toString();

			if (cell12.equals("Service*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}

			String cell13 = header.get(13).toString();

			if (cell13.equals("Network Type*")) {
				flag = true;
			} else {
				flag = false;
				break;
			}
			isFlag = false;
		}
		if (flag) {
			jsonarray = "Valid";
		} else {
			jsonarray = "Invalid";
		}
		return jsonarray;
	}

	/* Method call to validate number of request in csv file */
	public String validateNoOfRequestCSV(Resource filePath) throws IOException {

		boolean isFlag = false;
		File SAMPLE_XLSX_FILE_PATH = filePath.getFile();

		InputStream inputStream = new FileInputStream(SAMPLE_XLSX_FILE_PATH);
		Reader inputStreamReader = new InputStreamReader(inputStream);

		CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).build();
		List<String[]> rows = csvReader.readAll();
		List<String> header = null;
		List<String> rowValue = null;

		String jsonarray = "";

		try {

			for (int i = 1; i < (rows.size()); i++) {

				rowValue = Arrays.asList(rows.get(i));

				logger.info(rowValue);

				String cell3 = rowValue.get(3).toString();
				if (!(cell3.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}
				String cell4 = rowValue.get(4).toString();
				if (!(cell4.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}
				String cell5 = rowValue.get(5).toString();
				if (!(cell5.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}
				String cell6 = rowValue.get(6).toString();
				if (!(cell6.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}
				String cell7 = rowValue.get(7).toString();
				if (!(cell7.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}

				String cell8 = rowValue.get(8).toString();
				if (!(cell8.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}

				String cell9 = rowValue.get(9).toString();
				if (!(cell9.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}

				String cell10 = rowValue.get(10).toString();
				if (!(cell10.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}

				String cell11 = rowValue.get(11).toString();
				if (!(cell11.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}
				String cell12 = rowValue.get(12).toString();
				if (!(cell9.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}
				String cell13 = rowValue.get(13).toString();
				if (!(cell13.equals(""))) {
					isFlag = true;

				} else {
					isFlag = false;

					break;
				}

			}

			if (isFlag == true && rows.size() == 2) {
				jsonarray = "Valid Single Request";
			} else if (isFlag == false && rows.size() == 2) {
				jsonarray = "Invalid Single Request";
			} else if (isFlag == true && rows.size() > 2) {

				jsonarray = "Valid Multiple Request";

			} else if (isFlag == false && rows.size() > 2) {
				jsonarray = "Invalid Multiple Request";
			}

		} catch (Exception e) {
			logger.error("Exception in validateNoOfRequestCSV method "+e.getMessage());
		}
		return jsonarray;

	}

	/*
	 * Method call to validate number of columns in customer onboarding csv file
	 */
	public Map<String, List<String>> validateColumnCSVForCOB(Resource filePath) throws IOException {
		logger.info("Inside validateColumnCSVForCOB method");
		ArrayList<String> list = new ArrayList<String>();
		Map<String, List<String>> response = new HashMap<String, List<String>>();
		List<String> missingManColumns = null;
		List<String> matchingOptColumns = null;
		InetAddressValidator validator = InetAddressValidator.getInstance();
		Set<String> csvAllColumns = new HashSet<String>();
		try (Reader reader = new FileReader(filePath.getFile());
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {
			Map<String, Integer> header = csvParser.getHeaderMap();

			Map<String, List<String>> dbColumns = getCOBHeaders();
			List<String> dbManColumns = dbColumns.get("ManColumns");
			List<String> dbOptColumns = dbColumns.get("OptionalColumns");
			for (String checkHeaderWithAsterick : header.keySet()) {
				if (checkHeaderWithAsterick.contains("*"))
					csvAllColumns.add(checkHeaderWithAsterick.replace("*", ""));
				else
					csvAllColumns.add(checkHeaderWithAsterick);
			}
			logger.info("csvAllColumns :" + csvAllColumns);
			logger.info("DB Mandatory :" + dbManColumns);
			logger.info("DB Optional Column :" + dbOptColumns);
			/* Checking the mandatory columns presents in csv headers */
			for (String manColumn : dbManColumns) {
				if (csvAllColumns.contains(manColumn)) {
					logger.info("Matched Mandatory column:" + manColumn);
				} else {
					if (missingManColumns == null) {
						missingManColumns = new ArrayList<String>();
					}
					missingManColumns.add(manColumn);
				}
			}

			if (missingManColumns != null) {
				// Adding Mandatory Columns information
				logger.info("missingManColumns:" + missingManColumns);
				// response.put("Missing mandatory headers", missingManColumns);
				response.put("C3P_CB_013", missingManColumns);
			} else {
				logger.info("No Missing Mandatory Columns");
				/* Finding matching optional columns presents in csv headers */
				for (String optColumn : dbOptColumns) {
					if (matchingOptColumns == null) {
						matchingOptColumns = new ArrayList<String>();
					}
					if (csvAllColumns.contains(optColumn)) {
						matchingOptColumns.add(optColumn);
					}
				}

				/*
				 * Read the csv data rows and store then based on key (header)
				 * and its value will be number of records present under it
				 */
				List<String> missingRowInfo = null;
				List<String> invalidIPInfo = null;
				List<CSVRecord> getAllCSVRecords = csvParser.getRecords();
				for (CSVRecord csvRecord : getAllCSVRecords) {
					long missingRow = 0;
					/* Check for missing row data for mandatory columns */
					for (String manColumn : dbManColumns) {
						if (!"IPV4 Management Address".equals(manColumn)
								&& !"IPV6 Management Address".equals(manColumn)) {
							/*appending * for mandatory columns to keep the column name is matching with CSV header*/
							manColumn = manColumn + "*";
							if (csvRecord.get(manColumn) == null
									|| (csvRecord.get(manColumn) != null && csvRecord.get(manColumn).isEmpty())) {
								missingRow = csvRecord.getRecordNumber();
							}
						}
					}
					logger.info("missingRow --" + missingRow);
					if (missingRow != 0) {
						if (missingRowInfo == null) {
							missingRowInfo = new ArrayList<String>();
						}
						missingRowInfo.add(String.valueOf(missingRow));
					}
					boolean isValidIP4Present = false;
					boolean isValidIP6Present = false;
					boolean isIP4Present = false;
					boolean isIP6Present = false;
					boolean isIP46Present = false;
					/*
					 * Check for missing IP data & Validation of IP4/IP6 for
					 * mandatory columns
					 */
					for (String manColumn : dbManColumns) {
						/*appending * for mandatory columns to keep the column name is matching with CSV header*/
						manColumn = manColumn + "*";
						/* Check for missing row data for mandatory columns */
						if ("IPV4 Management Address*".equals(manColumn) && csvRecord.get(manColumn) != null
								&& !csvRecord.get(manColumn).isEmpty()) {
							isValidIP4Present = validator.isValidInet4Address(csvRecord.get(manColumn));
							logger.info("isValidIP4Present 0-" + isValidIP4Present);
							isIP46Present = true;
							isIP4Present = true;
						}
						if ("IPV6 Management Address*".equals(manColumn) && csvRecord.get(manColumn) != null
								&& !csvRecord.get(manColumn).isEmpty()) {
							isValidIP6Present = InetAddresses.isInetAddress(csvRecord.get(manColumn));
							logger.info("isValidIP6Present 0-" + isValidIP6Present);
							isIP46Present = true;
							isIP6Present = true;
						}
					}
					/*
					 * Adding missing mandatory data for IP4/IP6 when there is
					 * not previous missingRowInfo present
					 */
					if (!isIP46Present && missingRow == 0) {
						logger.info("missingRowInfo 0-" + missingRowInfo);
						if (missingRowInfo == null) {
							missingRowInfo = new ArrayList<String>();
						}
						missingRowInfo.add(String.valueOf(csvRecord.getRecordNumber()));
						logger.info("missingRowInfo 1-" + missingRowInfo);
					}
					// Check for the IP4/IP6 present and its a valid one. If not
					// add the information
					// into invalidIPInfo.
					if ((isIP4Present && !isValidIP4Present) || (isIP6Present && !isValidIP6Present)) {
						if (invalidIPInfo == null) {
							invalidIPInfo = new ArrayList<String>();
						}
						invalidIPInfo.add(String.valueOf(csvRecord.getRecordNumber()));
					}
				}

				if (missingRowInfo == null && invalidIPInfo == null) {
					List<String> validList = new ArrayList<String>();
					validList.add("Valid CSV File");
					response.put("Valid", validList);
				} else {
					if (missingRowInfo != null) {
						logger.info("missingRowInfo size-" + missingRowInfo.size());
						// response.put("Fields are missing", missingRowInfo);
						response.put("C3P_CB_012", missingRowInfo);
					}

					if (invalidIPInfo != null) {
						logger.info("invalidIPInfo size-" + invalidIPInfo.size());
						// response.put("IP4/IP6 data is not valid",
						// invalidIPInfo);
						response.put("C3P_CB_014", invalidIPInfo);
					}
				}

			}

		} catch (Exception e) {
			list.add(e.getMessage());
			response.put("error", list);
			logger.error("exception in validateColumnCSVForCOB method" + e.getMessage());
		}
		return response;
	}
	/*
	 * Check for missing data for mandatory columns, matching optional columns presents in csv headers
	 * 
	 */
	public List<Map<String, String>> consolidateCSVData(MultipartFile file) throws IOException {
		List<Map<String, String>> consCSVData = new ArrayList<Map<String, String>>();
		List<String> matchingOptColumns = null;

		try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {
			Map<String, Integer> header = csvParser.getHeaderMap();

			Map<String, List<String>> dbColumns = getCOBHeaders();
			List<String> dbManColumns = dbColumns.get("ManColumns");
			List<String> dbOptColumns = dbColumns.get("OptionalColumns");
			Set<String> csvAllColumns = new HashSet<String>();
			for (String checkHeaderWithAsterick : header.keySet()) {
				if (checkHeaderWithAsterick.contains("*"))
					csvAllColumns.add(checkHeaderWithAsterick.replace("*", ""));
				else
					csvAllColumns.add(checkHeaderWithAsterick);
			}
			logger.info("csvAllColumns :" + csvAllColumns);
			logger.info("DB Mandatory :" + dbManColumns);
			logger.info("DB Optional Column :" + dbOptColumns);

			/* Finding matching optional columns presents in csv headers */
			for (String optColumn : dbOptColumns) {
				if (matchingOptColumns == null) {
					matchingOptColumns = new ArrayList<String>();
				}
				if (csvAllColumns.contains(optColumn)) {
					matchingOptColumns.add(optColumn);
				}
			}

			List<CSVRecord> getAllCSVRecords = csvParser.getRecords();
			for (CSVRecord csvRecord : getAllCSVRecords) {
				Map<String, String> rowData = new HashMap<String, String>();
				/* Check for missing row data for mandatory columns */
				for (String manColumn : dbManColumns) {
					/*appending * for mandatory columns to keep the column name is matching with CSV header*/
					manColumn = manColumn + "*";
					rowData.put(manColumn, csvRecord.get(manColumn));
				}

				/* Adding optional header columns data in to rowData map */
				for (String matchColumn : matchingOptColumns) {
					rowData.put(matchColumn, csvRecord.get(matchColumn));
				}
				consCSVData.add(rowData);
			}

			logger.info("consCSVData size - " + consCSVData.size());
			for (Map<String, String> rowData : consCSVData) {
				logger.info("consCSVData row Data - " + rowData);
				logger.info("consCSVData row Data keyset - " + rowData.keySet());
				for (String key : rowData.keySet()) {
					logger.info("consCSVData row Data key - " + key);
					logger.info("consCSVData row Data key values- " + rowData.get(key));
				}
				logger.info(" #######################################################################################");
			}

		} catch (Exception e) {
			logger.error("exception in validateColumnCSVForCOB method" + e.getMessage());
		}
		return consCSVData;
	}

	public String getErrorInformation(String errorType, List<String> message) {
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append(errorValidationRepository.findByErrorId(errorType));
		errorMessage.append(message.toString());
		logger.info("getErrorInformation-" + errorMessage.toString());
		return errorMessage.toString();
	}

	private Map<String, List<String>> getCOBHeaders() {
		Map<String, List<String>> cobHeaderMap = new HashMap<String, List<String>>();
		List<String> manColumns = new ArrayList<String>();
		List<String> optionalColumns = new ArrayList<String>();
		List<CSVHeaderCOBEntity> allColumns = csvHeader.findAll();
		for (CSVHeaderCOBEntity entity : allColumns) {
			if (entity.getMandatoryFlag().equals("1")) {
				manColumns.add(entity.getCsvHeaderName());
			} else {
				optionalColumns.add(entity.getCsvHeaderName());
			}
		}
		cobHeaderMap.put("ManColumns", manColumns);
		cobHeaderMap.put("OptionalColumns", optionalColumns);
		return cobHeaderMap;
	}
}
