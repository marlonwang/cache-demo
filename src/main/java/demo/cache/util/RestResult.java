package demo.cache.util;

public class RestResult 
{
	// 响应状态
	private boolean status;
	
	// 错误编码
	private String errorCode;
	
	// 错误消息
	private String errorMsg;
	
	// 返回结果
	private Object resultData;
	
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Object getResultData() {
		return resultData;
	}

	public void setResultData(Object resultData) {
		this.resultData = resultData;
	}
	
}
