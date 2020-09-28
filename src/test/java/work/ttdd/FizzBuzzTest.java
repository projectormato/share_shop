package work.ttdd;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FizzBuzzTest {
    @Test
    void input1_output1() {
        assertEquals("1", fizzBuzz(1));
    }

    @Test
    void input2_output2() {
        assertEquals("2", fizzBuzz(2));
    }

    @Test
    void input3_outputFizz() {
        assertEquals("Fizz", fizzBuzz(3));
    }

    @Test
    void input6_outputFizz() {
        assertEquals("Fizz", fizzBuzz(6));
    }

    @Test
    void input5_outputBuzz() {
        assertEquals("Buzz", fizzBuzz(5));
    }

    @Test
    void input10_outputBuzz() {
        assertEquals("Buzz", fizzBuzz(10));
    }

    @Test
    void input15_outputFizzBuzz() {
        assertEquals("FizzBuzz", fizzBuzz(15));
    }

    private String fizzBuzz(int i) {
        if (i % 15 == 0) {
            return "FizzBuzz";
        }
        if (i % 5 == 0) {
            return "Buzz";
        }
        if (i % 3 == 0) {
            return "Fizz";
        }
        return String.valueOf(i);
    }
}
