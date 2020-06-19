package com.NowakArtur97.GlobalTerrorismAPI.util.page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PageHelper {

    <T> PageImpl<T> convertListToPage(Pageable pageable, List<T> list);
}
