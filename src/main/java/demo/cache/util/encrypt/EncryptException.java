package demo.cache.util.encrypt;

public class EncryptException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public EncryptException(){
		super();
	}
	
	public EncryptException(String msg)
	{
		super(msg);
	}
	
	public EncryptException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public EncryptException(Throwable cause) {
		super(cause);
	}

}
