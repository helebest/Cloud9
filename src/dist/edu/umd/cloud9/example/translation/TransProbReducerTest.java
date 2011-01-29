
package edu.umd.cloud9.example.translation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.junit.Before;
import org.junit.Test;

import edu.umd.cloud9.io.pair.Pair;
import edu.umd.cloud9.io.pair.PairOfStrings;

/**
 * Test cases for the inverted index mapper.
 */
public class TransProbReducerTest extends TestCase {
 
    private Reducer<PairOfStrings, FloatWritable, PairOfStrings, FloatWritable> reducer;
    private ReduceDriver<PairOfStrings, FloatWritable, PairOfStrings, FloatWritable> driver;
 
    /** We expect pathname@offset for the key from each of these */
    private final FloatWritable EXPECTED_COUNT = new FloatWritable(1);
 
    @Before
    public void setUp() {
        reducer = new TransProbReducer();
        driver = new ReduceDriver<PairOfStrings, FloatWritable, PairOfStrings, FloatWritable>(reducer);
    }

    @Test
    public void testOnePair() {
        List<Pair<PairOfStrings, FloatWritable>> out0 = null;
        List<Pair<PairOfStrings, FloatWritable>> out1 = null;
        List<Pair<PairOfStrings, FloatWritable>> out2 = null;
 
        try {
	    List<FloatWritable> input_values = new ArrayList<FloatWritable>();

	    input_values.add(new FloatWritable(6.0f));
	    driver.withInputKey(new PairOfStrings("evil", "*"))
		.setInputValues(input_values);
	    out0 = driver.run();


	    input_values.clear();
	    input_values.add(new FloatWritable(4.0f));

	    driver.withInputKey(new PairOfStrings("evil", "mal"))
		.setInputValues(input_values);
	    out1 = driver.run();

	    input_values.clear();
	    input_values.add(new FloatWritable(1.0f));
	    input_values.add(new FloatWritable(1.0f));

	    driver.withInputKey(new PairOfStrings("evil", "malfaisant")).setInputValues(input_values);
	    out2 = driver.run();

        } catch (IOException ioe) {
            fail();
        }
 
        List<Pair<PairOfStrings, FloatWritable>> expected = new ArrayList<Pair<PairOfStrings, FloatWritable>>();
        expected.add(new Pair<PairOfStrings, FloatWritable>(new PairOfStrings("evil", "mal"), new FloatWritable(2.0f / 3.0f)));
 
        assertListEquals(expected, out1);

        expected = new ArrayList<Pair<PairOfStrings, FloatWritable>>();
 
        assertListEquals(expected, out0);

	expected.add(new Pair<PairOfStrings, FloatWritable>(new PairOfStrings("evil", "malfaisant"), new FloatWritable(1.0f / 3.0f)));
	assertListEquals(expected, out2);
    }

}