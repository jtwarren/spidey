package com.spideyapp;

import java.util.ArrayList;
import java.util.List;

import com.spideyapp.sqlite.model.CellInfo;
import com.spideyapp.sqlite.model.Scan;

/**
 * A simple class for comparing two Scans.
 * @author jtwarren
 *
 */
public class ScanDiff {
	
	private List<CellInfo> mAdditions;
	private List<CellInfo> mSubtractions;
	
	private ScanDiff() {
		this.mAdditions = new ArrayList<CellInfo>();
		this.mSubtractions = new ArrayList<CellInfo>();
	}

	public static ScanDiff compare(Scan a, Scan b) {
		ScanDiff diff = new ScanDiff();
		
		outerloop:
		for (CellInfo cell1 : a.getCellInfos()) {
			for (CellInfo cell2 : b.getCellInfos()) {
				if (cell1.equals(cell2)) {
					continue outerloop;
				}
			}
			diff.mSubtractions.add(cell1);
		}
		
		outerloop:
		for (CellInfo cell1 : b.getCellInfos()) {
			for (CellInfo cell2 : a.getCellInfos()) {
				if (cell1.equals(cell2)) {
					continue outerloop;
				}
			}
			diff.mAdditions.add(cell1);
		}
		
		return diff;
	}

	/**
	 * @return the mAdditions
	 */
	public List<CellInfo> getAdditions() {
		return mAdditions;
	}

	/**
	 * @return the mSubtractions
	 */
	public List<CellInfo> getSubtractions() {
		return mSubtractions;
	}

}
