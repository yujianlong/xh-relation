package com.xinhuanet.relationship.common.exception;

public class XhrcRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String errorCode;

	public XhrcRuntimeException() {
		super();
	}

	public XhrcRuntimeException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public XhrcRuntimeException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public XhrcRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public XhrcRuntimeException(String message) {
		super(message);
	}

	public XhrcRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * 获取组合本异常信息与底层异常信息的异常描述
	 * 
	 * @return 异常信息
	 */
	public String getCausedByMessage() {
		Throwable nestedException = getCause();
		return new StringBuilder().append(getMessage()).append(" nested exception is ")
				.append(nestedException.getClass().getName()).append(":").append(nestedException.getMessage())
				.toString();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
