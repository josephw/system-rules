package org.junit.contrib.java.lang.system;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RestoreSystemPropertiesTest {
	private static final Description NO_DESCRIPTION = null;
	private static final String FIRST_PROPERTY = "first property";
	private static final String ARBITRARY_VALUE = "arbitrary value";
	private static final String VALUE_SET_BY_STATEMENT = "value set by statement";

	private final TestRule rule = new RestoreSystemProperties();

	@Test
	public void restoresProperty() throws Throwable {
		String originalValue = getProperty(FIRST_PROPERTY);
		Statement setValueOfProperty = new SetValueOfProperty(FIRST_PROPERTY);
		rule.apply(setValueOfProperty, NO_DESCRIPTION).evaluate();
		assertThat(getProperty(FIRST_PROPERTY), is(equalTo(originalValue)));
	}

	@Test
	public void restoresExistingProperty() throws Throwable {
		setProperty(FIRST_PROPERTY, ARBITRARY_VALUE);
		Statement setValueOfProperty = new SetValueOfProperty(FIRST_PROPERTY);
		rule.apply(setValueOfProperty, NO_DESCRIPTION).evaluate();
		assertThat(getProperty(FIRST_PROPERTY), is(equalTo(ARBITRARY_VALUE)));
	}

	@Test
	public void providesPropertyToExecutedStatement() throws Throwable {
		setProperty("dummy property", "dummy value");
		Statement verifyProperty = new Statement() {
			@Override
			public void evaluate() throws Throwable {
				assertThat(getProperty("dummy property"), is(equalTo("dummy value")));
			}
		};
		rule.apply(verifyProperty, NO_DESCRIPTION).evaluate();
	}

	private class SetValueOfProperty extends Statement {
		private final String name;

		SetValueOfProperty(String name) {
			this.name = name;
		}

		@Override
		public void evaluate() throws Throwable {
			setProperty(name, VALUE_SET_BY_STATEMENT);
		}
	}
}
