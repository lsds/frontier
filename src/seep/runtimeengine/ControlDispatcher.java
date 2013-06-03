package seep.runtimeengine;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;

import seep.comm.serialization.ControlTuple;
import seep.comm.serialization.controlhelpers.Ack;
import seep.comm.serialization.controlhelpers.BackupNodeState;
import seep.comm.serialization.controlhelpers.BackupOperatorState;
import seep.comm.serialization.controlhelpers.BackupRI;
import seep.comm.serialization.controlhelpers.InitNodeState;
import seep.comm.serialization.controlhelpers.InitOperatorState;
import seep.comm.serialization.controlhelpers.InitRI;
import seep.comm.serialization.controlhelpers.InvalidateState;
import seep.comm.serialization.controlhelpers.RawData;
import seep.comm.serialization.controlhelpers.ReconfigureConnection;
import seep.comm.serialization.controlhelpers.Resume;
import seep.comm.serialization.controlhelpers.ScaleOutInfo;
import seep.comm.serialization.controlhelpers.StateAck;
import seep.infrastructure.NodeManager;
import seep.operator.EndPoint;
import seep.processingunit.PUContext;

public class ControlDispatcher {

	private PUContext puCtx = null;
	private Kryo k = null;
	
	// For use when backuping large state
//	private Output controlOutput = new Output(1000000000);
	
	public ControlDispatcher(PUContext puCtx){
		this.puCtx = puCtx;
		this.k = initializeKryo();
	}
	
	private Kryo initializeKryo(){
		k = new Kryo();

		k.register(ControlTuple.class);
		k.register(HashMap.class, new MapSerializer());
		k.register(BackupOperatorState.class);
		k.register(byte[].class);
		k.register(RawData.class);
		k.register(Ack.class);
		k.register(BackupNodeState.class);
		k.register(Resume.class);
		k.register(ScaleOutInfo.class);
		k.register(StateAck.class);
		k.register(ArrayList.class);
		k.register(BackupRI.class);
		k.register(InitNodeState.class);
		k.register(InitOperatorState.class);
		k.register(InitRI.class);
		k.register(InvalidateState.class);
		k.register(ReconfigureConnection.class);
		return k;
	}
	
	public void sendAllUpstreams(ControlTuple ct){
		for(int i = 0; i < puCtx.getUpstreamTypeConnection().size(); i++) {
			sendUpstream(ct, i);
		}		
	}
	
//	public void sendUpstream2(ControlTuple ct, int index){
//		EndPoint obj = puCtx.getUpstreamTypeConnection().elementAt(index);
////		if(obj instanceof CoreRE){
////			CoreRE operatorObj = (CoreRE) obj;
////			operatorObj.processControlTuple(ct, null);
////		}
////		else if (obj instanceof SynchronousCommunicationChannel){
//			Socket socket = ((SynchronousCommunicationChannel) obj).getDownstreamControlSocket();
//			//Output output = null;
//			try{
//				//output = new Output(socket.getOutputStream(), 1000000000);
//				controlOutput.setOutputStream(socket.getOutputStream());
//				synchronized(socket){
//				synchronized (controlOutput){
//					k.writeObject(controlOutput, ct);
//					controlOutput.flush();
//				}
//				}
//			}
//			catch(IOException io){
//				NodeManager.nLogger.severe("-> Dispatcher. While sending control msg "+io.getMessage());
//				io.printStackTrace();
//			}
////		}
//	}
	
//	byte[] backupBuffer = new byte[100000000];
	
	public void sendUpstream(ControlTuple ct, int index){
		EndPoint obj = puCtx.getUpstreamTypeConnection().elementAt(index);
		Socket socket = ((SynchronousCommunicationChannel) obj).getDownstreamControlSocket();
		Output output = null;
		try{
			output = new Output(socket.getOutputStream());
			synchronized(socket){
				synchronized (output){
					k.writeObject(output, ct);
					output.flush();
				}
			}
		}
		catch(IOException io){
			NodeManager.nLogger.severe("-> Dispatcher. While sending control msg "+io.getMessage());
			io.printStackTrace();
		}
	}
	
	private Output largeOutput = new Output(1000000000);
	
