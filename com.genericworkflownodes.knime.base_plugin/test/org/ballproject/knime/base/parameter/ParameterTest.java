package org.ballproject.knime.base.parameter;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.IntegerParameter;
import com.genericworkflownodes.knime.parameter.InvalidParameterValueException;
import com.genericworkflownodes.knime.parameter.StringListParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;

public class ParameterTest {

	@Test
	public void test() throws InvalidParameterValueException {
		Locale locale = new Locale("en", "UK");
		Locale.setDefault(locale);

		IntegerParameter ip = new IntegerParameter("i", 2204);
		assertEquals("2204", ip.getStringRep());
		ip.fillFromString("1979");
		assertEquals(new Integer(1979), ip.getValue());

		DoubleParameter dp = new DoubleParameter("d", 22.04);
		assertEquals("22.040000", dp.getStringRep());
		dp.fillFromString("-19.79");
		assertEquals(new Double(-19.79), dp.getValue());

		StringParameter sp = new StringParameter("s", "lkwpeter");
		assertEquals("lkwpeter", sp.getStringRep());
		sp.fillFromString("1337");
		assertEquals("1337", sp.getValue());

		List<String> strings = new ArrayList<String>();
		strings.add("A");
		strings.add("B");
		strings.add("C");
		StringListParameter slp = new StringListParameter("slp", strings);
		assertEquals("A@@@__@@@B@@@__@@@C@@@__@@@", slp.getStringRep());
		slp.fillFromString("X@@@__@@@y@@@__@@@Z@@@__@@@");
		assertEquals(3, slp.getValue().size());
		assertEquals("X", slp.getValue().get(0));
		assertEquals("y", slp.getValue().get(1));
		assertEquals("Z", slp.getValue().get(2));

		List<Integer> ints = new ArrayList<Integer>();
		ints.add(-1);
		ints.add(2);
		ints.add(3);
		IntegerListParameter ilp = new IntegerListParameter("ilp", ints);
		assertEquals("-1@@@__@@@2@@@__@@@3@@@__@@@", ilp.getStringRep());
		ilp.fillFromString("-19@@@__@@@16@@@__@@@44@@@__@@@");
		assertEquals(3, ilp.getValue().size());
		assertEquals(new Integer(-19), ilp.getValue().get(0));
		assertEquals(new Integer(16), ilp.getValue().get(1));
		assertEquals(new Integer(44), ilp.getValue().get(2));

	}

}
