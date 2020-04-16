package com.NowakArtur97.GlobalTerrorismAPI.eventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
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

	@EventListener
	public void onApplicationStartup(ContextRefreshedEvent event) {

		if (targetService.isDatabaseEmpty()) {
			
			try {

				File globalTerrorismFile = ResourceUtils.getFile(PATH_TO_FILE);

				InputStream inputStream = new FileInputStream(globalTerrorismFile);

				Workbook workbook = StreamingReader.builder().rowCacheSize(10).bufferSize(4096).open(inputStream);

				Sheet sheet = workbook.getSheetAt(0);

				for (Row row : sheet) {

					int targetIndex = 0;

					for (int i = 0; i < row.getLastCellNum(); i++) {

						Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);

						if (cell != null && targetIndex == XlsxColumnType.TARGET.getIndex()) {

							String targetValue = null;

							switch (cell.getCellType()) {

							case NUMERIC:
								Double doubleValue = cell.getNumericCellValue();
								targetValue = doubleValue.toString();
								break;

							case STRING:
								targetValue = cell.getStringCellValue();
								break;
							case FORMULA:
								targetValue = cell.getCellFormula();
								break;

							case BLANK:
								targetValue = "BLANK";
								break;

							case BOOLEAN:
								boolean booleanValue = cell.getBooleanCellValue();
								targetValue = "" + booleanValue;
								break;

							case ERROR:
								byte byteValue = cell.getErrorCellValue();
								targetValue = "" + byteValue;
								break;

							case _NONE:
								targetValue = "_NONE";
								break;

							default:
								break;
							}

							TargetNode target = new TargetNode(targetValue);

							targetService.persistUpdate(target);
						}

						targetIndex++;
					}
				}
			} catch (FileNotFoundException e) {

				log.info("File in path: " + PATH_TO_FILE + " not found");
			}
		}
	}
}
