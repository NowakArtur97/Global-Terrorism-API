package com.NowakArtur97.GlobalTerrorismAPI.enums;

import lombok.Getter;

public enum XlsxColumnType {

	YEAR_OF_EVENT(1), MONTH_OF_EVENT(2), DAY_OF_EVENT(3), EVENT_SUMMARY(18), WAS_EVENT_PART_OF_MULTIPLE_INCIDENTS(25),
	WAS_EVENT_SUCCESS(26), WAS_EVENT_SUICIDE(27), TARGET(39), MOTIVE(64);

	@Getter
	private int index;

	private XlsxColumnType(int index) {

		this.index = index;
	}
}
