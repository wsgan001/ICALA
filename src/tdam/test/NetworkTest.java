package tdam.test;

import static org.junit.Assert.*;

import org.junit.Test;

import tdam.Network;
import tdam.Unit;

public class NetworkTest {

	@Test
	public void testGetUnit() {

		// create empty network
		Network network = new Network();

		// insert data into network
		Object data = "data";
		network.insertData(data);

		// retrieve unit by data
		Unit unit = network.getUnit(data);

		// compare data
		Object dataActual = unit.getData();
		assertEquals("data of retrieved unit", data, dataActual);

	}

	@Test
	public void testInsertData() {

		// create empty network
		Network network = new Network();

		// insert data into network
		Object data = "data";
		network.insertData(data);

		// re-insert data into network
		network.insertData(data);

		// count occurrences
		int occurrences = 0;
		for (Unit unit : network.getUnits()) {
			if (unit.getData().equals(data)) {
				occurrences++;
			}
		}

		// check number of occurrences
		assertEquals("occurrences of data", 1, occurrences);

	}

	@Test
	public void testDeleteData() {

		// create empty network
		Network network = new Network();

		// insert data into network
		Object data = "data";
		network.insertData(data);

		// delete data
		network.deleteData(data);

		// count occurrences
		int occurrences = 0;
		for (Unit unit : network.getUnits()) {
			if (unit.getData().equals(data)) {
				occurrences++;
			}
		}

		// check number of occurrences
		assertEquals("occurrences of data", 0, occurrences);

	}

	@Test
	public void testDeleteUnit() {

		// create empty network
		Network network = new Network();

		// insert data into network
		Object data = "data";
		Unit unitInserted = network.insertData(data);

		// delete unit
		network.deleteUnit(unitInserted);

		// count occurrences
		int occurrences = 0;
		for (Unit unit : network.getUnits()) {
			if (unit.getData().equals(data)) {
				occurrences++;
			}
		}

		// check number of occurrences
		assertEquals("occurrences of data", 0, occurrences);

	}

	@Test
	public void testJoinData() {

		// create empty network
		Network network = new Network();

		// insert data into network
		Object dataA = "dataA";
		Object dataB = "dataB";
		network.insertData(dataA);
		network.insertData(dataB);

		// join data
		network.joinData(dataA, dataB);

		// count occurrences
		int occurrencesA = 0;
		int occurrencesB = 0;
		for (Unit unit : network.getUnits()) {
			if (unit.getData().equals(dataA)) {
				occurrencesA++;
			}
			if (unit.getData().equals(dataB)) {
				occurrencesB++;
			}
		}

		// check number of occurrences
		assertEquals("occurrences of dataA", 1, occurrencesA);
		assertEquals("occurrences of dataB", 0, occurrencesB);

	}

	@Test
	public void testSplitData() {

		// create empty network
		Network network = new Network();

		// insert data into network
		Object dataA = "dataA";
		network.insertData(dataA);

		// split data
		Object dataB = "dataB";
		network.splitData(dataA, dataB);

		// count occurrences
		int occurrencesA = 0;
		int occurrencesB = 0;
		for (Unit unit : network.getUnits()) {
			if (unit.getData().equals(dataA)) {
				occurrencesA++;
			}
			if (unit.getData().equals(dataB)) {
				occurrencesB++;
			}
		}

		// check number of occurrences
		assertEquals("occurrences of dataA", 1, occurrencesA);
		assertEquals("occurrences of dataB", 1, occurrencesB);

	}

	@Test
	public void testCleanUp() {

		// create empty network
		Network network = new Network();

		// insert data into network
		Object data = "data";
		network.insertData(data);

		// clean up network
		network.cleanUp();

		// check number of units
		assertEquals(0, network.getUnits().size());

		// check number of associations
		assertEquals(0, network.getAssociations().size());

	}

}
