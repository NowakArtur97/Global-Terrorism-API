package com.nowakArtur97.globalTerrorismAPI.common.util;

import com.nowakArtur97.globalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("PageUtil_Tests")
class PageUtilTest {

    private PageUtil pageUtil;

    @BeforeEach
    private void setUp() {

        pageUtil = new PageUtil();
    }

    @Test
    void when_offset_not_exceed_list_size_should_return_page_impl_with_multiple_pages() {

        int pageExpected = 1;
        int sizeExpected = 2;

        String string1 = "String1";
        String string2 = "String2";
        String string3 = "String3";
        String string4 = "String4";
        List<String> listToConvert = List.of(string1, string2, string3, string4);
        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        PageImpl pageImplActual = pageUtil.convertListToPage(pageable, listToConvert);

        assertAll(
                () -> assertFalse(pageImplActual.getContent().contains(string1), () -> "should not contain: " + string1 + ", but was: " + pageImplActual.getContent()),
                () -> assertFalse(pageImplActual.getContent().contains(string2), () -> "should not contain: " + string2 + ", but was: " + pageImplActual.getContent()),
                () -> assertTrue(pageImplActual.getContent().contains(string3), () -> "should contain: " + string3 + ", but was: " + pageImplActual.getContent()),
                () -> assertTrue(pageImplActual.getContent().contains(string4), () -> "should contain: " + string4 + ", but was: " + pageImplActual.getContent()),
                () -> assertEquals(pageExpected, pageImplActual.getNumber(), () -> "should be on: " + pageExpected + " page, but was on: " + pageImplActual.getNumber()),
                () -> assertEquals(sizeExpected, pageImplActual.getNumberOfElements(), () -> "should contain: " + sizeExpected + " elements, but was: " + pageImplActual.getNumberOfElements()),
                () -> assertEquals(listToConvert.size(), pageImplActual.getTotalElements(), () -> "should contain: " + sizeExpected + " total elements, but was: " + pageImplActual.getNumberOfElements())
        );
    }

    @Test
    void when_size_exceed_list_size_should_return_page_impl_with_one_page() {

        int pageExpected = 0;
        int sizeExpected = 8;

        String string1 = "String1";
        String string2 = "String2";
        String string3 = "String3";
        String string4 = "String4";
        List<String> listToConvert = List.of(string1, string2, string3, string4);
        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        PageImpl pageImplActual = pageUtil.convertListToPage(pageable, listToConvert);

        assertAll(
                () -> assertTrue(pageImplActual.getContent().contains(string1), () -> "should contain: " + string1 + ", but was: " + pageImplActual.getContent()),
                () -> assertTrue(pageImplActual.getContent().contains(string2), () -> "should contain: " + string2 + ", but was: " + pageImplActual.getContent()),
                () -> assertTrue(pageImplActual.getContent().contains(string3), () -> "should contain: " + string3 + ", but was: " + pageImplActual.getContent()),
                () -> assertTrue(pageImplActual.getContent().contains(string4), () -> "should contain: " + string4 + ", but was: " + pageImplActual.getContent()),
                () -> assertEquals(pageExpected, pageImplActual.getNumber(), () -> "should be on: " + pageExpected + " page, but was on: " + pageImplActual.getNumber()),
                () -> assertEquals(listToConvert.size(), pageImplActual.getNumberOfElements(), () -> "should contain: " + sizeExpected + " elements, but was: " + pageImplActual.getNumberOfElements()),
                () -> assertEquals(listToConvert.size(), pageImplActual.getTotalElements(), () -> "should contain: " + sizeExpected + " total elements, but was: " + pageImplActual.getNumberOfElements())
        );
    }

    @Test
    void when_list_is_empty_should_return_page_impl_with_empty_content() {

        int pageExpected = 0;
        int sizeExpected = 10;

        List<String> listToConvert = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageExpected, sizeExpected);

        PageImpl pageImplActual = pageUtil.convertListToPage(pageable, listToConvert);

        assertAll(
                () -> assertTrue(pageImplActual.getContent().isEmpty(), () -> "should not contain any element, but was: " + pageImplActual.getContent()),
                () -> assertEquals(pageExpected, pageImplActual.getNumber(), () -> "should be on: " + pageExpected + " page, but was on: " + pageImplActual.getNumber()),
                () -> assertEquals(listToConvert.size(), pageImplActual.getNumberOfElements(), () -> "should contain: " + sizeExpected + " elements, but was: " + pageImplActual.getNumberOfElements()),
                () -> assertEquals(listToConvert.size(), pageImplActual.getTotalElements(), () -> "should contain: " + sizeExpected + " total elements, but was: " + pageImplActual.getNumberOfElements())
        );
    }
}
