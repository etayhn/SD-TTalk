package il.ac.technion.cs.sd.lib.communication;


public class Message{

	public final String from;
	public final String data;
	public int counter;
	private boolean stopped;
	
	public boolean isStoppedMessage() {
		return stopped;
	}

	public Message(String from, String data, int counter) {
		this.from= from;
		this.data = data;
		this.counter = counter;
	}
	
	public Message(String from, String data, int counter, boolean stopped) {
		this.from= from;
		this.data = data;
		this.counter = counter;
		this.stopped = stopped;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Message other = (Message) obj;
		if(!other.data.equals(data)){
			return false;
		}
		if(!other.from.equals(from)){
			return false;
		}
		return true;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	@Override
	public String toString() {
		return "[from: " + from + ", data: " + data + ", counter: " + counter + "]"; 
	}
	
}
