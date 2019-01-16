package com.evergarden.cms.app.plugin;

import com.google.common.eventbus.EventBus;
import com.hanami.sdk.api.Hanami;
import com.hanami.sdk.event.HanamiStartedEvent;
import com.hanami.sdk.plugin.ExtentionPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Configuration
public class PluginLoader {

	private final static String PLUGIN_FOLDER_PATH = "/out/artifacts/hello_plugin_main_liht_jar";
	private final static String PLUGIN_ROOT_FOLDER_PATH = "/plugins";

	@Bean
	public void pluginFolder() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
		String path        = getCurrentWorkingDirectory() + PLUGIN_FOLDER_PATH;
//		String fullPath    = path + "/hello-plugin_main.jar";
//		File[] directories = new File(path).listFiles(File::isDirectory);
//
//		File file = new File(fullPath);
//
//		Hanami hanami = classicLoader(file);
//
//		ExtentionPlugin jclPlugin = jcLoader(getCurrentWorkingDirectory() + PLUGIN_ROOT_FOLDER_PATH);
//
//		jclPlugin.inject(hanami);
//
//		Properties properties = getPluginProperties(file);
//
//		System.out.println(properties.getProperty("plugin_class"));
//
//		for (File directory : directories) {
//			System.out.println(directory.getName());
//		}
	}

	private Hanami classicLoader(File file) throws NoSuchMethodException, MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
		URL url = file.toURI().toURL();

		URL[] urls = new URL[]{url};

		ClassLoader cl       = new URLClassLoader(urls);
		Class       cls      = cl.loadClass("com.hanami.demoplugin.PluginOne");
		Object      instance = cls.newInstance();
		EventBus eventBus = new EventBus();

		Method[] methods = cls.getMethods();

		Hanami hanami = new Hanami();

		hanami.setEventBus(eventBus);

		Method method = cls.getMethod("inject", Hanami.class);

		method.invoke(instance, hanami);

		int timestamp = (int) (new Date().getTime() / 1000) ;
		eventBus.post(new HanamiStartedEvent(timestamp));
		return hanami;
	}

	private ExtentionPlugin jcLoader(String path) {
		JarClassLoader jcl = new JarClassLoader();
		jcl.add(path);

		JclObjectFactory factory = JclObjectFactory.getInstance();

		Object object = factory.create(jcl, "com.hanami.demoplugin.PluginOne");
		return (ExtentionPlugin) object;
	}

	private Properties getPluginProperties(File file) {
		Properties properties = new Properties();
		try {
			JarFile     jar    = new JarFile(file);
			JarEntry    entry  = jar.getJarEntry("hanami.properties");
			InputStream stream = jar.getInputStream(entry);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			properties.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	private Path findPluginFolder() {
		return FileSystems.getDefault().getPath("./");
	}

	private String getCurrentWorkingDirectory() {
		return System.getProperty("user.dir");
	}
}
