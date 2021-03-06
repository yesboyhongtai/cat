package com.dianping.cat.message.internal;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.dianping.cat.message.Message;
import com.dianping.cat.message.spi.codec.PlainTextMessageCodec;

public abstract class AbstractMessage implements Message {
	private String m_type;

	private String m_name;

	private String m_status = "unset";

	private long m_timestampInMillis;

	private CharSequence m_data;

	private boolean m_completed;
	
	public AbstractMessage(String type, String name) {
		m_type = String.valueOf(type);
		m_name = String.valueOf(name);
		m_timestampInMillis = MilliSecondTimer.currentTimeMillis();
	}

	@Override
	public void addData(String keyValuePairs) {
		if (m_data == null) {
			m_data = keyValuePairs;
		} else if (m_data instanceof StringBuilder) {
			((StringBuilder) m_data).append('&').append(keyValuePairs);
		} else {
			StringBuilder sb = new StringBuilder(m_data.length() + keyValuePairs.length() + 16);

			sb.append(m_data).append('&');
			sb.append(keyValuePairs);
			m_data = sb;
		}
	}

	@Override
	public void addData(String key, Object value) {
		if (m_data instanceof StringBuilder) {
			((StringBuilder) m_data).append('&').append(key).append('=').append(value);
		} else {
			String str = String.valueOf(value);
			int old = m_data == null ? 0 : m_data.length();
			StringBuilder sb = new StringBuilder(old + key.length() + str.length() + 16);

			if (m_data != null) {
				sb.append(m_data).append('&');
			}

			sb.append(key).append('=').append(str);
			m_data = sb;
		}
	}

	@Override
	public CharSequence getData() {
		if (m_data == null) {
			return "";
		} else {
			return m_data;
		}
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String getStatus() {
		return m_status;
	}

	@Override
	public long getTimestamp() {
		return m_timestampInMillis;
	}

	@Override
	public String getType() {
		return m_type;
	}

	@Override
	public boolean isCompleted() {
		return m_completed;
	}

	@Override
	public boolean isSuccess() {
		return Message.SUCCESS.equals(m_status);
	}

	protected void setCompleted(boolean completed) {
		m_completed = completed;
	}

	@Override
	public void setStatus(String status) {
		m_status = status;
	}

	@Override
	public void setStatus(Throwable e) {
		m_status = e.getClass().getName();
	}

	public void setTimestamp(long timestamp) {
		m_timestampInMillis = timestamp;
	}

	@Override
	public String toString() {
		PlainTextMessageCodec codec = new PlainTextMessageCodec();
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

		codec.encodeMessage(this, buf);
		return buf.toString(Charset.forName("utf-8"));
	}
}
