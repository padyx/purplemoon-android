package ch.defiant.purplesky.ch.defiant.purplesky.util;

import junit.framework.Assert;

import org.junit.Test;

import ch.defiant.purplesky.util.StringUtility;

public class StringUtilityTest {

	@Test
	public void testIsNullOrEmpty() {
		// Test 1: Null
		Assert.assertTrue(StringUtility.isNullOrEmpty(null));
		// Test 2: Empty String
		Assert.assertTrue(StringUtility.isNullOrEmpty(""));
		// Test 3: Space String
		Assert.assertFalse(StringUtility.isNullOrEmpty(" "));
		// Test 4: Some letters
		Assert.assertFalse(StringUtility.isNullOrEmpty("I am empty"));
	}

	@Test
	public void testIsNotNullOrEmpty() {
		// Test 1: Null
		Assert.assertFalse(StringUtility.isNotNullOrEmpty(null));
		// Test 2: Empty String
		Assert.assertFalse(StringUtility.isNotNullOrEmpty(""));
		// Test 3: Space String
		Assert.assertTrue(StringUtility.isNotNullOrEmpty(" "));
		// Test 4: Some letters
		Assert.assertTrue(StringUtility.isNotNullOrEmpty("I am empty"));
	}

	@Test
	public void testRemovePrefix() {
		// Test 1: Full prefix required, but not present. Should return original string
		String s = "Hell World";
		String prefix = "Hello";
		Assert.assertEquals(s, StringUtility.removePrefix(s, prefix, true));
		
		// Test 2: No full prefix required
		Assert.assertEquals(" World", StringUtility.removePrefix(s, prefix, false));

		// Test 3: Full prefix required, and present.
		s = "Hello World";
		Assert.assertEquals(" World", StringUtility.removePrefix(s, prefix, true));
		
		// Test 4: Null string - should return empty string
		Assert.assertEquals("", StringUtility.removePrefix(null, "anyprefix", true));
		Assert.assertEquals("", StringUtility.removePrefix(null, "anyprefix", false));
		
		// Test 5: Null prefix - should return original string
		Assert.assertEquals(s, StringUtility.removePrefix(s, null, false));
		Assert.assertEquals(s, StringUtility.removePrefix(s, null, true));
	}

	@Test
	public void testRemovePostfix() {
		// Test 1: Full postfix required, but not present. Should return original string
		String s = "Hello orld";
		String postfix = "World";
		Assert.assertEquals(s, StringUtility.removePostfix(s, postfix, true));
		
		// Test 2: No full postfix required
		Assert.assertEquals("Hello ", StringUtility.removePostfix(s, postfix, false));

		// Test 3: Full postfix required, and present.
		s = "Hello World";
		Assert.assertEquals("Hello ", StringUtility.removePostfix(s, postfix, true));
		
		// Test 4: Null string - should return empty string
		Assert.assertEquals("", StringUtility.removePostfix(null, "anypostfix", true));
		Assert.assertEquals("", StringUtility.removePostfix(null, "anypostfix", false));
		
		// Test 5: Null prefix - should return original string
		Assert.assertEquals(s, StringUtility.removePostfix(s, null, false));
		Assert.assertEquals(s, StringUtility.removePostfix(s, null, true));
	}

}
