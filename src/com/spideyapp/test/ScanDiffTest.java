package com.spideyapp.test;

import com.spideyapp.ScanDiff;
import com.spideyapp.sqlite.model.CellInfo;
import com.spideyapp.sqlite.model.Scan;

import junit.framework.TestCase;

public class ScanDiffTest extends TestCase {
	protected Scan scan1;
	protected Scan scan2;
	
	protected CellInfo cell1;
	protected CellInfo cell2;
	protected CellInfo cell3;
	protected CellInfo cell4;
	
	protected void setUp() {
		scan1 = new Scan();
		scan2 = new Scan();
		
		cell1 = new CellInfo(1, 2, 3, 4, 0);
		cell2 = new CellInfo(2, 3, 4, 5, 0);
		cell3 = new CellInfo(3, 4, 5, 6, 0);
		cell4 = new CellInfo(4, 5, 6, 7, 0);
		
		scan1.addCellInfo(cell1);
		scan1.addCellInfo(cell2);
		
		scan2.addCellInfo(cell1);
		scan2.addCellInfo(cell2);
		scan2.addCellInfo(cell3);
		scan2.addCellInfo(cell4);
	}
	
	public void testBasicAddition() {
		ScanDiff diff = ScanDiff.compare(scan1, scan2);
		assertEquals(2, diff.getAdditions().size());
		assertEquals(0, diff.getSubtractions().size());
	}
	
	public void testAddition() {
		ScanDiff diff = ScanDiff.compare(scan1, scan2);
		assertEquals(2, diff.getAdditions().size());
		assertEquals(0, diff.getSubtractions().size());
		
		assertTrue(diff.getAdditions().contains(cell3));
		assertTrue(diff.getAdditions().contains(cell4));
	}
	
	public void testBasicSubtraction() {
		ScanDiff diff = ScanDiff.compare(scan2, scan1);
		assertEquals(0, diff.getAdditions().size());
		assertEquals(2, diff.getSubtractions().size());
	}
	
	public void testSubtraction() {
		ScanDiff diff = ScanDiff.compare(scan2, scan1);
		assertEquals(0, diff.getAdditions().size());
		assertEquals(2, diff.getSubtractions().size());
		
		assertTrue(diff.getSubtractions().contains(cell3));
		assertTrue(diff.getSubtractions().contains(cell4));
	}
}
