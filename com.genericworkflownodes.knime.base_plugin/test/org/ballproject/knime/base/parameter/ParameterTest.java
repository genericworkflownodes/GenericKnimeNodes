package org.ballproject.knime.base.parameter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.ballproject.knime.base.parameter.BoolParameter;
import org.ballproject.knime.base.parameter.DoubleListParameter;
import org.ballproject.knime.base.parameter.DoubleParameter;
import org.ballproject.knime.base.parameter.IntegerListParameter;
import org.ballproject.knime.base.parameter.IntegerParameter;
import org.ballproject.knime.base.parameter.InvalidParameterValueException;
import org.ballproject.knime.base.parameter.StringListParameter;
import org.ballproject.knime.base.parameter.StringParameter;
import org.junit.Test;

public class ParameterTest
{

	@Test
	public void test() throws InvalidParameterValueException
	{
		Locale locale = new Locale("en", "UK");
		Locale.setDefault(locale);

		IntegerParameter ip = new IntegerParameter("i",2204);
		assertEquals("2204",ip.getStringRep());
		ip.fillFromString("1979");
		assertEquals(new Integer(1979),ip.getValue());
		
		DoubleParameter dp = new DoubleParameter("d",22.04);
		assertEquals("2.204000e+01",dp.getStringRep());
		dp.fillFromString("-19.79");
		assertEquals(new Double(-19.79),dp.getValue());
		
		BoolParameter bp = new BoolParameter("b",true);
		assertEquals("true",bp.getStringRep());
		bp.fillFromString("false");
		assertEquals(false,bp.getValue());
		
		StringParameter sp = new StringParameter("s","lkwpeter");
		assertEquals("lkwpeter",sp.getStringRep());
		sp.fillFromString("1337");
		assertEquals("1337",sp.getValue());
		
		List<String> strings = new ArrayList<String>();
		strings.add("A");strings.add("B");strings.add("C");
		StringListParameter slp = new StringListParameter("slp",strings);
		assertEquals("A@@@__@@@B@@@__@@@C@@@__@@@",slp.getStringRep());
		slp.fillFromString("X@@@__@@@y@@@__@@@Z@@@__@@@");
		assertEquals(3,slp.getValue().size());
		assertEquals("X",slp.getValue().get(0));
		assertEquals("y",slp.getValue().get(1));
		assertEquals("Z",slp.getValue().get(2));
		
		List<Integer> ints = new ArrayList<Integer>();
		ints.add(-1);ints.add(2);ints.add(3);
		IntegerListParameter ilp = new IntegerListParameter("ilp",ints);
		assertEquals("-1@@@__@@@2@@@__@@@3@@@__@@@",ilp.getStringRep());
		ilp.fillFromString("-19@@@__@@@16@@@__@@@44@@@__@@@");
		assertEquals(3,ilp.getValue().size());
		assertEquals(new Integer(-19),ilp.getValue().get(0));
		assertEquals(new Integer(16),ilp.getValue().get(1));
		assertEquals(new Integer(44),ilp.getValue().get(2));
		
		List<Double> floats = new ArrayList<Double>();
		floats.add(99.1);floats.add(-12.4);floats.add(3.3);
		DoubleListParameter dlp = new DoubleListParameter("dlp",floats);
		assertEquals("9.910000e+01@@@__@@@-1.240000e+01@@@__@@@3.300000e+00@@@__@@@",dlp.getStringRep());
		dlp.fillFromString("-19@@@__@@@16@@@__@@@44@@@__@@@");
		assertEquals(3,dlp.getValue().size());
		assertEquals(new Double(-19),dlp.getValue().get(0));
		assertEquals(new Double(16),dlp.getValue().get(1));
		assertEquals(new Double(44),dlp.getValue().get(2));		
	}

}
