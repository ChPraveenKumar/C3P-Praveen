package com.techm.orion.ValidatorConfigService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.Resource;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/*Class to validate file against predefine set of rules like file format, mandatory column name and position, mandatory values*/
public class ExcelFileValidation {
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

			e.printStackTrace();
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
		}
		return jsonarray;

	}

}
