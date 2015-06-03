package il.ac.technion.cs.sd.lib.communication;


public class Message{

	public final String from;
	public final String data;

	public Message(String from, String data) {
		this.from= from;
		this.data = data;
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
	
}
