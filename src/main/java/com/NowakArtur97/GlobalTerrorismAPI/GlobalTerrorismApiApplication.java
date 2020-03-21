package com.NowakArtur97.GlobalTerrorismAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.monitorjbl.xlsx.StreamingReader;

@SpringBootApplication
public class GlobalTerrorismApiApplication implements CommandLineRunner {

	private final static String pathToFile = "C:\\Users\\Samsung\\Spring\\eclipse-workspace\\Projects\\GlobalTerrorismAPI\\src\\main\\resources\\data\\globalterrorismdb_0919dist.xlsx";

	public static void main(String[] args) {
		SpringApplication.run(GlobalTerrorismApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		File globalTerrorismFile = new File(pathToFile);

		InputStream inputStream = new FileInputStream(globalTerrorismFile);

		Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(inputStream);

		Sheet sheet = workbook.getSheetAt(0);

		Iterator<Row> rowIterator = sheet.rowIterator();

		while (rowIterator.hasNext()) {

			Row row = rowIterator.next();

			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext()) {

				Cell cell = cellIterator.next();

				switch (cell.getCellType()) {
				case STRING:
					System.out.print(cell.getStringCellValue() + "\t");
					break;
				case NUMERIC:
					System.out.print(cell.getNumericCellValue() + "\t");
					break;
				case BOOLEAN:
					System.out.print(cell.getBooleanCellValue() + "\t");
					break;
				default:
					System.out.print(cell.getStringCellValue());
				}
			}

			System.out.println();
		}
	}
}
