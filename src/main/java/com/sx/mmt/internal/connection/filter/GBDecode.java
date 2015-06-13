package com.sx.mmt.internal.connection.filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.Tail;
import com.sx.mmt.internal.util.SimpleBytes;

public class GBDecode extends CumulativeProtocolDecoder{
		private final AttributeKey contextKey = new AttributeKey(
				getClass(), "CONTEXT");
		
		private Context getContext(final IoSession session) {
			Context context = (Context) session.getAttribute(contextKey);
			if (context == null) {
				context = new Context();
				session.setAttribute(contextKey, context);
			}
			return context;
		}
		
		private enum DecodeStatus{
			START,HEAD,TAIL,NEXT
		}
		
		private class Context {
			private DecodeStatus decodeStatus;
			private int packetLength;
			public Context() {
				decodeStatus = DecodeStatus.START;
			}
			public DecodeStatus getDecodeStatus() {
				return decodeStatus;
			}
			public void setDecodeStatus(DecodeStatus decodeStatus) {
				this.decodeStatus = decodeStatus;
			}
			public int getPacketLength() {
				return packetLength;
			}
			public void setPacketLength(int packetLength) {
				this.packetLength = packetLength;
			}
		}

		@Override
		protected boolean doDecode(final IoSession session, final IoBuffer buffer,
				final ProtocolDecoderOutput out) {
			Context context = getContext(session);
			if (context.getDecodeStatus() == DecodeStatus.START) {
				doFindHeadTag(context, buffer);
			}
			if (context.getDecodeStatus() == DecodeStatus.HEAD) {
				doDecodeHeader(context, buffer);
			}
			if (context.getDecodeStatus() == DecodeStatus.TAIL) {
				doDecodePacket(context, buffer, out);
			}
			boolean isArrived;
			if (context.getDecodeStatus() == DecodeStatus.NEXT) {
				isArrived = true;
				context.setDecodeStatus(DecodeStatus.START);
			} else {
				isArrived = false;
			}
			return isArrived;
		}

		private void doFindHeadTag(final Context context,
				final IoBuffer buffer) {
			while (buffer.hasRemaining()) {
				if (buffer.get() == (byte) 0x68) {
					buffer.position(buffer.position() - 1);
					context.setDecodeStatus(DecodeStatus.HEAD);
					break;
				}
			}
		}

		private void doDecodeHeader(final Context context, final IoBuffer buffer) {
			if (buffer.remaining() >= Head.SEGMENT_LENGTH) {
				byte[] headData = new byte[Head.SEGMENT_LENGTH];
				buffer.get(headData,0,Head.SEGMENT_LENGTH);
				Head head=new Head(new SimpleBytes(headData));
				head.decode("","");
				if (head.check()) {
					buffer.position(buffer.position() - Head.SEGMENT_LENGTH);
					context.setDecodeStatus(DecodeStatus.TAIL);
					context.setPacketLength(head.getTotalDataLength()
							+Head.SEGMENT_LENGTH+Tail.SEGMENT_LENGTH);
				} else {
					context.setDecodeStatus(DecodeStatus.START);
				}
			}
		}

		private void doDecodePacket(final Context context, final IoBuffer buffer,
				final ProtocolDecoderOutput out) {
			int packetLength = context.getPacketLength();
			if (buffer.remaining() >= packetLength) {
				byte[] packetData = new byte[packetLength];
				buffer.get(packetData);
				SimpleBytes packet = new SimpleBytes(packetData);
				out.write(packet);
				context.setDecodeStatus(DecodeStatus.NEXT);
			}
		}

		@Override
		public void dispose(final IoSession session) {
			Context ctx = (Context) session.getAttribute(contextKey);
			if (ctx != null) {
				session.removeAttribute(contextKey);
			}
		}
}
