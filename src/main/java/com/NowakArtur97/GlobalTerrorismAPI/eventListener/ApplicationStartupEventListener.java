package com.NowakArtur97.GlobalTerrorismAPI.eventListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.NowakArtur97.GlobalTerrorismAPI.service.api.TargetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationStartupEventListener {

	private final static String pathToFile = "C:\\Users\\Samsung\\Spring\\eclipse-workspace\\Projects\\GlobalTerrorismAPI\\src\\main\\resources\\data\\globalterrorismdb_0919dist.xlsx";

	private final TargetService targetService;

//	@EventListener
//	public void onApplicationStartup(ContextRefreshedEvent event) {
//
//		File globalTerrorismFile = new File(pathToFile);
//
//		InputStream inputStream;
//
//		try {
//
//			inputStream = new FileInputStream(globalTerrorismFile);
//
//			Workbook workbook = StreamingReader.builder().rowCacheSize(10).bufferSize(4096).open(inputStream);
//
//			Sheet sheet = workbook.getSheetAt(0);
//
//			for (Row row : sheet) {
//
//				int targetIndex = 0;
//
//				for (int i = 0; i < row.getLastCellNum(); i++) {
//
//					Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
//
//					if (cell != null && targetIndex == XlsxColumnType.TARGET.getIndex()) {
//
//						String targetValue = null;
//
//						switch (cell.getCellType()) {
//
//						case NUMERIC:
//							Double doubleValue = cell.getNumericCellValue();
//							targetValue = doubleValue.toString();
//							break;
//						case STRING:
//							targetValue = cell.getStringCellValue();
//							break;
//						case FORMULA:
//							targetValue = cell.getCellFormula();
//							break;
//						case BLANK:
//							targetValue = "BLANK";
//							break;
//						case BOOLEAN:
//							boolean booleanValue = cell.getBooleanCellValue();
//							targetValue = "" + booleanValue;
//							break;
//						case ERROR:
//							byte byteValue = cell.getErrorCellValue();
//							targetValue = "" + byteValue;
//							break;
//						case _NONE:
//							targetValue = "_NONE";
//							break;
//						default:
//							break;
//						}
//						
//						Target target = new Target(targetValue);
//
//						targetRepository.save(target);
//					}
//
//					targetIndex++;
//				}
//			}
//		} catch (FileNotFoundException e) {
//
//			log.info("File in path: " + pathToFile + " not found");
//		} 
//	}
}
