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
import com.NowakArtur97.GlobalTerrorismAPI.repository.EventRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;
import com.monitorjbl.xlsx.StreamingReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationStartupEventListener {

	private final static String PATH_TO_FILE = "classpath:data/globalterrorismdb_0919dist-mini.xlsx";

	private final TargetService targetService;

	private final EventRepository eventRepository;

	@EventListener
	public void onApplicationStartup(ContextRefreshedEvent event) {

		if (targetService.isDatabaseEmpty()) {

			try {

				Sheet sheet = loadSheetFromFile();

				insertDataToDatabase(sheet);

			} catch (FileNotFoundException e) {

				log.info("File in path: " + PATH_TO_FILE + " not found");
			}
		}
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
			int monthOfEvent = 1;
			int dayOfEvent = 1;
			String eventSummary = "";
			boolean wasPartOfMultipleIncidents = false;
			boolean wasSuccessful = false;
			boolean wasSuicide = false;
			String motive = "";
			TargetNode target = new TargetNode();

			for (int i = 0; i < row.getLastCellNum(); i++) {

				Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);

				if (cell != null) {

					if (columnIndex == XlsxColumnType.TARGET.getIndex()) {

						target = saveTarget(cell);

					} else if (columnIndex == XlsxColumnType.YEAR_OF_EVENT.getIndex()) {

						String cellVal = getCellValue(cell);

						if (isNumeric(cellVal)) {
							
							yearOfEvent = (int) Double.parseDouble(getCellValue(cell));

//							log.info("YEAR_OF_EVENT: " + yearOfEvent);
						}

					} else if (columnIndex == XlsxColumnType.MONTH_OF_EVENT.getIndex()) {

						String cellVal = getCellValue(cell);

						if (isNumeric(cellVal)) {

							monthOfEvent = (int) Double.parseDouble(getCellValue(cell));

//							log.info("MONTH_OF_EVENT: " + monthOfEvent);
						}

					} else if (columnIndex == XlsxColumnType.DAY_OF_EVENT.getIndex()) {

						String cellVal = getCellValue(cell);

						if (isNumeric(cellVal)) {

							dayOfEvent = (int) Double.parseDouble(getCellValue(cell));

//							log.info("DAY_OF_EVENT: " + dayOfEvent);
						}

					} else if (columnIndex == XlsxColumnType.EVENT_SUMMARY.getIndex()) {

						eventSummary = getCellValue(cell);

					} else if (columnIndex == XlsxColumnType.WAS_PART_OF_MULTIPLE_INCIDENTS.getIndex()) {

						String cellVal = getCellValue(cell);

						wasPartOfMultipleIncidents = "1".equals(cellVal) || "1.0".equals(cellVal);

//						log.info(cellVal + " WAS_PART_OF_MULTIPLE_INCIDENTS: " + wasPartOfMultipleIncidents);

					} else if (columnIndex == XlsxColumnType.WAS_SUCCESS.getIndex()) {

						String cellVal = getCellValue(cell);

						wasSuccessful = "1".equals(cellVal) || "1.0".equals(cellVal);

//						log.info(cellVal + " WAS_SUCCESS: " + wasSuccessful);

					} else if (columnIndex == XlsxColumnType.WAS_SUICIDE.getIndex()) {

						String cellVal = getCellValue(cell);

						wasSuicide = "1".equals(cellVal) || "1.0".equals(cellVal);

//						log.info(cellVal + " WAS_SUICIDE: " + wasSuicide);

					} else if (columnIndex == XlsxColumnType.MOTIVE.getIndex()) {

						motive = getCellValue(cell);

//						log.info("MOTIVE: " + motive);
					}
				}

				columnIndex++;
			}

			saveEvent(yearOfEvent, monthOfEvent, dayOfEvent, eventSummary, wasPartOfMultipleIncidents, wasSuccessful,
					wasSuicide, motive, target);
		}
	}

	private TargetNode saveTarget(Cell cell) {

		String targetName = getCellValue(cell);

		TargetNode target = new TargetNode(targetName);

		return targetService.persistUpdate(target);
	}

	private void saveEvent(int yearOfEvent, int monthOfEvent, int dayOfEvent, String eventSummary,
			boolean wasPartOfMultipleIncidents, boolean wasSuccessful, boolean wasSuicide, String motive,
			TargetNode target) {

		Date date = getEventDate(yearOfEvent, monthOfEvent, dayOfEvent);

		EventNode eventNode = EventNode.builder().date(date).summary(eventSummary)
				.wasPartOfMultipleIncidents(wasPartOfMultipleIncidents).wasSuccessful(wasSuccessful)
				.wasSuicide(wasSuicide).motive(motive).target(target).build();

		eventRepository.save(eventNode);
	}

	private Date getEventDate(int yearOfEvent, int monthOfEvent, int dayOfEvent) {

		monthOfEvent = isMonthCorrect(monthOfEvent) ? monthOfEvent : 1;
		dayOfEvent = isDayCorrect(dayOfEvent) ? dayOfEvent : 1;

		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.YEAR, yearOfEvent);
		cal.set(Calendar.MONTH, monthOfEvent);
		cal.set(Calendar.DAY_OF_MONTH, dayOfEvent);

		return cal.getTime();
	}

	private boolean isMonthCorrect(int monthOfEvent) {

		return monthOfEvent > 0 && monthOfEvent <= 12;
	}

	private boolean isDayCorrect(int dayOfEvent) {

		return dayOfEvent > 0 && dayOfEvent <= 31;
	}

	private boolean isNumeric(String number) {
		
		return NumberUtils.isParsable(number);
	}
	
	private String getCellValue(Cell cell) {

		String value = "";

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
			break;
		}

		return value;
	}
}
