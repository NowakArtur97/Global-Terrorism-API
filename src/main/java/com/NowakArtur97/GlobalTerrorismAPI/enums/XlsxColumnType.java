package com.NowakArtur97.GlobalTerrorismAPI.enums;

import lombok.Getter;

public enum XlsxColumnType {

	TARGET(39);

	@Getter
	private final int index;

	private XlsxColumnType(int index) {

		this.index = index;
	}
}
