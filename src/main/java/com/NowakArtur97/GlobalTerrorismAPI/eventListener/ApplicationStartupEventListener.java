package com.NowakArtur97.GlobalTerrorismAPI.eventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.NowakArtur97.GlobalTerrorismAPI.enums.XlsxColumnType;
import com.NowakArtur97.GlobalTerrorismAPI.node.Target;
import com.NowakArtur97.GlobalTerrorismAPI.repository.TargetRepository;
import com.monitorjbl.xlsx.StreamingReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationStartupEventListener {

	private final static String pathToFile = "C:\\Users\\Samsung\\Spring\\eclipse-workspace\\Projects\\GlobalTerrorismAPI\\src\\main\\resources\\data\\globalterrorismdb_0919dist-mini.xlsx";

	private final TargetRepository targetRepository;

	@EventListener
	public void onApplicationStartup(ContextRefreshedEvent event) {

		File globalTerrorismFile = new File(pathToFile);

		InputStream inputStream;

		try {

			inputStream = new FileInputStream(globalTerrorismFile);

			Workbook workbook = StreamingReader.builder().rowCacheSize(10).bufferSize(4096).open(inputStream);

			Sheet sheet = workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.rowIterator();

			while (rowIterator.hasNext()) {

				int targetIndex = 0;

				Row row = rowIterator.next();

				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {

					Cell cell = cellIterator.next();

					if (targetIndex == XlsxColumnType.TARGET.getIndex() && !cell.getStringCellValue().isBlank()) {

						Target target = new Target(cell.getStringCellValue());

						targetRepository.save(target);
					}

					targetIndex++;
				}
			}
		} catch (FileNotFoundException e) {

			log.info("File in path: " + pathToFile + " not found");
		}

	}
}
