package work.ttdd;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleTest {
    @Test
    void sumTest() {
        assertEquals(2, sum(1, 1));
    }

    @Test
    void sumTest2() {
        assertEquals(4, sum(2, 2));
    }

    int sum(int a, int b) {
        return a + b;
    }
}
