package it.Controllers;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({CarParkTests.class, EventoTests.class, UserTests.class,  BigliettoTests.class, ReviewTests.class})
public class TestSuite {

}
