package com.techm.orion.ValidatorConfigService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.google.common.net.InetAddresses;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.techm.orion.repositories.DiscrepancyMsgRepository;

/*Class to validate file against predefine set of rules like file format, mandatory column name and position, mandatory values*/
@Service
public class ExcelFileValidation {
	
	@Autowired
	DiscrepancyMsgRepository msgRepo;
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

	/*
	 * Method call to validate number of columns in customer onboarding csv file
	 */
	public Map<String, List> validateColumnCSVForCOB(Resource filePath) throws IOException {

		logger.info("\n" + "Inside validateColumnCSVForCOB method");
		ArrayList<String> list = new ArrayList<String>();
		Map<String, List> response = new HashMap<String, List>();
		boolean flag = false;
		boolean flagError = false;
		File SAMPLE_XLSX_FILE_PATH = filePath.getFile();

		try {
			InputStream inputStream = new FileInputStream(SAMPLE_XLSX_FILE_PATH);
			Reader inputStreamReader = new InputStreamReader(inputStream);
			CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).build();
			List<String[]> rows = csvReader.readAll();
			List<String> header = null;
			boolean isFlag = true;
			String status = "";
			int countCSVColumns =Arrays.asList(rows.get(0)).size();
			
			if(countCSVColumns !=32)
				throw new IOException(msgRepo.findDiscrepancyMsg("mandatory csv col"));
			
			int rowSize = rows.size();

			if (rowSize == 2) {
				for (int i = 0; i < (rows.size() - 1); i++) {
					header = Arrays.asList(rows.get(i));
				}
			} else {
				for (int i = (rows.size() - 1); i >= 0; i--) {
					header = Arrays.asList(rows.get(i));
				}
			}
			while (isFlag) {
				String cell0 = header.get(0).toString();
				if (cell0.equals("SR#")) {
					flag = true;
				} else {
					list.add(cell0.toString());
					flagError = true;
				}

				String cell1 = header.get(1).toString();
				if (cell1.equals("IPV4 Management Address*")) {
					flag = true;
				} else {
					list.add(cell1.toString());
					flagError = true;
				}

				String cell2 = header.get(2).toString();
				if (cell2.equals("IPV6 Management Address*")) {
					flag = true;
				} else {
					list.add(cell2);
					flagError = true;
				}

				String cell3 = header.get(3).toString();
				if (cell3.equals("Hostname")) {
					flag = true;
				} else {
					list.add(cell3);
					flagError = true;
				}

				String cell4 = header.get(4).toString();
				if (cell4.equals("Device Vendor")) {
					flag = true;
				} else {
					list.add(cell4);
					flagError = true;
				}

				String cell5 = header.get(5).toString();
				if (cell5.equals("Device Family")) {
					flag = true;
				} else {
					list.add(cell5);
					flagError = true;
				}

				String cell6 = header.get(6).toString();
				if (cell6.equals("Device Model")) {
					flag = true;
				} else {
					list.add(cell6);
					flagError = true;
				}

				String cell7 = header.get(7).toString();
				if (cell7.equals("OS")) {
					flag = true;
				} else {
					list.add(cell7);
					flagError = true;
				}

				String cell8 = header.get(8).toString();
				if (cell8.equals("OS Ver")) {
					flag = true;
				} else {
					list.add(cell8);
					flagError = true;
				}

				String cell9 = header.get(9).toString();
				if (cell9.equals("CPU")) {
					flag = true;
				} else {
					list.add(cell9);
					flagError = true;
				}

				String cell10 = header.get(10).toString();
				if (cell10.equals("CPU Version")) {
					flag = true;
				} else {
					list.add(cell10);
					flagError = true;
				}

				String cell11 = header.get(11).toString();
				if (cell11.equals("DRAM Size(Mb)")) {
					flag = true;
				} else {
					list.add(cell11);
					flagError = true;
				}

				String cell12 = header.get(12).toString();
				if (cell12.equals("Flash Size(Mb)")) {
					flag = true;
				} else {
					list.add(cell12);
					flagError = true;
				}

				String cell13 = header.get(13).toString();
				if (cell13.equals("image filename")) {
					flag = true;
				} else {
					list.add(cell13);
					flagError = true;
				}

				String cell14 = header.get(14).toString();
				if (cell14.equals("MAC Address")) {
					flag = true;
				} else {
					list.add(cell14);
					flagError = true;
				}

				String cell15 = header.get(15).toString();
				if (cell15.equals("Serial Number")) {
					flag = true;
				} else {
					list.add(cell15);
					flagError = true;
				}

				String cell16 = header.get(16).toString();
				if (cell16.equals("Customer Name")) {
					flag = true;
				} else {
					list.add(cell16);
					flag = false;
				}

				String cell17 = header.get(17).toString();
				if (cell17.equals("Customer ID*")) {
					flag = true;
				} else {
					list.add(cell17);
					flagError = true;
				}

				String cell18 = header.get(18).toString();
				if (cell18.equals("Site Name*")) {
					flag = true;
				} else {
					list.add(cell18);
					flagError = true;
				}

				String cell19 = header.get(19).toString();
				if (cell19.equals("Site ID*")) {
					flag = true;
				} else {
					list.add(cell19);
					flagError = true;
				}

				String cell20 = header.get(20).toString();
				if (cell20.equals("Site Address")) {
					flag = true;
				} else {
					list.add(cell20);
					flagError = true;
				}

				String cell21 = header.get(21).toString();
				if (cell21.equals("Site Address1")) {
					flag = true;
				} else {
					list.add(cell21);
					flagError = true;
				}

				String cell22 = header.get(22).toString();
				if (cell22.equals("City")) {
					flag = true;
				} else {
					list.add(cell22);
					flagError = true;
				}

				String cell23 = header.get(23).toString();
				if (cell23.equals("Site Contact")) {
					flag = true;
				} else {
					list.add(cell23);
					flagError = true;
				}

				String cell24 = header.get(24).toString();
				if (cell24.equals("Contact Email ID")) {
					flag = true;
				} else {
					list.add(cell24);
					flagError = true;
				}

				String cell25 = header.get(25).toString();
				if (cell25.equals("Contact number")) {
					flag = true;
				} else {
					list.add(cell25);
					flagError = true;
				}

				String cell26 = header.get(26).toString();
				if (cell26.equals("Country")) {
					flag = true;
				} else {
					list.add(cell26);
					flagError = true;
				}

				String cell27 = header.get(27).toString();
				if (cell27.equals("Market")) {
					flag = true;
				} else {
					list.add(cell27);
					flagError = true;
				}

				String cell28 = header.get(28).toString();
				if (cell28.equals("Site Region")) {
					flag = true;
				} else {
					list.add(cell28);
					flagError = true;
				}

				String cell29 = header.get(29).toString();
				if (cell29.equals("Site State")) {
					flag = true;
				} else {
					list.add(cell29);
					flagError = true;
				}

				String cell30 = header.get(30).toString();
				if (cell30.equals("Site Status")) {
					flag = true;
				} else {
					list.add(cell30);
					flagError = true;
				}

				String cell31 = header.get(31).toString();
				if (cell31.equals("Site Subregion")) {
					flag = true;
				} else {
					list.add(cell31);
					flagError = true;
					break;
				}
				isFlag = false;
			}
			if (flag && !flagError) {
				status = msgRepo.findDiscrepancyMsg("csv format");
				list.add(status);
				response.put("Valid", list);
			} else if(flagError ==true && list.get(0) !=null && list.contains("")) {
				status = msgRepo.findDiscrepancyMsg("mandatory csv col");
				List<String> l = new ArrayList<String>();
				l.add(status);
				response.put("mandatory csv col",l);
			}else {
				status = msgRepo.findDiscrepancyMsg("bad column");
				response.put(status, list);
			}
		} catch (Exception e) {
			list.add(e.getMessage());
			response.put("error", list);
			logger.error("\n" + "exception in validateColumnCSVForCOB method" + e.getMessage());
		}
		return response;
	}

	/*
	 * Method call to validate number of request of CustomerOnBoarding in csv
	 * file
	 */
	public Map<String, String> validateColumnValuesCSVForCOB(Resource filePath) throws IOException {

		logger.info("\n" + "Inside validateColumnValuesCSVForCOB method");
		Map<String, String> response = new HashMap<String, String>();
		StringBuilder strBbuilder = new StringBuilder();
		boolean isFlagError = true;
		File SAMPLE_XLSX_FILE_PATH = filePath.getFile();
		try {
			InputStream inputStream = new FileInputStream(SAMPLE_XLSX_FILE_PATH);
			Reader inputStreamReader = new InputStreamReader(inputStream);

			// Get an InetAddressValidator
			InetAddressValidator validator = InetAddressValidator.getInstance();
			CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).build();
			List<String[]> rows = csvReader.readAll();
			List<String> rowValue = null;
			String status = "";

			for (int i = 1; i < (rows.size()); i++) {
				rowValue = Arrays.asList(rows.get(i));

				if (((rowValue.get(1) != null && !rowValue.get(1).isEmpty())
						|| (rowValue.get(2) != null && !rowValue.get(2).isEmpty())) && rowValue.get(3) != null
						&& !rowValue.get(3).isEmpty() && rowValue.get(4) != null && !rowValue.get(4).isEmpty()
						&& rowValue.get(5) != null && !rowValue.get(5).isEmpty() && rowValue.get(6) != null
						&& !rowValue.get(6).isEmpty() && rowValue.get(7) != null && !rowValue.get(7).isEmpty()
						&& rowValue.get(8) != null && !rowValue.get(8).isEmpty() && rowValue.get(16) != null
						&& !rowValue.get(16).isEmpty() && rowValue.get(17) != null && !rowValue.get(17).isEmpty()
						&& rowValue.get(18) != null && !rowValue.get(18).isEmpty() && rowValue.get(19) != null
						&& !rowValue.get(19).isEmpty()) {
					isFlagError = false;
					if (rowValue.get(1) != null && !rowValue.get(1).isEmpty()
							&& !validator.isValidInet4Address(rowValue.get(1))) {
						isFlagError = true;
					} else if (rowValue.get(2) != null && !rowValue.get(2).isEmpty()
							&& !InetAddresses.isInetAddress(rowValue.get(2))) {
						isFlagError = true;
					}
				}

				if (isFlagError) {
					strBbuilder.append(i).append(",").toString();
					isFlagError = false;
				}
			}

			if (strBbuilder.length() > 0) {
				strBbuilder.deleteCharAt(strBbuilder.length() - 1);
				isFlagError = true;
			}
			if (rows.size() < 2) {
				status = msgRepo.findDiscrepancyMsg("no records");
				response.put("No records found", status);
			} else if (isFlagError && rows.size() >= 2) {
				String msg = msgRepo.findDiscrepancyMsg("Mandatory Col");
				strBbuilder.insert(0, msg);
				response.put("Fields are missing", strBbuilder.toString());
			} else if (!isFlagError) {
				if (rows.size() == 2) {
					status = msgRepo.findDiscrepancyMsg("valid single req");
					response.put("Valid Single Request", status);
				} else {
					status = msgRepo.findDiscrepancyMsg("valid multiple req");
					response.put("Valid Multiple Request", status);
				}
			}

		} catch (Exception e) {
			logger.error("\n" + "exception in validateColumnValuesCSVForCOB method" + e.getMessage());
		}
		return response;
	}
}
