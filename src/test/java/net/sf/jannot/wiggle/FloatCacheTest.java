package net.sf.jannot.wiggle;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class FloatCacheTest {
	private final Random random = new Random();
	private final FloatCache cache;

	private final int SIZE = 1000;
	private final List<Float> values = new ArrayList<>();

	public FloatCacheTest() {
		for (int n = 0; n < SIZE; n++) {
			values.add((float) (10 * Math.sin(n / 8f)));
		}
		Query data = new Query() {

			@Override
			public float[] getRawRange(final int start, final int end)
					throws IOException {
				float[] copy = new float[end - start];
				for (int n = 0; n < end - start; n++) {
					copy[n] = values.get(start + n);
				}
				return copy;
			}

			@Override
			public long size() {
				return values.size();
			}
		};
		cache = new FloatCache(data);
	}

	@Test
	public void smoke() {
	}

	@Test
	public void testGet() throws IOException {
		// cross-computed in Mathematica, using
		// t = Table[10 Sin[n/10], {n, 0, 1000}];
		// Mean[Take[t,{1,32}]
		assertEquals(4.24698, cache.getRawRange(0, 32)[0], 0.001);
		// and test that cached value also works, assuming
		// cached value is now retuened.
		assertEquals(4.24698, cache.getRawRange(0, 32)[0], 0.001);
	}

	@Test
	public void testGet3() throws IOException {
		// cross-computed in Mathematica, using
		// t = Table[10 Sin[n/10], {n, 0, 1000}];
		// Mean[Take[t,{1,32}]
		float[] samples = cache.getRawRange(2 * 32, 5 * 32);
		assertEquals(3, samples.length);
		assertEquals(-2.23174, samples[0], 0.001);
		assertEquals(4.45906, samples[1], 0.001);
		assertEquals(-3.59754, samples[2], 0.001);
	}
}
