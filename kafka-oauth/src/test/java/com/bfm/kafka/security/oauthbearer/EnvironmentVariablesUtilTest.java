/*
Copyright © 2019 BlackRock Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
//package com.bfm.kafka.security.oauthbearer;
//
//import org.junit.Test;
//
//import java.lang.reflect.Field;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.assertTrue;
//
///**
// * The type Environment variables util test.
// */
//public class EnvironmentVariablesUtilTest {
//
//	/**
//	 * Gets integer environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getIntegerEnvironmentVariable() throws Exception {
//		Map<String, String > map = new HashMap<>();
//		map.put("getIntegerEnvironmentVariable", "2");
//		setEnv(map);
//		Integer output = EnvironmentVariablesUtil.getIntegerEnvironmentVariable("getIntegerEnvironmentVariable", Integer.valueOf(1));
//		assertTrue(output == 2);
//	}
//
//	/**
//	 * Gets integer environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getIntegerNullEnvironmentVariable() throws Exception {
//		Map<String, String > map = new HashMap<>();
//		map.put("getIntegerNullEnvironmentVariable", null);
//		setEnv(map);
//		Integer output = EnvironmentVariablesUtil.getIntegerEnvironmentVariable("getIntegerNullEnvironmentVariable", Integer.valueOf(1));
//		assertTrue(output == 1);
//	}
//
//	/**
//	 * Gets float environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getFloatEnvironmentVariable()  throws Exception{
//		Map<String, String > map = new HashMap<>();
//		map.put("getFloatEnvironmentVariable", "2.0f");
//		setEnv(map);
//		Float output = EnvironmentVariablesUtil.getFloatEnvironmentVariable("getFloatEnvironmentVariable", Float.valueOf(1.0f));
//		assertTrue(output == 2.0f);
//	}
//
//	/**
//	 * Gets float environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getFloatNullEnvironmentVariable()  throws Exception{
//		Map<String, String > map = new HashMap<>();
//		map.put("getFloatEnvironmentVariable", null);
//		setEnv(map);
//		Float output = EnvironmentVariablesUtil.getFloatEnvironmentVariable("getFloatEnvironmentVariable", Float.valueOf(1.0f));
//		assertTrue(output == 1.0f);
//	}
//
//
//	/**
//	 * Gets double environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getDoubleEnvironmentVariable()  throws Exception{
//		Map<String, String > map = new HashMap<>();
//		map.put("getDoubleEnvironmentVariable", "2.0");
//		setEnv(map);
//		Double output = EnvironmentVariablesUtil.getDoubleEnvironmentVariable("getDoubleEnvironmentVariable", Double.valueOf(1.0f));
//		assertTrue(output == 2.0);
//	}
//
//
//	/**
//	 * Gets double null environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getDoubleNullEnvironmentVariable()  throws Exception{
//		Map<String, String > map = new HashMap<>();
//		map.put("getDoubleEnvironmentVariable", null);
//		setEnv(map);
//		Double output = EnvironmentVariablesUtil.getDoubleEnvironmentVariable("getDoubleEnvironmentVariable", Double.valueOf(1.0f));
//		assertTrue(output == 1.0);
//	}
//
//	/**
//	 * Gets boolean environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getBooleanEnvironmentVariable()  throws Exception{
//		Map<String, String > map = new HashMap<>();
//		map.put("getBooleanEnvironmentVariable", "false");
//		setEnv(map);
//		Boolean output = EnvironmentVariablesUtil.getBooleanEnvironmentVariable("getBooleanEnvironmentVariable", Boolean.valueOf("true"));
//		assertTrue(!output);
//	}
//
//
//	/**
//	 * Gets boolean null environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getBooleanNullEnvironmentVariable()  throws Exception{
//		Map<String, String > map = new HashMap<>();
//		map.put("getBooleanEnvironmentVariable", null);
//		setEnv(map);
//		Boolean output = EnvironmentVariablesUtil.getBooleanEnvironmentVariable("getBooleanEnvironmentVariable", Boolean.valueOf("true"));
//		assertTrue(output);
//	}
//
//	/**
//	 * Gets string environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getStringEnvironmentVariable()  throws Exception{
//		Map<String, String > map = new HashMap<>();
//		map.put("getStringEnvironmentVariable", "temp");
//		setEnv(map);
//		String output = EnvironmentVariablesUtil.getStringEnvironmentVariable("getStringEnvironmentVariable", "test");
//		assertTrue(output.equals("temp"));
//	}
//
//
//	/**
//	 * Gets string null environment variable.
//	 *
//	 * @throws Exception the exception
//	 */
//	@Test
//	public void getStringNullEnvironmentVariable()  throws Exception{
//		Map<String, String > map = new HashMap<>();
//		map.put("getStringEnvironmentVariable", null);
//		setEnv(map);
//		String output = EnvironmentVariablesUtil.getStringEnvironmentVariable("getStringEnvironmentVariable", "test");
//		assertTrue(output.equals("test"));
//	}
//
//
//	/**
//	 * Set ENV variables.
//	 * @param newenv newenv
//	 * @throws Exception Exception
//	 */
//	private static void setEnv(Map<String, String> newenv) throws Exception {
//		try {
//			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
//			Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
//			theEnvironmentField.setAccessible(true);
//			Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
//			env.putAll(newenv);
//			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
//			theCaseInsensitiveEnvironmentField.setAccessible(true);
//			Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
//			cienv.putAll(newenv);
//		} catch (NoSuchFieldException e) {
//			Class[] classes = Collections.class.getDeclaredClasses();
//			Map<String, String> env = System.getenv();
//			for(Class cl : classes) {
//				if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
//					Field field = cl.getDeclaredField("m");
//					field.setAccessible(true);
//					Object obj = field.get(env);
//					Map<String, String> map = (Map<String, String>) obj;
//					map.clear();
//					map.putAll(newenv);
//				}
//			}
//		}
//	}
//}