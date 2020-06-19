package com.NowakArtur97.GlobalTerrorismAPI.util;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PageHelper {

    PageImpl<T> convertListToPage(Pageable pageable, List<T> eventsCausedByGroup);
}
