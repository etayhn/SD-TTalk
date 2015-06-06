package il.ac.technion.cs.sd.app.msg;


public interface IMessageHandler {

	default public void handle(FriendReplyMessage message){
		throw new UnsupportedOperationException();
	}
	
	default public void handle(FriendRequestMessage message){
		throw new UnsupportedOperationException();
	}
	
	default public void handle(LoginReplyMessage message) {
		throw new UnsupportedOperationException();
	}
	
	default public void handle(LoginRequestMessage message) {
		throw new UnsupportedOperationException();
	}
	
	default public void handle(OnlineCheckRequestMessage message) {
		throw new UnsupportedOperationException();
	}
	
	default public void handle(OnlineCheckReplyMessage message) {
		throw new UnsupportedOperationException();
	}
	
	default public void handle(CommonInstantMessage message) {
		throw new UnsupportedOperationException();
	}
	
	default public void handle(LogoutRequestMessage message) {
		throw new UnsupportedOperationException();
	}
	
	default public void handle(LogoutReplyMessage message) {
		throw new UnsupportedOperationException();
	}
	
	default public void handle(ReceivingApprovalMessage message) {
		throw new UnsupportedOperationException();
	}
	
	default public void handle(IMessage message) {
		throw new UnsupportedOperationException();
	}
}
