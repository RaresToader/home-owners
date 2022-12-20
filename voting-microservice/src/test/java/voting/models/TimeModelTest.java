package voting.models;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import voting.annotations.TestSuite;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static voting.annotations.TestSuite.TestType.BOUNDARY;

@TestSuite(testType = {BOUNDARY})
class TimeModelTest {

    static final List<Integer> BASE_ARGS = new ArrayList<>(List.of(10, 10, 10, 10, 10, 10));

    @ParameterizedTest
    @MethodSource("argGen")
    void isValidTest(int sec, int min, int hour, int day, int month, int year, boolean expected, String testdesc) {
        TimeModel tm = new TimeModel(sec, min, hour, day, month, year);
        LocalDateTime ldt = tm.createDate();
        assertEquals(expected, ldt != null, testdesc);
    }

    static Stream<Arguments> argGen() {

        List<Arguments> args = new ArrayList<>();
        args.add(argGenHelper(0, -1, "Invalid seconds"));
        args.add(argGenHelper(0, 69, "Invalid seconds"));
        args.add(argGenHelper(1, -1, "Invalid minutes"));
        args.add(argGenHelper(1, 69, "Invalid minutes"));
        args.add(argGenHelper(2, -1, "Invalid hours"));
        args.add(argGenHelper(2, 25, "Invalid hours"));
        args.add(argGenHelper(3, -1, "Invalid days"));
        args.add(argGenHelper(3, 32, "Invalid days"));
        args.add(argGenHelper(4, -1, "Invalid month"));
        args.add(argGenHelper(4, 13, "Invalid month"));
        return Stream.concat(args.stream(), // Invalid cases
                Stream.of(
                        // Contextually invalid cases
                        Arguments.of(10, 10, 10, 30, 2, 2022, false, "Feb 30"),
                        Arguments.of(10, 10, 10, 31, 4, 10, false, "April 31"),
                        Arguments.of(10, 10, 10, 29, 2, 2022, false, "Feb 29 on non-leap year"),
                        // Valid date
                        Arguments.of(10, 10, 10, 10, 10, 10, true, "Valid")
                ));
    }

    /**
     * Helper used for generating invalid cases
     *
     * @param idx   Index of argument to be replaced
     * @param val   Value to replace the argument by
     * @param cause Text description of test case
     * @return Arguments object to be added to arglist
     */
    static Arguments argGenHelper(int idx, int val, String cause) {
        BASE_ARGS.set(idx, val);
        Object[] args = Arrays.copyOf(BASE_ARGS.toArray(), 8);
        args[6] = false;
        args[7] = cause;
        Arguments ret = Arguments.of(args);
        BASE_ARGS.set(idx, 10);
        return ret;
    }
}
