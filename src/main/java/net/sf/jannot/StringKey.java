package net.sf.jannot;

import java.util.Objects;

public class StringKey implements DataKey {

	@Override
	public String toString() {
		return key;
	}

	private String key;

	public StringKey(String key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StringKey other = (StringKey) obj;
		return Objects.equals(key, other.key);
	}

	@Override
	public int compareTo(DataKey o) {
		return o.toString().compareTo(this.toString());
	}
}