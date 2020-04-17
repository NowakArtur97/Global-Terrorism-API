package com.NowakArtur97.GlobalTerrorismAPI.eventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.NowakArtur97.GlobalTerrorismAPI.enums.XlsxColumnType;
import com.NowakArtur97.GlobalTerrorismAPI.node.EventNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.monitorjbl.xlsx.StreamingReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationStartupEventListener {

	private final static String PATH_TO_FILE = "classpath:data/globalterrorismdb_0919dist-mini2.xlsx";

	private final TargetService targetService;

//	private final EventRepository eventRepository;

	@EventListener
	public void onApplicationStartup(ContextRefreshedEvent event) {

//		if (targetService.isDatabaseEmpty()) {

		try {

			Sheet sheet = loadSheetFromFile();

			insertDataToDatabase(sheet);

		} catch (FileNotFoundException e) {

			log.info("File in path: " + PATH_TO_FILE + " not found");
		}
//		}
	}

	private Sheet loadSheetFromFile() throws FileNotFoundException {

		File globalTerrorismFile = ResourceUtils.getFile(PATH_TO_FILE);

		InputStream inputStream = new FileInputStream(globalTerrorismFile);

		Workbook workbook = StreamingReader.builder().rowCacheSize(10).bufferSize(4096).open(inputStream);

		Sheet sheet = workbook.getSheetAt(0);

		return sheet;
	}

	private void insertDataToDatabase(Sheet sheet) {

		for (Row row : sheet) {

			int columnIndex = 0;

			int yearOfEvent = 1900;
			int monthOfEvent = 12;
			int dayOfEvent = 12;
			String eventSummary;
			boolean wasPartOfMultipleIncidents;
			boolean wasSuccessful;
			boolean wasSuicide;
			String motive;

			for (int i = 0; i < row.getLastCellNum(); i++) {

				Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);

				if (cell != null) {

					if (columnIndex == XlsxColumnType.TARGET.getIndex()) {

//						saveTarget(cell);

					} else if (columnIndex == XlsxColumnType.YEAR_OF_EVENT.getIndex()) {

//						log.info("YEAR: " + getCellValue(cell).toString());

						String cellVal = getCellValue(cell);

						boolean isNumeric = NumberUtils.isParsable(cellVal);

						if (isNumeric) {
							yearOfEvent = (int) Double.parseDouble(getCellValue(cell));
						}

					} else if (columnIndex == XlsxColumnType.MONTH_OF_EVENT.getIndex()) {

//						log.info("MONTH: " + getCellValue(cell).toString());
						String cellVal = getCellValue(cell);

						boolean isNumeric = NumberUtils.isParsable(cellVal);

						if (isNumeric) {
							monthOfEvent = (int) Double.parseDouble(getCellValue(cell));
						}

					} else if (columnIndex == XlsxColumnType.DAY_OF_EVENT.getIndex()) {

//						log.info("DAY: " + getCellValue(cell).toString());
						String cellVal = getCellValue(cell);

						boolean isNumeric = NumberUtils.isParsable(cellVal);

						if (isNumeric) {
							dayOfEvent = (int) Double.parseDouble(getCellValue(cell));
						}

					} else if (columnIndex == XlsxColumnType.EVENT_SUMMARY.getIndex()) {

//						log.info("SUMMARY: " + getCellValue(cell).toString());
						eventSummary = getCellValue(cell);

					} else if (columnIndex == XlsxColumnType.WAS_PART_OF_MULTIPLE_INCIDENTS.getIndex()) {

//						log.info("WAS PART OF MULTIPLE INCIDENTS: " + getCellValue(cell).toString());
//						wasPartOfMultipleIncidents = getCellValue(cell);

					} else if (columnIndex == XlsxColumnType.WAS_SUCCESS.getIndex()) {

//						log.info("WAS SUCCESS: " + getCellValue(cell).toString());
//						wasSuccessful = getCellValue(cell);

					} else if (columnIndex == XlsxColumnType.WAS_SUICIDE.getIndex()) {

//						log.info("WAS SUICIDE: " + getCellValue(cell).toString());
//						wasSuicide = getCellValue(cell);

					} else if (columnIndex == XlsxColumnType.MOTIVE.getIndex()) {

//						log.info("MOTIVE: " + getCellValue(cell).toString());
//						motive = getCellValue(cell);
					}
				}

				columnIndex++;
			}
//			saveEvent(yearOfEvent, monthOfEvent, dayOfEvent, eventSummary, wasPartOfMultipleIncidents, wasSuccessful,
//					wasSuicide, motive);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, yearOfEvent);
			cal.set(Calendar.MONTH, monthOfEvent);
			cal.set(Calendar.DAY_OF_MONTH, dayOfEvent);
			Date date = cal.getTime();
			log.info(date.toString());

			log.info("************************************");
		}

	}

	private void saveTarget(Cell cell) {

		String targetName = getCellValue(cell);

		TargetNode target = new TargetNode(targetName);

		targetService.persistUpdate(target);
	}

	private void saveEvent(int yearOfEvent, int monthOfEvent, int dayOfEvent, String eventSummary,
			boolean wasPartOfMultipleIncidents, boolean wasSuccessful, boolean wasSuicide, String motive) {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, yearOfEvent);
		cal.set(Calendar.MONTH, monthOfEvent);
		cal.set(Calendar.DAY_OF_MONTH, dayOfEvent);
		Date date = cal.getTime();

		EventNode eventNode = EventNode.builder().date(date).summary(eventSummary)
				.wasPartOfMultipleIncidents(wasPartOfMultipleIncidents).wasSuccessful(wasSuccessful)
				.wasSuicide(wasSuicide).build();

//		eventRepository.save(eventNode);
	}

	private String getCellValue(Cell cell) {

		String value = null;

		switch (cell.getCellType()) {

		case NUMERIC:
			Double doubleValue = cell.getNumericCellValue();
			value = doubleValue.toString();
			break;

		case STRING:
			value = cell.getStringCellValue();
			break;

		case FORMULA:
			value = cell.getCellFormula();
			break;

		case BOOLEAN:
			boolean booleanValue = cell.getBooleanCellValue();
			value = "" + booleanValue;
			break;

		case ERROR:
			byte byteValue = cell.getErrorCellValue();
			value = "" + byteValue;
			break;

		case BLANK:
		case _NONE:
		default:
			value = "";
			break;
		}

		return value;
	}
}
