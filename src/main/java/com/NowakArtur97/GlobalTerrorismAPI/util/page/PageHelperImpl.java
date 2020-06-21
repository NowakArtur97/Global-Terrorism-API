package com.NowakArtur97.GlobalTerrorismAPI.util.page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PageHelperImpl implements PageHelper {

    @Override
    public <T> PageImpl<T> convertListToPage(Pageable pageable, List<T> list) {

        int startIndex = (int) pageable.getOffset();
        int endIndex = (int) getEndIndex(pageable, list);

        List<T> subList;

        if (hasOffsetExceededListSize(pageable, list)) {
            subList = new ArrayList<>();
        } else {
            subList = list.subList(startIndex, endIndex);
        }

        return new PageImpl<>(subList, pageable, list.size());
    }

    private <T> long getEndIndex(Pageable pageable, List<T> list) {

        return hasOffsetAndPageSizeExceededListSize(pageable, list) ?
                list.size() :
                pageable.getOffset() + pageable.getPageSize();
    }

    private <T> boolean hasOffsetAndPageSizeExceededListSize(Pageable pageable, List<T> list) {

        return (pageable.getOffset() + pageable.getPageSize()) > list.size();
    }

    private <T> boolean hasOffsetExceededListSize(Pageable pageable, List<T> list) {

        return pageable.getOffset() < list.size();
    }
}