	public void sendUpstream_large(ControlTuple ct, int index){
		long startSend = System.currentTimeMillis();
		EndPoint obj = puCtx.getUpstreamTypeConnection().elementAt(index);
		Socket socket = ((SynchronousCommunicationChannel) obj).getDownstreamControlSocket();
		Output output = null;
		BackupOperatorState bos = ct.getBackupState();
		System.out.println("About to send: "+bos.getOpId());
		try{
			//output = new Output(socket.getOutputStream(), 1000000000);
			largeOutput.setOutputStream(socket.getOutputStream());
			synchronized(socket){
				synchronized (largeOutput){
					long startWrite = System.currentTimeMillis();
					k.writeObject(largeOutput, ct);
					System.out.println("%*% SER SIZE: "+largeOutput.toBytes().length+" bytes");
					largeOutput.flush();
					long stopWrite = System.currentTimeMillis();
					System.out.println("% Write socket: "+(stopWrite-startWrite));
//					output.close();
				}
			}
		}
		catch(IOException io){
			NodeManager.nLogger.severe("-> Dispatcher. While sending control msg "+io.getMessage());
			io.printStackTrace();
		}
		long stopSend = System.currentTimeMillis();
		System.out.println("% Send : "+(stopSend-startSend));
	}
	
	public void sendUpstream_blind(ControlTuple ct, int index){
		long startSend = System.currentTimeMillis();
		EndPoint obj = puCtx.getUpstreamTypeConnection().elementAt(index);
		Socket socket = ((SynchronousCommunicationChannel) obj).getBlindSocket();
		Output output = null;
		BackupOperatorState bos = ct.getBackupState();
		System.out.println("About to send: "+bos.getOpId());
		try{
			largeOutput.setOutputStream(socket.getOutputStream());
			synchronized(socket){
				synchronized (largeOutput){
					long startWrite = System.currentTimeMillis();
					k.writeObject(largeOutput, ct);
					System.out.println("%*% SER SIZE: "+largeOutput.toBytes().length+" bytes");
					largeOutput.flush();
					long stopWrite = System.currentTimeMillis();
					System.out.println("% Write socket: "+(stopWrite-startWrite));
//					output.close();
				}
			}
		}
		catch(IOException io){
			NodeManager.nLogger.severe("-> Dispatcher. While sending control msg "+io.getMessage());
			io.printStackTrace();
		}
		long stopSend = System.currentTimeMillis();
		System.out.println("% Send : "+(stopSend-startSend));
	}
	
	public void sendDownstream(ControlTuple ct, int index){
		EndPoint obj = puCtx.getDownstreamTypeConnection().elementAt(index);
		if(obj instanceof CoreRE){
			CoreRE operatorObj = (CoreRE) obj;
			operatorObj.processControlTuple(ct, null);
		}
		else if (obj instanceof SynchronousCommunicationChannel){
			Socket socket = ((SynchronousCommunicationChannel) obj).getDownstreamControlSocket();
			Output output = null;
			try{
				output = new Output(socket.getOutputStream());
				synchronized (socket){
//					tuple.writeDelimitedTo(socket.getOutputStream());
					k.writeObject(output, ct);
					output.flush();
				}
			}
			catch(IOException io){
				NodeManager.nLogger.severe("-> Dispatcher. While sending control msg "+io.getMessage());
				io.printStackTrace();
			}
		}
	}
	
	public void ackControlMessage(ControlTuple genericAck, OutputStream os){
		Output output = new Output(os);
		k.writeObject(output, genericAck);
		output.flush();
	}
	
	public void initStateMessage(ControlTuple initStateMsg, OutputStream os){
		Output output = new Output(os);
		k.writeObject(output, initStateMsg);
		output.flush();
	}
	
	public Object deepCopy(Object toCopy){
		long s = System.currentTimeMillis();
		System.out.println("CLASS: "+toCopy.getClass().toString());
		k.register(toCopy.getClass());
		Object o = k.copy(toCopy);
		long e = System.currentTimeMillis();
		System.out.println("TOTAL-Kryo-SER: "+(e-s));
		return k.copy(toCopy);
	}
	
}