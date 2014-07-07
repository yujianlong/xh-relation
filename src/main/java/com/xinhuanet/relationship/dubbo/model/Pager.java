package com.xinhuanet.relationship.dubbo.model;

import java.util.List;

public class Pager {

	private long startIndex;
	private int pageNumber;
	private int pageSize;
	private long recordCount;
	private int pageCount;
	private List results;

	public Pager(long startIndex, int pageNumber, int pageSize) {
		super();
		this.startIndex = startIndex;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	public long getStartIndex() {
		if (startIndex <= 0) {
			return pageNumber <= 1 ? 0L : (pageNumber - 1L) * pageSize;
		}
		return startIndex;
	}

	public void setStartIndex(long startIndex) {
		this.startIndex = startIndex;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public int getPageCount() {
		if (pageCount < 0) {
			pageCount = (int) Math.ceil((double) recordCount / pageSize);
		}
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public List getResults() {
		return results;
	}

	public void setResults(List results) {
		this.results = results;
	}
}
