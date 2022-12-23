package voting.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Documented
@Retention(CLASS)
@Target({TYPE})
public @interface TestSuite {
    /**
     * Documentation variable for indicating test suite type
     *
     * @return Type of test suite, based on enum. Multiple values allowed.
     */
    TestType[] testType() default {TestType.NONE};

    @Generated // It's an enum... why would test coverage go through this
    enum TestType {
        NONE,
        UNIT,
        INTEGRATION,
        SYSTEM,
        BOUNDARY,
        DOMAIN
    }
}
