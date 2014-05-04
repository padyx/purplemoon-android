package ch.defiant.purplesky.util;

public class CompareUtility {

	public static <T> boolean equals(T one, T two) {
		if (one == null || two == null)
			return (one == null && two == null);
		return one.equals(two);
	}

	public static <T> boolean notEquals(T one, T two) {
		return !equals(one, two);
	}

	public static <T extends Comparable<T>> int compare(T one, T two) {
		if (one == null) {
			if (two == null) {
				return 0;
			} else {
				return -1; // Other is null, so this one is greater
			}
		} else {
			if (two == null) {
				return 1;
			} else {
				return one.compareTo(two);
			}
		}
	}

}
