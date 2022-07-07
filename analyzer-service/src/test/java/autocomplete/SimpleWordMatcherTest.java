package autocomplete;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SimpleWordMatcherTest {

    @Test
    void buildPrefixMatchers() {
    }

    @Test
    void dynamicAddNew() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(Locale.ENGLISH);//Oct 11, 2019
        String text = today.format(formatters);

        LocalDate date = LocalDate.parse("Oct 11, 2019", formatters);


//

        {
            formatters = DateTimeFormatter.ofPattern("yyyy年M月d日").withLocale(Locale.SIMPLIFIED_CHINESE);//Oct 11, 2019

            date = LocalDate.parse("2020年1月3日", formatters);
            System.out.println(date);
        }

    }
}