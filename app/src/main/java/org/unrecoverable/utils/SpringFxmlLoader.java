package org.unrecoverable.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.unrecoverable.streamboard.config.SpringApplicationConfig;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

public class SpringFxmlLoader {

	private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
			SpringApplicationConfig.class);

	public FXMLLoader getLoader() {
		FXMLLoader loader = new FXMLLoader();
		loader.setControllerFactory(new Callback<Class<?>, Object>() {
			@Override
			public Object call(Class<?> clazz) {
				return applicationContext.getBean(clazz);
			}
		});
		return loader;
	}
	
	public Object getBean(String name) {
		return applicationContext.getBean(name);
	}
}
